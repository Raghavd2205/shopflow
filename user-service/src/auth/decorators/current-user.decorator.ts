import { createParamDecorator, ExecutionContext } from '@nestjs/common';

// Usage: @CurrentUser() user in controller
// Extracts user from request object automatically
export const CurrentUser = createParamDecorator(
    (data: unknown, ctx: ExecutionContext) => {
        const request = ctx.switchToHttp().getRequest();
        return request.user;
    },
);