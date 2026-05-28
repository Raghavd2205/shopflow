import { Module, Global } from '@nestjs/common';
import { RedisService } from './redis.service';

// @Global makes RedisService available everywhere
// without importing RedisModule in every module
@Global()
@Module({
  providers: [RedisService],
  exports: [RedisService],
})
export class RedisModule {}