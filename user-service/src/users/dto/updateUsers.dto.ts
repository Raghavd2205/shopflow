import { IsString, IsNotEmpty } from 'class-validator';

export class UpdateUsersDto {
    @IsNotEmpty({ message: 'Name is required' })
    @IsString()
    name: string;
}