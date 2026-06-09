import { IsNumber, IsNotEmpty, Min } from 'class-validator';

export class UpdateCartItemDto {

    @IsNotEmpty({ message: 'Quantity is required' })
    @IsNumber()
    @Min(1, { message: 'Quantity must be at least 1' })
    quantity: number;
}