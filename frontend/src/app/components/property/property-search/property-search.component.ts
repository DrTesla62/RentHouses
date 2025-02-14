// src/app/components/property/property-search/property-search.component.ts
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Property, PropertyStatus } from '../../../models/property.model';

export interface PropertyFilter {
  minPrice?: number;
  maxPrice?: number;
  status?: PropertyStatus;
  searchTerm?: string;
}

@Component({
  selector: 'app-property-search',
  templateUrl: './property-search.component.html',
  styleUrls: ['./property-search.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class PropertySearchComponent implements OnInit {
  @Output() filterChange = new EventEmitter<PropertyFilter>();

  filterForm: FormGroup;
  propertyStatuses = Object.values(PropertyStatus);
  showAdvancedFilters = false;

  constructor(private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      searchTerm: [''],
      minPrice: [''],
      maxPrice: [''],
      status: ['']
    });
  }

  ngOnInit() {
    // Subscribe to form changes and emit filter updates
    this.filterForm.valueChanges.subscribe(values => {
      const filters: PropertyFilter = {};

      if (values.searchTerm) {
        filters.searchTerm = values.searchTerm;
      }

      if (values.minPrice) {
        filters.minPrice = Number(values.minPrice);
      }

      if (values.maxPrice) {
        filters.maxPrice = Number(values.maxPrice);
      }

      if (values.status) {
        filters.status = values.status;
      }

      this.filterChange.emit(filters);
    });
  }

  toggleAdvancedFilters() {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  clearFilters() {
    this.filterForm.reset();
  }

  formatStatus(status: string): string {
    return status.replace(/_/g, ' ').toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}
