// src/app/models/user.model.ts
export interface User {
    id: number;
    username: string;
    email?: string;
    phone?: string;
    firstName?: string;
    lastName?: string;
    role: {
      id: number;
      name: string;
    };
    createdAt?: Date;
    updatedAt?: Date;
}


export interface Role {
    id: number;
    name: string;
}
