import {
    IsEmail,
    IsString,
    MinLength,
    IsNotEmpty,
    MaxLength,
} from 'class-validator';

export class RegisterDto {

    @IsNotEmpty({ message: 'Name is required' })
    @IsString()
    @MaxLength(100)
    name: string;

    @IsEmail({}, { message: 'Please provide a valid email' })
    email: string;

    @IsString()
    @MinLength(6, { message: 'Password must be at least 6 characters' })
    password: string;
}