import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    CreateDateColumn,
    UpdateDateColumn,
} from 'typeorm';

@Entity('users')
export class User {

    @PrimaryGeneratedColumn()
    id: number;

    @Column({ length: 100 })
    name: string;

    @Column({ unique: true, length: 150 })
    email: string;

    @Column()
    password: string;

    @Column({
        type: 'enum',
        enum: ['customer', 'admin'],
        default: 'customer',
    })
    role: string;

    @Column({ nullable: true, type: 'text' })
    refreshToken: string;

    @CreateDateColumn()
    createdAt: Date;

    @UpdateDateColumn()
    updatedAt: Date;
}