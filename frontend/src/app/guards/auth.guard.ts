// src/app/guards/auth.guard.ts
import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable, of } from 'rxjs';
import { map, take, catchError } from 'rxjs/operators';

export const authGuard = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return authService.currentUser.pipe(
    take(1),
    map(user => {
      console.log('Auth Guard - Current user:', user);
      console.log('Auth Guard - Required roles:', route.data['roles']);
      console.log('Auth Guard - User role:', user?.role?.name);

      if (!user) {
        router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
        return false;
      }

      if (route.data['roles'] && !route.data['roles'].includes(user.role?.name)) {
        console.log('Auth Guard - Role not authorized');
        router.navigate([''], {
          queryParams: { error: 'insufficient-permissions' }
        });
        return false;
      }

      return true;
    })
  );
};
