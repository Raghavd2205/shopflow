import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Logger } from '@nestjs/common';

async function bootstrap() {
  const app = await NestFactory.create(AppModule, { bodyParser: false });

  const logger = new Logger('Bootstrap');

  // Enable CORS — allows frontend to call gateway
  app.enableCors({
    origin: '*',
    methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS'],
    allowedHeaders: [
      'Content-Type',
      'Authorization',
      'X-Internal-Service-Secret',
    ],
  });

  const PORT = process.env.PORT || 3000;
  await app.listen(PORT);

  logger.log(`API Gateway running on port ${PORT}`);
  logger.log(`User Service → ${process.env.USER_SERVICE_URL}`);
  logger.log(`Product Service → ${process.env.PRODUCT_SERVICE_URL}`);
  logger.log(`Order Service → ${process.env.ORDER_SERVICE_URL}`);
}
bootstrap();
