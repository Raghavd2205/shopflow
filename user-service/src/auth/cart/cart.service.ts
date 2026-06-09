import { Injectable, Logger } from "@nestjs/common";
import { RedisService } from 'src/redis/redis.service';
import { AddToCartDto } from './dto/add-to-cart.dto';
import { UpdateCartItemDto } from './dto/update-cart-item.dto';

// Cart TTL — 24 hours in seconds
const CART_TTL = 86400;

@Injectable()
export class CartService {
    private readonly logger = new Logger(CartService.name);

    constructor(private redisService: RedisService) { }

    // ─── Get Cart Key ─────────────────────────────
    private getCartKey(userId: number): string {
        return `cart:${userId}`;
    }
    async addToCart(userId: number, dto: AddToCartDto) {
        const cartKey = this.getCartKey(userId);
        console.log("cartKey",cartKey);
        const cartData = await this.redisService.get(cartKey);

        const cart = cartData ? JSON.parse(cartData) : { items: [] };

        const existingKey = cart.items.findIndex(
            (item: any) => item.productId === dto.productId
        );

        if (existingKey > -1) {
            cart.items[existingKey].quantity += dto.quantity;
            cart.items[existingKey].subtotal = cart.items[existingKey].price * cart.items[existingKey].quantity;
        }
        else {
            cart.items.push({
                productId: dto.productId,
                productName: dto.productName,
                price: dto.price,
                quantity: dto.quantity,
                subtotal: dto.price * dto.quantity,
            });
        }
        let total = 0;
        cart.items.map((item: any) => { total += item.subtotal });
        cart.totalAmount = total;
        cart.totalItems = cart.items.length;
        cart.updatedAt = new Date().toISOString();

        await this.redisService.set(cartKey, JSON.stringify(cart), CART_TTL);

        this.logger.log(`Item added to cart for user: ${userId}`);

        return cart;
    }
    async getMyCart(userId: number) {
        const cartKey = this.getCartKey(userId);
        console.log("cartKey",cartKey);
        const cartData = await this.redisService.get(cartKey);
        console.log("cartData",cartData)
        if (!cartData) {
            // Return empty cart if nothing in Redis
            return {
                items: [],
                totalAmount: 0,
                totalItems: 0,
            };
        }

        return JSON.parse(cartData);
    }
    async updateMyCart(userId: number,productId: number,dto: UpdateCartItemDto){
        const cartKey = this.getCartKey(userId);
        console.log("cartKey",cartKey);
        const cartData = await this.redisService.get(cartKey);
        console.log("cartData",cartData)
        if (!cartData) {
            // Return empty cart if nothing in Redis
            return {
                items: [],
                totalAmount: 0,
                totalItems: 0,
            };
        }
        const cart = cartData ? JSON.parse(cartData) : { items: [] };
        const existingKey = cart.items.findIndex(
            (item: any) => item.productId === productId
        );
        if(existingKey>-1){
            cart.item[existingKey].quantity = dto.quantity;
        }


    }

}