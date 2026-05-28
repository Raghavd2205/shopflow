import {
    Controller,
    Post,
    Body,
    HttpCode,
    HttpStatus,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import { RefreshDto } from './dto/refresh.dto';

@Controller('api/auth')
export class AuthController {

    constructor(private authService: AuthService) { }

    @Post('register')
    @HttpCode(HttpStatus.CREATED)
    async register(@Body() registerDto: RegisterDto) {
        const user = await this.authService.register(registerDto);
        return {
            statusCode: 201,
            message: 'User registered successfully',
            data: user,
        };
    }
    @Post('login')
    @HttpCode(HttpStatus.OK)
    async login(@Body() loginDto: LoginDto) {
        const user = await this.authService.login(loginDto);
        return {
            statusCode: 200,
            message: 'Login successful',
            data: user,
        };
    }
    @Post('refresh')
    @HttpCode(HttpStatus.OK)
    async refreshToken(@Body() refreshDto: RefreshDto) {
        const token = await this.authService.generateRefreshToken(refreshDto);
        return {
            statusCode: 200,
            message: 'Refreshed successfully',
            accessToken: token,
        };
    }
    @Post('logout')
    @HttpCode(HttpStatus.OK)
    async logOut(@Body() UserId: number) {
        const message = await this.authService.logOut(UserId);
        return {
            statusCode: 200,
            message: message
        };
    }
}