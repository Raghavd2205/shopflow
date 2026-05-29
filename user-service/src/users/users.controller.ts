import {
    Controller,
    Post,
    Body,
    HttpCode,
    HttpStatus,
    UseGuards,
    Get,Put
} from '@nestjs/common';
import { UsersService } from './users.service';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { Roles } from '../auth/decorators/roles.decorator';
import { UpdateUsersDto } from './dto/updateUsers.dto';

@Controller('api/users')
@UseGuards(JwtAuthGuard)   // ← All routes in this controller are protected
export class UsersController {
    constructor(private usersService: UsersService) { }

    @Get('profile')
    @HttpCode(HttpStatus.OK)
    async getProfile(@CurrentUser() user: any) {
        const fullUser = await this.usersService.findById(user.userId);
        return {
            statusCode: 200,
            message: 'Profile Fetched successfully',
            data: fullUser,
        }
    }
    @Put('profile')
    @HttpCode(HttpStatus.CREATED)
    async updateProfile(@CurrentUser() user: any,@Body() updateUserDto : UpdateUsersDto) {
        console.log("user",user);
        const updatedUser = await this.usersService.updateUserProfile(user,updateUserDto);
        return {
            statusCode: 201,
            message: 'Profile Updated successfully',
            data: updatedUser,
        }
    }

    @Get('all')
    @HttpCode(HttpStatus.OK)
    @UseGuards(RolesGuard)
    @Roles('admin')
    async getAllUsers(){
        const allUsers = await  this.usersService.findAll();
        return {
            statusCode: 200,
            message: 'Users Fetched successfully',
            data: allUsers,
        }
    }
}