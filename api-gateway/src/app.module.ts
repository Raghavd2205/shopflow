import {
  Module,
  NestModule,
  MiddlewareConsumer,
  RequestMethod,
} from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { AppController } from './app.controller';
import { ProxyModule } from './proxy/proxy.module';
import { ProxyMiddleware } from './proxy/proxy.middleware';
import { JwtMiddleware } from './auth/jwt.middleware';

@Module({
  imports: [
    // Load .env globally
    ConfigModule.forRoot({ isGlobal: true }),
    ProxyModule,
  ],
  controllers: [AppController],
})
export class AppModule implements NestModule {

  // Register ProxyMiddleware for ALL routes
  configure(consumer: MiddlewareConsumer) {
    consumer
        // JWT middleware runs FIRST on all routes
        .apply(JwtMiddleware)
        .forRoutes({ path: '*', method: RequestMethod.ALL })
  
        // Then proxy forwards to service
        .apply(ProxyMiddleware)
        .forRoutes({ path: '*', method: RequestMethod.ALL });
  }
}