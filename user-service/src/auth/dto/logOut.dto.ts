import { IsEmail, IsNumber, IsNotEmpty } from 'class-validator';

export class LogOutDto {
    @IsNotEmpty({ message: 'Password is required' })
    @IsNumber()
    userId: number;
}