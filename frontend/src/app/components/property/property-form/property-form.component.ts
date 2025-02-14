// src/app/components/property/property-form/property-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PropertyService } from '../../../services/property.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-property-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mt-4">
      <div class="card">
        <div class="card-header">
          <h2>Add New Property</h2>
        </div>
        <div class="card-body">
          <form [formGroup]="propertyForm" (ngSubmit)="onSubmit()">
            <div class="mb-3">
              <label class="form-label">Title</label>
              <input type="text" class="form-control" formControlName="title">
              <div *ngIf="propertyForm.get('title')?.errors?.['required'] && propertyForm.get('title')?.touched"
                   class="text-danger">
                Title is required
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Description</label>
              <textarea class="form-control" formControlName="description" rows="3"></textarea>
              <div *ngIf="propertyForm.get('description')?.errors?.['required'] && propertyForm.get('description')?.touched"
                   class="text-danger">
                Description is required
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Address</label>
              <input type="text" class="form-control" formControlName="address">
              <div *ngIf="propertyForm.get('address')?.errors?.['required'] && propertyForm.get('address')?.touched"
                   class="text-danger">
                Address is required
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Price (per month)</label>
              <div class="input-group">
                <span class="input-group-text">$</span>
                <input type="number" class="form-control" formControlName="price">
              </div>
              <div *ngIf="propertyForm.get('price')?.errors?.['required'] && propertyForm.get('price')?.touched"
                   class="text-danger">
                Price is required
              </div>
              <div *ngIf="propertyForm.get('price')?.errors?.['min']" class="text-danger">
                Price must be greater than 0
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Image URL</label>
              <input type="text" class="form-control" formControlName="imageUrl">
            </div>

            <div class="d-grid gap-2">
              <button type="submit" class="btn btn-primary" [disabled]="propertyForm.invalid">
                Submit Property
              </button>
              <button type="button" class="btn btn-secondary" (click)="onCancel()">
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `
})
export class PropertyFormComponent implements OnInit {
  propertyForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private propertyService: PropertyService,
    private router: Router
  ) {
    this.propertyForm = this.fb.group({
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      address: ['', [Validators.required]],
      price: ['', [Validators.required, Validators.min(0)]],
      imageUrl: ['']
    });
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.propertyForm.valid) {
      const property = {
        ...this.propertyForm.value,
        status: 'PENDING_APPROVAL'
      };

      this.propertyService.createProperty(property).subscribe({
        next: () => {
          // Navigate to property list after successful creation
          this.router.navigate(['/properties']);
        },
        error: (error) => {
          console.error('Error creating property:', error);
          // Handle error (show error message to user)
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/properties']);
  }
}
