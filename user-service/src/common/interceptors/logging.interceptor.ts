import {
  Injectable,
  NestInterceptor,
  ExecutionContext,
  CallHandler,
  Logger,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable()
export class LoggingInterceptor implements NestInterceptor {

  private readonly logger = new Logger('HTTP');

  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    const request = context.switchToHttp().getRequest();
    const { method, url, body } = request;
    const startTime = Date.now();

    // Sanitize body — remove sensitive fields before logging
    const sanitizedBody = this.sanitizeBody(body);

    this.logger.log(`→ ${method} ${url} ${JSON.stringify(sanitizedBody)}`);

    return next.handle().pipe(
      tap({
        next: () => {
          const response = context.switchToHttp().getResponse();
          const duration = Date.now() - startTime;
          this.logger.log(
            `← ${method} ${url} ${response.statusCode} ${duration}ms`,
          );
        },
        error: (error) => {
          const duration = Date.now() - startTime;
          this.logger.error(
            `← ${method} ${url} ${error.status || 500} ${duration}ms`,
          );
        },
      }),
    );
  }

  // Remove sensitive fields from logs
  private sanitizeBody(body: any): any {
    if (!body) return body;

    const sensitiveFields = ['password', 'refreshToken', 'token'];
    const sanitized = { ...body };

    sensitiveFields.forEach(field => {
      if (sanitized[field]) {
        sanitized[field] = '***';
      }
    });

    return sanitized;
  }
}