import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AuthModule } from 'src/auth/auth.module'
import { UsersModule } from './users/users.module';
import { User } from './users/entities/user.entity';
import { RedisModule } from './redis/redis.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),

    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      useFactory: (config: ConfigService) => ({
        type: 'mysql',
        host: config.get('DB_HOST'),
        port: parseInt(config.get('DB_PORT') ?? '3306', 10),
        username: config.get('DB_USER'),
        password: config.get('DB_PASSWORD'),
        database: config.get('DB_NAME'),
        entities: [User],
        // Bellow line Automatically modifies your database table to match
        synchronize: true,
        logging: false,
      }),
      inject: [ConfigService],
    }),

    AuthModule,
    UsersModule,
    RedisModule
  ],
})
export class AppModule {}