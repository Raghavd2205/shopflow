import { Injectable, UnauthorizedException } from '@nestjs/common';
import { UsersService } from '../users/users.service';
import { RegisterDto } from './dto/register.dto';
import { ConfigService } from '@nestjs/config';
import { JwtService } from '@nestjs/jwt';
import { LoginDto } from './dto/login.dto';
import { LogOutDto } from './dto/logOut.dto';
import { RedisService } from 'src/redis/redis.service';
import * as bcrypt from 'bcryptjs';
import { RefreshDto } from './dto/refresh.dto';
@Injectable()
export class AuthService {

    constructor(private usersService: UsersService, private jwtService: JwtService,
        private configService: ConfigService, private redisService: RedisService
    ) { }

    async register(registerDto: RegisterDto) {
        const user = await this.usersService.createUser(registerDto);
        return this.usersService.sanitizeUser(user);
    }
    async login(loginDto: LoginDto) {
        const { email, password } = loginDto;
        const user = await this.usersService.findByEmail(email);
        if (!user) {
            throw new UnauthorizedException('Invalid email or password');
        }
        const isPasswordValid = await bcrypt.compare(password, user.password);
        if (!isPasswordValid) {
            throw new UnauthorizedException('Invalid email or password');
        }
        const tokens = await this.generateTokens(user.id, email, user.role);
        const refreshToken = await this.usersService.updateToken(user.id, tokens.refreshToken);

        await this.redisService.set(`Session:${user.id}`, JSON.stringify({ userId: user.id, email: user.email, role: user.role }), 604800)
        return {
            ...tokens,
            user: this.usersService.sanitizeUser(user)
        }
    }
    async generateRefreshToken(refreshDto: RefreshDto) {
        const { refreshToken } = refreshDto;
        try {
            // Verify refresh token signature and expiry
            const payload = this.jwtService.verify(refreshToken, {
                secret: this.configService.get('REFRESH_TOKEN_SECRET'),
            });
            console.log("payload", payload);
            const user = await this.usersService.findById(payload?.userId);
            if (!user) {
                throw new UnauthorizedException('Invalid email or password');
            }
            if (user.refreshToken !== refreshToken) {
                throw new UnauthorizedException('Invalid refresh token');
            }
            const session = await this.redisService.get(`Session:${user.id}`);
            console.log("session", session)
            if (!session) {
                throw new UnauthorizedException('Session Expired - Please login again');
            }
            // Access token — short lived 15 minutes
            const accessToken = this.jwtService.sign({ userId: user.id, email: user.email, role: user.role }, {
                secret: this.configService.get('JWT_SECRET'),
                expiresIn: this.configService.get('JWT_EXPIRES_IN'),
            })
            return { accessToken };;

        }
        catch (error) {
            throw new UnauthorizedException('Invalid or expired refresh token');


        }
    }
    async logOut(logOutDto: LogOutDto) {
        this.usersService.updateToken(logOutDto.userId, null);
        this.redisService.del(`Session:${logOutDto.userId}`);
        return "Logged out successfully !";
    }
    // ─── Generate Access + Refresh Tokens ───────
    async generateTokens(userId: number, email: string, role: string) {

        const payload = { userId, email, role };

        // Access token — short lived 15 minutes
        const accessToken = this.jwtService.sign(payload, {
            secret: this.configService.get('JWT_SECRET'),
            expiresIn: this.configService.get('JWT_EXPIRES_IN'),
        });

        // Refresh token — long lived 7 days
        const refreshToken = this.jwtService.sign(payload, {
            secret: this.configService.get('REFRESH_TOKEN_SECRET'),
            expiresIn: this.configService.get('REFRESH_TOKEN_EXPIRES_IN'),
        });

        return { accessToken, refreshToken };
    }
}