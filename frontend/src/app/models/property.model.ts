import { User } from './user.model';

export interface Property {
    id: number;
    title: string;
    description: string;
    address: string;
    price: number;
    status: PropertyStatus;
    owner: User | null;
    imageUrl?: string;
}

export enum PropertyStatus {
    PENDING_APPROVAL = 'PENDING_APPROVAL',
    APPROVED = 'APPROVED',
    RENTED = 'RENTED',
    UNAVAILABLE = 'UNAVAILABLE'
}
