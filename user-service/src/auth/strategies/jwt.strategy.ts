import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';
import { UsersService } from '../../users/users.service';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {

    constructor(
        private configService: ConfigService,
        private usersService: UsersService,
    ) {
        super({
            // Extract token from Authorization: Bearer <token> header
            jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),

            // Reject expired tokens
            ignoreExpiration: false,

            // Secret key to verify token signature
            secretOrKey: configService.get('JWT_SECRET')||'',
        });
    }

    // Called automatically after token is verified
    // Whatever we return here gets attached to request.user
    async validate(payload: any) {
        const user = await this.usersService.findById(payload.userId);

        if (!user) {
            throw new UnauthorizedException('User not found');
        }

        // This gets attached to request.user
        return {
            userId: payload.userId,
            email: payload.email,
            role: payload.role,
        };
    }
}