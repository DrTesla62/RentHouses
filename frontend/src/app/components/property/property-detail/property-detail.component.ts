import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ActivatedRoute, Router } from '@angular/router';
import { PropertyService } from '../../../services/property.service';
import { AuthService } from '../../../services/auth.service';
import { Property, PropertyStatus } from '../../../models/property.model';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-property-detail',
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class PropertyDetailComponent implements OnInit {
  property?: Property;
  currentUser?: User | null;
  isRenter = false;
  isOwner = false;
  isAdmin = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Get the property ID from the route parameters
    const id = Number(this.route.snapshot.paramMap.get('id'));

    // Load the property details
    this.propertyService.getPropertyById(id).subscribe({
      next: (property) => {
        this.property = property;
      },
      error: (error) => {
        console.error('Error loading property:', error);
        this.router.navigate(['/properties']);
      }
    });

    // Get current user and check roles
    this.currentUser = this.authService.getCurrentUserValue();
    if (this.currentUser) {
      this.isRenter = this.currentUser.role.name === 'RENTER';
      this.isOwner = this.currentUser.role.name === 'OWNER';
      this.isAdmin = this.currentUser.role.name === 'ADMIN';
    }
  }

  getStatusBadgeClass(): string {
    if (!this.property) return '';

    switch (this.property.status) {
      case PropertyStatus.PENDING_APPROVAL:
        return 'badge-pending';
      case PropertyStatus.APPROVED:
        return 'badge-approved';
      case PropertyStatus.RENTED:
        return 'badge-rented';
      case PropertyStatus.UNAVAILABLE:
        return 'badge-unavailable';
      default:
        return '';
    }
  }

  applyForRental() {
    this.router.navigate(['/properties', this.property?.id, 'apply']);
  }

  editProperty() {
    this.router.navigate(['/properties', this.property?.id, 'edit']);
  }

  deleteProperty() {
    if (!this.property?.id || !confirm('Are you sure you want to delete this property?')) {
      return;
    }

    this.propertyService.deleteProperty(this.property.id).subscribe({
      next: () => {
        this.router.navigate(['/properties']);
      },
      error: (error) => {
        console.error('Error deleting property:', error);
      }
    });
  }

  approveProperty() {
    if (!this.property?.id) return;

    this.propertyService.approveProperty(this.property.id).subscribe({
      next: () => {
        if (this.property) {
          this.property.status = PropertyStatus.APPROVED;
        }
      },
      error: (error) => {
        console.error('Error approving property:', error);
      }
    });
  }

  contactOwner() {
    // Implement contact functionality
    // This could open a modal with a contact form or messaging system
    console.log('Contact owner functionality to be implemented');
  }
}
