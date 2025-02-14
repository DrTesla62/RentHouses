// src/app/components/rental/application-management/application-management.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RentalApplicationService } from '../../../services/rental-application.service';
import { RentalApplication, ApplicationStatus } from '../../../models/rental-application.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-application-management',
  templateUrl: './application-management.component.html',
  styleUrls: ['./application-management.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class ApplicationManagementComponent implements OnInit {
  applications: RentalApplication[] = [];
  filteredApplications: RentalApplication[] = [];
  selectedApplication: RentalApplication | null = null;
  statusFilter: string = 'all';
  sortBy: string = 'date';

  constructor(
    private rentalApplicationService: RentalApplicationService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadApplications();
  }

  loadApplications() {
    const role = this.authService.getRole(); // e.g. returns 'ADMIN' or 'OWNER'
    console.log('Role application management:', role);

    if (role === 'ADMIN') {
      this.rentalApplicationService.getAllApplications().subscribe({
        next: (applications) => {
          this.applications = applications;
          this.applyFilters();
        },
        error: (error) => {
          console.error('Error loading applications for ADMIN:', error);
        }
      });
    } else if (role === 'OWNER') {
      this.rentalApplicationService.getApplicationsForOwner().subscribe({
        next: (applications) => {
          this.applications = applications;
          this.applyFilters();
        },
        error: (error) => {
          console.error('Error loading applications for OWNER:', error);
        }
      });
    }
  }

applyFilters() {
  let filtered = [...this.applications];

  // Apply status filter
  if (this.statusFilter !== 'all') {
    filtered = filtered.filter(app => app.status === this.statusFilter);
  }

  // Apply sorting
  switch (this.sortBy) {
    case 'date':
      filtered.sort((a, b) => {
        const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
        const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
        return dateB - dateA;
      });
      break;
    case 'status':
      filtered.sort((a, b) => a.status.localeCompare(b.status));
      break;
    case 'property':
      filtered.sort((a, b) => {
        const titleA = a.property?.title || '';
        const titleB = b.property?.title || '';
        return titleA.localeCompare(titleB);
      });
      break;
  }

  this.filteredApplications = filtered;
}

  getStatusBadgeClass(status: ApplicationStatus): string {
    switch (status) {
      case ApplicationStatus.PENDING:
        return 'badge-pending';
      case ApplicationStatus.APPROVED:
        return 'badge-approved';
      case ApplicationStatus.REJECTED:
        return 'badge-rejected';
      default:
        return '';
    }
  }

  viewDetails(application: RentalApplication) {
    this.selectedApplication = application;
  }

  closeModal() {
    this.selectedApplication = null;
  }

  approveApplication(application: RentalApplication) {
    if (!application.id) return;

    if (confirm('Are you sure you want to approve this application?')) {
      this.rentalApplicationService.approveApplication(application.id).subscribe({
        next: () => {
          application.status = ApplicationStatus.APPROVED;
          this.closeModal();
          this.loadApplications();
        },
        error: (error) => {
          console.error('Error approving application:', error);
        }
      });
    }
  }

  rejectApplication(application: RentalApplication) {
    if (!application.id) return;

    if (confirm('Are you sure you want to reject this application?')) {
      this.rentalApplicationService.rejectApplication(application.id).subscribe({
        next: () => {
          application.status = ApplicationStatus.REJECTED;
          this.closeModal();
          this.loadApplications();
        },
        error: (error) => {
          console.error('Error rejecting application:', error);
        }
      });
    }
  }
}
