import {
    Injectable,
    ConflictException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './entities/user.entity';
import * as bcrypt from 'bcryptjs';
import { RegisterDto } from '../auth/dto/register.dto';
import { UsersRepository } from 'src/users/users.repository';
import { UpdateUsersDto } from './dto/updateUsers.dto';
@Injectable()
export class UsersService {

    constructor(
        @InjectRepository(User)
        private usersRepository: UsersRepository,
    ) { }

    async findByEmail(email: string): Promise<User | null> {
        return await this.usersRepository.findByEmail(email);
    }

    async findById(id: number): Promise<User | null> {
        return await this.usersRepository.findById(id);
    }
    async findAll():Promise<User[]|null>{
        return await this.usersRepository.findAllUsers();
    }
    async updateToken(userId: number, refreshToken: any): Promise<void> {
        await this.usersRepository.updateRefreshToken(userId,refreshToken);
    }
    async updateUserProfile(user:User,updateUserDto:UpdateUsersDto): Promise<User|null> {
        const {name} = updateUserDto;
        const userProfile= await this.usersRepository.updateProfile(user.id,name);
        return userProfile;
    }

    async createUser(registerDto: RegisterDto): Promise<User> {
        const { name, email, password } = registerDto;

        // Check duplicate email
        const existingUser = await this.findByEmail(email);
        if (existingUser) {
            throw new ConflictException('Email already exists');
        }

        // Hash password — never store plain text
        const hashedPassword = await bcrypt.hash(password, 10);

        const user = await this.usersRepository.createUser({
            name,
            email,
            password: hashedPassword,
        });

        return user;
    }

    // Remove sensitive fields from response
    sanitizeUser(user: User) {
        const { password, refreshToken, ...sanitized } = user;
        return sanitized;
    }
}