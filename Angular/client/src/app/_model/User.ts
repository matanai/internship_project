import { Role } from "./Role";

export class User
{
    id!: string;
    firstName!: string;
    lastName!: string;
    username!: string;
    enabled!: boolean;
    authorities!: any;
    roles!: Role[];
}