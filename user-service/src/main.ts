import { NestFactory } from '@nestjs/core';
import { ValidationPipe, Logger } from '@nestjs/common';
import { AppModule } from './app.module';
import { GlobalExceptionFilter } from './common/filters/http-exception.filter';
import { LoggingInterceptor } from './common/interceptors/logging.interceptor';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  const logger = new Logger('Bootstrap');

  // Global validation pipe
  app.useGlobalPipes(new ValidationPipe({
    whitelist: true,
    forbidNonWhitelisted: true,
    transform: true,
  }));

  // Global exception filter — consistent error responses
  app.useGlobalFilters(new GlobalExceptionFilter());

  // Global logging interceptor — log every request
  app.useGlobalInterceptors(new LoggingInterceptor());

  const PORT = process.env.PORT || 3001;
  await app.listen(PORT);

  logger.log(`User Service running on port ${PORT}`);
}
bootstrap();