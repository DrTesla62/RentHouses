import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RegisterRequest } from '../../../models/auth.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
    CommonModule
  ]
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  loading = false;
  errorMessage = '';
  availableRoles = ['RENTER', 'OWNER'];

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required]
    });
  }

  ngOnInit() {
    if (this.authService.getCurrentUserValue()) {
      this.router.navigate(['/']);
    }
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const registerData: RegisterRequest = this.registerForm.value;

    this.authService.register(registerData).subscribe({
      next: () => {
        this.loading = false;
        alert('Registration successful! Please login.');
        this.router.navigate(['/login']);
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        if (error.status === 400) {
          this.errorMessage = error.error?.message || 'Username is already taken';
        } else if (error.status === 403) {
          this.errorMessage = 'Registration is currently not allowed';
        } else {
          this.errorMessage = 'Registration failed. Please try again later.';
        }
        console.error('Registration error:', error);
      }
    });
  }
}
