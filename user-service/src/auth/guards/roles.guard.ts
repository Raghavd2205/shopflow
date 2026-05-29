import {
    Injectable,
    CanActivate,
    ExecutionContext,
    ForbiddenException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';

@Injectable()
export class RolesGuard implements CanActivate {

    constructor(private reflector: Reflector) { }

    canActivate(context: ExecutionContext): boolean {
        // Get required roles from @Roles decorator
        const requiredRoles = this.reflector.get<string[]>(
            'roles',
            context.getHandler(),
        );

        // No roles required — allow access
        if (!requiredRoles) return true;

        // Get user from request — set by JwtStrategy
        const { user } = context.switchToHttp().getRequest();

        // Check if user has required role
        if (!requiredRoles.includes(user.role)) {
            throw new ForbiddenException('You do not have permission');
        }

        return true;
    }
}