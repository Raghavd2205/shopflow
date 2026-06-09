import { IsNotEmpty, IsNumber, IsPositive, IsString, Min } from 'class-validator';

export class AddToCartDto {

    @IsNotEmpty({ message: 'Product ID is required' })
    @IsNumber()
    productId: number;

    @IsNotEmpty({ message: 'Product name is required' })
    @IsString()
    productName: string;

    @IsNotEmpty({ message: 'Price is required' })
    @IsNumber()
    @IsPositive({ message: 'Price must be positive' })
    price: number;

    @IsNotEmpty({ message: 'Quantity is required' })
    @IsNumber()
    @Min(1, { message: 'Quantity must be at least 1' })
    quantity: number;
}