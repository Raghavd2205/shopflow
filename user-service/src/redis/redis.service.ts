import { Injectable, OnModuleInit, OnModuleDestroy } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import Redis from 'ioredis';

@Injectable()
export class RedisService implements OnModuleInit, OnModuleDestroy {

  private client!: Redis;

  constructor(private configService: ConfigService) {}

  // Connect to Redis when module starts
  onModuleInit() {
    this.client = new Redis({
      host: this.configService.get('REDIS_HOST'),
      port: this.configService.get<number>('REDIS_PORT'),
    });

    this.client.on('connect', () => {
      console.log('Redis connected successfully');
    });

    this.client.on('error', (err) => {
      console.error('Redis error:', err);
    });
  }

  // Disconnect when module destroys
  onModuleDestroy() {
    this.client.quit();
  }

  // Set key with TTL in seconds
  async set(key: string, value: string, ttlSeconds: number): Promise<void> {
    await this.client.set(key, value, 'EX', ttlSeconds);
  }

  // Get value by key
  async get(key: string): Promise<string | null> {
    return this.client.get(key);
  }

  // Delete key
  async del(key: string): Promise<void> {
    await this.client.del(key);
  }

  // Check if key exists
  async exists(key: string): Promise<boolean> {
    const result = await this.client.exists(key);
    return result === 1;
  }
}