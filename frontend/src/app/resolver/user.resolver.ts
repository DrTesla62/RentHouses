import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { User } from '../models/user.model';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class UserResolver implements Resolve<User> {
  constructor(private authService: AuthService) {}

  resolve(): Observable<User> {
    return this.authService.currentUser.pipe(
      filter(user => user !== null),
      take(1)
    );
  }
}
