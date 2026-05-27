import {
    Controller,
    Post,
    Body,
    HttpCode,
    HttpStatus,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { RegisterDto } from './dto/register.dto';

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
}