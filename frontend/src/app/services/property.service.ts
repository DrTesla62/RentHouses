import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Property } from '../models/property.model';
import { PropertyStatus } from '../models/property.model';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private apiUrl = `${environment.apiUrl}/v1/properties`;

  constructor(private http: HttpClient) { }

  getAllProperties(): Observable<Property[]> {
    return this.http.get<Property[]>(this.apiUrl).pipe(
      map(properties => properties.filter(p => p.status === PropertyStatus.APPROVED))
    );
  }

  getAllPropertiesAdmin(): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.apiUrl}/admin`);
  }

  getPropertyById(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.apiUrl}/${id}`);
  }

  createProperty(property: Property): Observable<Property> {
    console.log('Creating property:', property);
    return this.http.post<Property>(this.apiUrl, property);
  }

  updateProperty(property: Property): Observable<Property> {
    return this.http.put<Property>(`${this.apiUrl}/${property.id}`, property);
  }

  deleteProperty(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getPendingProperties(): Observable<Property[]> {
    return this.getAllProperties().pipe(
      map((properties: Property[]) => properties.filter(p => p.status === PropertyStatus.PENDING_APPROVAL))
    );
  }

  approveProperty(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectProperty(id: number, reason: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/reject`, { reason });
  }
}
