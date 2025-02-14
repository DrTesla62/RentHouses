// src/app/components/auth/login/login.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
    CommonModule
  ]
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]], // Added minLength
      password: ['', [Validators.required, Validators.minLength(6)]]  // Added minLength
    });
  }

  ngOnInit() {
    // First check if user is already logged in
    if (this.authService.getCurrentUserValue()) {
      this.router.navigate(['/properties']);
      return;
    }
    // If not logged in, clear any stale auth data
    this.authService.logout();
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }

    console.log('Form submitted with:', this.loginForm.value);
    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: (user) => {
        console.log('Login successful, redirecting...', user);  // Added user to log
        this.router.navigate(['/properties'])
          .then(() => console.log('Navigation complete'))
          .catch(err => console.error('Navigation error:', err));
      },
      error: (error: HttpErrorResponse) => {
        console.error('Login component error:', error);
        this.loading = false;

        if (error.status === 401) {
          this.errorMessage = 'Invalid username or password';
        } else if (error.status === 403) {
          this.errorMessage = 'Access denied';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Login failed. Please try again.';
        }
      },
      complete: () => {
        console.log('Login observable completed');
        this.loading = false;
      }
    });
  }
}
