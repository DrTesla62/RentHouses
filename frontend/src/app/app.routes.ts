// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { PropertyListComponent } from './components/property/property-list/property-list.component';
import { PropertyDetailComponent } from './components/property/property-detail/property-detail.component';
import { PropertyFormComponent } from './components/property/property-form/property-form.component';
import { RentalApplicationFormComponent } from './components/rental/rental-application-form/rental-application-form.component';
import { ApplicationManagementComponent } from './components/rental/application-management/application-management.component';
import { PropertyApprovalComponent } from './components/admin/property-approval/property-approval.component';
import { UserProfileComponent } from './components/user/profile/user-profile.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Public routes
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'properties', component: PropertyListComponent },  // Public property list


  // Protected routes
  {
    path: 'profile',
    component: UserProfileComponent,
    canActivate: [authGuard]
  },
  {
    path: 'properties/new',
    component: PropertyFormComponent,
    canActivate: [authGuard],
    data: { roles: ['OWNER'] }
  },
  {
    path: 'properties/:id/apply',
    component: RentalApplicationFormComponent,
    canActivate: [authGuard],
    data: { roles: ['RENTER'] }
  },
  {
    path: 'applications',
    component: ApplicationManagementComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'applications/owner',
    component: ApplicationManagementComponent,
    canActivate: [authGuard],
    data: { roles: ['OWNER'] }
  },
  {
    path: 'property-approval',
    component: PropertyApprovalComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },
  { path: 'properties/:id', component: PropertyDetailComponent },
  {
    path: 'properties/:id/edit',
    component: PropertyFormComponent,
    canActivate: [authGuard],
    data: { roles: ['OWNER'] }
  },

  // Default routes
  { path: '', redirectTo: '/properties', pathMatch: 'full' },
  { path: '**', redirectTo: '/properties' }
];
