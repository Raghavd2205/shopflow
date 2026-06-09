import { Injectable, NestMiddleware, Logger } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { createProxyMiddleware, Options } from 'http-proxy-middleware';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class ProxyMiddleware implements NestMiddleware {

    private readonly logger = new Logger('ProxyMiddleware');
    private proxies: { [key: string]: any } = {};

    constructor(private configService: ConfigService) {
        // Create proxy instances ONCE at startup
        // Not on every request — this was causing hanging
        const userServiceUrl = this.configService.get('USER_SERVICE_URL');
        const productServiceUrl = this.configService.get('PRODUCT_SERVICE_URL');
        const orderServiceUrl = this.configService.get('ORDER_SERVICE_URL');

        this.proxies['user'] = createProxyMiddleware({
            target: userServiceUrl,
            changeOrigin: true,
        });

        this.proxies['product'] = createProxyMiddleware({
            target: productServiceUrl,
            changeOrigin: true,
        });

        this.proxies['order'] = createProxyMiddleware({
            target: orderServiceUrl,
            changeOrigin: true,
        });
    }

    use(req: Request, res: Response, next: NextFunction) {
        const url = req.path;
        const proxyKey = this.getProxyKey(url);

        if (!proxyKey) {
            next();
            return;
        }

        this.logger.log(`${req.method} ${url} → ${proxyKey} service`);

        // Use pre-created proxy instance
        this.proxies[proxyKey](req, res, next);
    }

    private getProxyKey(url: string): string | null {

        if (
            url.startsWith('/api/auth') ||
            url.startsWith('/api/users') ||
            url.startsWith('/api/v1/cart')
        ) {
            return 'user';
        }

        if (
            url.startsWith('/api/v1/product') ||
            url.startsWith('/api/v1/category')
        ) {
            return 'product';
        }

        if (
            url.startsWith('/api/v1/order') ||
            url.startsWith('/api/v1/admin')
        ) {
            return 'order';
        }

        return null;
    }
}