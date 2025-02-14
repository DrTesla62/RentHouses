// src/app/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  console.log('AuthInterceptor - Request URL:', req.url);
  console.log('AuthInterceptor - Token found:', token ? 'yes' : 'no');

  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: token
      }
    });
    console.log('AuthInterceptor - Headers after clone:', clonedRequest.headers.get('Authorization'));
    return next(clonedRequest);
  }

  return next(req);
};
