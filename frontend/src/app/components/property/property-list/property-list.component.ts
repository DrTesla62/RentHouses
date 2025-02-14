import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PropertyService } from '../../../services/property.service';
import { AuthService } from '../../../services/auth.service';
import { Property, PropertyStatus } from '../../../models/property.model';

@Component({
  selector: 'app-property-list',
  templateUrl: './property-list.component.html',
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class PropertyListComponent implements OnInit {
  properties: Property[] = [];
  filteredProperties: Property[] = [];
  loading = false;
  error = '';
  isOwner = false;

  constructor(
    private propertyService: PropertyService,
    private authService: AuthService
  ) {
    const currentUser = this.authService.getCurrentUserValue();
    this.isOwner = currentUser?.role?.name === 'OWNER';
  }

  ngOnInit() {
    this.loadProperties();
  }

  loadProperties() {
    this.loading = true;
    this.propertyService.getAllProperties().subscribe({
      next: (properties) => {
        this.properties = properties;
        this.filteredProperties = properties;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading properties:', error);
        this.error = 'Failed to load properties';
        this.loading = false;
      }
    });
  }
}
