import { Injectable, NestMiddleware, Logger } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { ConfigService } from '@nestjs/config';
import * as jwt from 'jsonwebtoken';

// Public routes — no token needed
const PUBLIC_ROUTES = [
    { path: '/api/auth/register', method: 'POST' },
    { path: '/api/auth/login', method: 'POST' },
    { path: '/api/auth/refresh', method: 'POST' },
    { path: '/health', method: 'GET' },
];

// Public route prefixes — all methods allowed
const PUBLIC_PREFIXES = [
    { prefix: '/api/v1/product', method: 'GET' },
    { prefix: '/api/v1/category', method: 'GET' },
];

@Injectable()
export class JwtMiddleware implements NestMiddleware {

    private readonly logger = new Logger('JwtMiddleware');

    constructor(private configService: ConfigService) { }

    use(req: Request, res: Response, next: NextFunction) {
        const { path, method } = req;

        // Step 1 — Check if public route
        if (this.isPublicRoute(path, method)) {
            next();
            return;
        }

        // Step 2 — Extract token from header
        const token = this.extractToken(req);

        if (!token) {
            res.status(401).json({
                statusCode: 401,
                message: 'Unauthorized — token required',
                error: 'Unauthorized',
            });
            return;
        }

        // Step 3 — Verify token
        try {
            const secret :any= this.configService.get<string>('JWT_SECRET');
            const payload = jwt.verify(token, secret) as any;

            // Step 4 — Add user info to headers
            // Services can read these headers directly
            req.headers['x-user-id'] = String(payload.userId);
            req.headers['x-user-email'] = payload.email;
            req.headers['x-user-role'] = payload.role;

            this.logger.debug(
                `Token valid for user: ${payload.email} role: ${payload.role}`
            );

            next();

        } catch (error) {

            if (error.name === 'TokenExpiredError') {
                res.status(401).json({
                    statusCode: 401,
                    message: 'Token expired — please login again',
                    error: 'Unauthorized',
                });
                return;
            }

            res.status(401).json({
                statusCode: 401,
                message: 'Invalid token',
                error: 'Unauthorized',
            });
        }
    }

    // ─── Check Public Route ───────────────────────
    private isPublicRoute(path: string, method: string): boolean {

        // Check exact public routes
        const isExactPublic = PUBLIC_ROUTES.some(
            route => route.path === path && route.method === method
        );

        if (isExactPublic) return true;

        // Check public prefixes
        const isPrefixPublic = PUBLIC_PREFIXES.some(
            route => path.startsWith(route.prefix) && route.method === method
        );

        return isPrefixPublic;
    }

    // ─── Extract Token ────────────────────────────
    private extractToken(req: Request): string | null {
        const authHeader = req.headers.authorization;

        if (authHeader && authHeader.startsWith('Bearer ')) {
            return authHeader.substring(7);
        }

        return null;
    }
}