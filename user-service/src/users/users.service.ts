import {
    Injectable,
    ConflictException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './entities/user.entity';
import * as bcrypt from 'bcryptjs';
import { RegisterDto } from '../auth/dto/register.dto';

@Injectable()
export class UsersService {

    constructor(
        @InjectRepository(User)
        private usersRepository: Repository<User>,
    ) { }

    async findByEmail(email: string): Promise<User | null> {
        return this.usersRepository.findOne({ where: { email } });
    }

    async findById(id: number): Promise<User | null> {
        return this.usersRepository.findOne({ where: { id } });
    }
    async updateToken(userId:number,refreshToken:string): Promise<void>{
     await this.usersRepository.update(userId,{refreshToken});
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

        const user = this.usersRepository.create({
            name,
            email,
            password: hashedPassword,
        });

        return this.usersRepository.save(user);
    }

    // Remove sensitive fields from response
    sanitizeUser(user: User) {
        const { password, refreshToken, ...sanitized } = user;
        return sanitized;
    }
}