// src/app/components/user/profile/user-profile.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class UserProfileComponent implements OnInit {
  user: User | null = null;
  profileForm: FormGroup;
  passwordForm: FormGroup;
  isEditMode = false;
  showPasswordForm = false;
  loading = false;
  error = '';
  success = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private userService: UserService
  ) {
    this.profileForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      firstName: [''],
      lastName: ['']
    });

    this.passwordForm = this.formBuilder.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, {
      validator: this.passwordMatchValidator
    });
  }

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile() {
    this.user = this.authService.getCurrentUserValue();
    if (this.user) {
      this.profileForm.patchValue({
        username: this.user.username,
        email: this.user.email || '',
        phone: this.user.phone || '',
        firstName: this.user.firstName || '',
        lastName: this.user.lastName || ''
      });
    }
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
    if (!this.isEditMode) {
      this.loadUserProfile(); // Reset form to original values
    }
  }

  togglePasswordForm() {
    this.showPasswordForm = !this.showPasswordForm;
    if (!this.showPasswordForm) {
      this.passwordForm.reset();
    }
  }

  onProfileSubmit() {
    if (this.profileForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const updatedProfile = {
      ...this.user,
      ...this.profileForm.value
    };

    this.userService.updateProfile(updatedProfile).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.success = 'Profile updated successfully';
        this.isEditMode = false;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to update profile';
        this.loading = false;
        console.error('Error updating profile:', error);
      }
    });
  }

  onPasswordSubmit() {
    if (this.passwordForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const { currentPassword, newPassword } = this.passwordForm.value;

    this.userService.changePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.success = 'Password changed successfully';
        this.showPasswordForm = false;
        this.passwordForm.reset();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to change password';
        this.loading = false;
        console.error('Error changing password:', error);
      }
    });
  }

  private passwordMatchValidator(g: FormGroup) {
    return g.get('newPassword')?.value === g.get('confirmPassword')?.value
      ? null
      : { mismatch: true };
  }
}
