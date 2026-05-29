import {
  Injectable,
  ExecutionContext,
  UnauthorizedException,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {

  canActivate(context: ExecutionContext) {
    // Call parent canActivate — runs JwtStrategy
    return super.canActivate(context);
  }

  handleRequest(err: any, user: any, info: any) {

    // Token expired
    if (info?.name === 'TokenExpiredError') {
      throw new UnauthorizedException('Token expired');
    }

    // No token provided
    if (info?.name === 'JsonWebTokenError') {
      throw new UnauthorizedException('Invalid token');
    }

    // No token at all
    if (!user) {
      throw new UnauthorizedException('No token provided');
    }

    // Error from strategy
    if (err) {
      throw new UnauthorizedException(err.message);
    }

    return user;
  }
}