import { Injectable } from '@nestjs/common';
import { DataSource, Repository } from 'typeorm';
import { User } from './entities/user.entity';

@Injectable()
export class UsersRepository extends Repository<User> {

    constructor(private dataSource: DataSource) {
        super(User, dataSource.createEntityManager());
    }

    // ─── Find by Email ───────────────────────────
    async findByEmail(email: string): Promise<User | null> {
        return this.createQueryBuilder('user')
            .where('user.email = :email', { email })
            .getOne();
    }

    // ─── Find by ID ──────────────────────────────
    async findById(id: number): Promise<User | null> {
        return this.createQueryBuilder('user') .select([
            'user.id',
            'user.name',
            'user.email',
            'user.role',
            'user.createdAt',
        ])
            .where('user.id = :id', { id })
            .getOne();
    }

    // ─── Create User ─────────────────────────────
    async createUser(userData: Partial<User>): Promise<User> {
        const user = this.create(userData);
        return this.save(user);
    }
    async updateProfile(userId:number,name:string): Promise<User | null> {
        await this.createQueryBuilder()
            .update(User)
            .set({ name })
            .where('id = :id', { id: userId })
            .execute();
            return this.findById(userId);
    }
    // ─── Update Refresh Token ────────────────────
    async updateRefreshToken(
        userId: number,
        refreshToken: string,
    ): Promise<void> {
        await this.createQueryBuilder()
            .update(User)
            .set({ refreshToken })
            .where('id = :id', { id: userId })
            .execute();
    }

    // ─── Find All Users (Admin) ──────────────────
    async findAllUsers(): Promise<User[]> {
        return this.createQueryBuilder('user')
            .select([
                'user.id',
                'user.name',
                'user.email',
                'user.role',
                'user.createdAt',
            ])
            .orderBy('user.createdAt', 'DESC')
            .getMany();
    }

    // ─── Find by Role ────────────────────────────
    async findByRole(role: string): Promise<User[]> {
        return this.createQueryBuilder('user')
            .where('user.role = :role', { role })
            .getMany();
    }
}