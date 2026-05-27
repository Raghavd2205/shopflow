import { IsEmail, IsString, IsNotEmpty } from 'class-validator';

export class LoginDto {

    @IsEmail({}, { message: 'Please provide a valid email' })
    email: string;

    @IsNotEmpty({ message: 'Password is required' })
    @IsString()
    password: string;
}