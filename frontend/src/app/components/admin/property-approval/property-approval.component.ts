// property-approval.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PropertyService } from '../../../services/property.service';
import { Property, PropertyStatus } from '../../../models/property.model';

@Component({
  selector: 'app-property-approval',
  templateUrl: './property-approval.component.html',
  styleUrls: ['./property-approval.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class PropertyApprovalComponent implements OnInit {
  pendingProperties: Property[] = [];
  selectedProperty: Property | null = null;
  loading = false;
  error = '';

  constructor(private propertyService: PropertyService) {}

  ngOnInit() {
    this.loadPendingProperties();
  }

  loadPendingProperties() {
    this.loading = true;
    this.error = '';

    this.propertyService.getAllPropertiesAdmin().subscribe({
      next: (properties) => {
        this.pendingProperties = properties.filter(
          p => p.status === PropertyStatus.PENDING_APPROVAL
        );
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading properties:', error);
        this.error = 'Failed to load pending properties';
        this.loading = false;
      }
    });
  }

  approveProperty(id: number) {
    if (!id || !confirm('Are you sure you want to approve this property?')) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.propertyService.approveProperty(id).subscribe({
      next: () => {
        this.loadPendingProperties();
        this.selectedProperty = null;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error approving property:', error);
        this.error = 'Failed to approve property. Please try again.';
        this.loading = false;
      }
    });
  }

  rejectProperty(id: number) {
    if (!id) return;

    const reason = prompt('Please provide a reason for rejection:');
    if (!reason) return;

    this.loading = true;
    this.error = '';

    this.propertyService.rejectProperty(id, reason).subscribe({
      next: () => {
        this.loadPendingProperties();
        this.selectedProperty = null;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error rejecting property:', error);
        this.error = 'Failed to reject property. Please try again.';
        this.loading = false;
      }
    });
  }

  viewDetails(property: Property) {
    this.selectedProperty = property;
  }
}
