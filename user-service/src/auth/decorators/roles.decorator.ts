import { SetMetadata } from '@nestjs/common';

// Usage: @Roles('admin') on any route
export const Roles = (...roles: string[]) =>
    SetMetadata('roles', roles);