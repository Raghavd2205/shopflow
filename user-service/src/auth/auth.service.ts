import { Injectable,UnauthorizedException } from '@nestjs/common';
import { UsersService } from '../users/users.service';
import { RegisterDto } from './dto/register.dto';
import { ConfigService } from '@nestjs/config';
import { JwtService } from '@nestjs/jwt';
import { LoginDto } from './dto/login.dto';
import * as bcrypt from 'bcryptjs';
@Injectable()
export class AuthService {

    constructor(private usersService: UsersService, private jwtService: JwtService,
        private configService: ConfigService,
    ) { }

    async register(registerDto: RegisterDto) {
        const user = await this.usersService.createUser(registerDto);
        return this.usersService.sanitizeUser(user);
    }
    async login(loginDto:LoginDto){
        const { email, password } = loginDto;
        const user = await this.usersService.findByEmail(email);
        if(!user){
            throw new UnauthorizedException('Invalid email or password');
        }
        const isPasswordValid = await bcrypt.compare(password, user.password);
        if(!isPasswordValid){
            throw new UnauthorizedException('Invalid email or password');
        }
        const tokens = await this.generateTokens(user.id,email,user.role);
        const refreshToken = await this.usersService.updateToken(user.id,tokens.refreshToken);
        return {
            ...tokens,
            user
        }
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