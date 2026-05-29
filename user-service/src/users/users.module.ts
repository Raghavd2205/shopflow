import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { UsersService } from './users.service';
import { UsersRepository } from './users.repository';
import { UsersController } from './users.controller';
import { DataSource } from 'typeorm';

@Module({
  imports: [TypeOrmModule.forFeature([User])],
  controllers: [UsersController],
  providers: [
    UsersRepository,
    {
      provide: UsersService,
      useFactory: (dataSource: DataSource) => {
        const repo = new UsersRepository(dataSource);
        return new UsersService(repo);
      },
      inject: [DataSource],
    },
  ],
  exports: [UsersService, UsersRepository],
})
export class UsersModule {}