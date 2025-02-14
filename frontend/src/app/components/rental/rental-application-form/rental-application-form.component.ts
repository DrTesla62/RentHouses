import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RentalApplicationService } from '../../../services/rental-application.service';
import { PropertyService } from '../../../services/property.service';
import { AuthService } from '../../../services/auth.service';
import { Property, PropertyStatus } from '../../../models/property.model';
import { ApplicationStatus } from '../../../models/rental-application.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-rental-application-form',
  templateUrl: './rental-application-form.component.html',
  styleUrls: ['./rental-application-form.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule]
})
export class RentalApplicationFormComponent implements OnInit {
  applicationForm: FormGroup;
  submitted = false;
  loading = false;
  property?: Property;
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private rentalApplicationService: RentalApplicationService,
    private propertyService: PropertyService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.applicationForm = this.formBuilder.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      employer: ['', Validators.required],
      monthlyIncome: ['', [Validators.required, Validators.min(0)]],
      message: [''],
      agreeToTerms: [false, Validators.requiredTrue]
    });
  }

  ngOnInit() {
    // First check if user is logged in
    const currentUser = this.authService.getCurrentUserValue();
    console.log('Current user from auth service:', currentUser); // Debug log

    if (!currentUser) {
      console.log('No current user found, redirecting to login'); // Debug log
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url }
      });
      return;
    }

    this.loading = true;
    const propertyId = Number(this.route.snapshot.paramMap.get('id'));
    console.log('Loading property with ID:', propertyId); // Debug log

    // Load property details
    this.propertyService.getPropertyById(propertyId).subscribe({
      next: (property) => {
        console.log('Loaded property:', property); // Debug log
        this.property = property;

        // Check if property is available for applications
        if (property.status !== PropertyStatus.APPROVED) {
          console.log('Property not approved. Status:', property.status); // Debug log
          this.errorMessage = 'This property is not currently available for rental applications.';
          return;
        }

        // Pre-fill form with current user data
        this.applicationForm.patchValue({
          fullName: currentUser.username,
          email: currentUser.email || '',
          phone: currentUser.phone || ''
        });
        console.log('Form initialized with values:', this.applicationForm.value); // Debug log
      },
      error: (error) => {
        console.error('Error loading property:', error);
        this.errorMessage = 'Unable to load property details. Please try again later.';
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  get f() {
    return this.applicationForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    this.errorMessage = '';

    if (this.applicationForm.invalid || !this.property) {
      console.log('Form validation failed:', {
        formValid: !this.applicationForm.invalid,
        propertyExists: !!this.property,
        formErrors: this.applicationForm.errors,
        formValue: this.applicationForm.value
      }); // Debug log
      return;
    }

    const currentUser = this.authService.getCurrentUserValue();
    console.log('Current user at submission:', currentUser); // Debug log

    if (!currentUser) {
      console.log('No user found at submission time, redirecting to login'); // Debug log
      this.router.navigate(['/login']);
      return;
    }

    // Double check property status before submission
    this.loading = true;
    this.propertyService.getPropertyById(this.property.id).subscribe({
      next: (freshProperty) => {
        console.log('Fresh property data:', freshProperty); // Debug log

        if (freshProperty.status !== PropertyStatus.APPROVED) {
          console.log('Property no longer approved. Current status:', freshProperty.status); // Debug log
          this.errorMessage = 'This property is no longer available for rental applications.';
          this.loading = false;
          return;
        }

        const application = {
          renter: {
            username: currentUser.username,
            role: currentUser.role
          },
          property: {
            id: freshProperty.id,
            status: freshProperty.status
          },
          status: ApplicationStatus.PENDING,
          message: this.applicationForm.get('message')?.value || ''
        };

        console.log('Submitting application with data:', application); // Debug log
        console.log('Full current user object:', currentUser); // Debug log

        this.rentalApplicationService.createApplication(application).subscribe({
          next: () => {
            console.log('Application submitted successfully'); // Debug log
            this.router.navigate(['/applications']);
          },
          error: (error: HttpErrorResponse) => {
            console.error('Error submitting application:', error);
            console.log('Error response:', error.error); // Debug log
            console.log('Error status:', error.status); // Debug log

            if (error.status === 403) {
              this.errorMessage = 'Cannot submit application. The property may no longer be available.';
            } else if (error.status === 404) {
              this.errorMessage = 'Unable to process application. Please try logging in again.';
              this.authService.logout();
              this.router.navigate(['/login']);
            } else if (error.error?.message) {
              this.errorMessage = error.error.message;
            } else {
              this.errorMessage = 'Failed to submit application. Please try again later.';
            }
            this.loading = false;
          }
        });
      },
      error: (error) => {
        console.error('Error verifying property status:', error);
        this.errorMessage = 'Unable to verify property availability. Please try again later.';
        this.loading = false;
      }
    });
  }

  onCancel() {
    this.router.navigate(['/properties', this.property?.id]);
  }
}
