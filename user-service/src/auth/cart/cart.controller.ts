import {
    Controller,
    Get,
    Post,
    Put,
    Delete,
    Body,
    Param,
    ParseIntPipe,
    HttpCode,
    HttpStatus,
    UseGuards,
} from '@nestjs/common';
import { CartService } from './cart.service';
import { AddToCartDto } from './dto/add-to-cart.dto';
import { UpdateCartItemDto } from './dto/update-cart-item.dto';
import { JwtAuthGuard } from 'src/auth/guards/jwt-auth.guard';
import { CurrentUser } from 'src/auth/decorators/current-user.decorator';
import { http } from 'winston';

@Controller('api/v1/cart')
@UseGuards(JwtAuthGuard)  // ← all cart routes require login
export class CartController {
    constructor(private cartService: CartService) { }
    @Post('add')
    @HttpCode(HttpStatus.OK)
    async addToCart(@CurrentUser() user: any, @Body() dto: AddToCartDto) {
        const cart = await this.cartService.addToCart(user.userId, dto);
        return {
            statusCode: 201,
            message: 'Item added to cart successfully',
            data: cart,
        };
    }
    @Get()
    @HttpCode(HttpStatus.OK)
    async getMyCart(@CurrentUser() user: any) {
        const cart = await this.cartService.getMyCart(user.userId);
        return {
            statusCode: 200,
            message: 'Cart fetched successfully',
            data: cart,
        };
    }
}