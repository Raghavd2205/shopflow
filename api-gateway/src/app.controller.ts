import { Controller, Get } from '@nestjs/common';

@Controller()
export class AppController {

  @Get('api/v1/health')
  health() {
    return {
      statusCode: 200,
      message: 'API Gateway is running',
      data: {
        status: 'UP',
        service: 'API Gateway',
        timestamp: new Date().toISOString(),
        routes: {
          userService: process.env.USER_SERVICE_URL,
          productService: process.env.PRODUCT_SERVICE_URL,
          orderService: process.env.ORDER_SERVICE_URL,
        },
      },
    };
  }
}