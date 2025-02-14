// src/app/models/rental-application.model.ts
import { User } from './user.model';
import { Property } from './property.model';

export interface RentalApplication {
    id?: number;
    renter: Partial<User>;
    property: Partial<Property>;
    createdAt?: Date;
    status: ApplicationStatus;
    message?: string;
}

export enum ApplicationStatus {
    PENDING = 'PENDING',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED'
}
