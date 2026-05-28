import { IsString, IsNotEmpty } from 'class-validator';

export class RefreshDto {

  @IsNotEmpty({ message: 'Refresh token is required' })
    @IsString()
    refreshToken!: string;
}