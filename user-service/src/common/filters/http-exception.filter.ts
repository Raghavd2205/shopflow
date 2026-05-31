import {
  ExceptionFilter,
  Catch,
  ArgumentsHost,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { Request, Response } from 'express';

// @Catch() with no args catches ALL exceptions
@Catch()
export class GlobalExceptionFilter implements ExceptionFilter {

  private readonly logger = new Logger(GlobalExceptionFilter.name);

  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request>();

    // Determine status code
    const status = exception instanceof HttpException
      ? exception.getStatus()
      : HttpStatus.INTERNAL_SERVER_ERROR;

    // Determine message
    let message: string | string[];

    if (exception instanceof HttpException) {
      const exceptionResponse = exception.getResponse();

      // Handle validation errors — array of messages
      if (typeof exceptionResponse === 'object' && 'message' in exceptionResponse) {
        message = (exceptionResponse as any).message;
      } else {
        message = exception.message;
      }
    } else {
      // Unknown error — don't expose internals
      message = 'Internal server error';

      // Log full error for debugging
      this.logger.error(
        `Unhandled exception: ${exception}`,
        exception instanceof Error ? exception.stack : '',
      );
    }

    // Consistent error response
    response.status(status).json({
      statusCode: status,
      message,
      error: HttpStatus[status],
      timestamp: new Date().toISOString(),
      path: request.url,
    });
  }
}