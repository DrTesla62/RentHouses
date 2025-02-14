// src/app/services/rental-application.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RentalApplication } from '../models/rental-application.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RentalApplicationService {
  private apiUrl = `${environment.apiUrl}/v1/rental-applications`;

  constructor(private http: HttpClient) { }

  createApplication(application: RentalApplication): Observable<RentalApplication> {
    return this.http.post<RentalApplication>(this.apiUrl, application);
  }

  getApplicationById(id: number): Observable<RentalApplication> {
    return this.http.get<RentalApplication>(`${this.apiUrl}/${id}`);
  }

  getAllApplications(): Observable<RentalApplication[]> {
    return this.http.get<RentalApplication[]>(this.apiUrl);
  }

  getApplicationsForOwner(): Observable<RentalApplication[]> {
    return this.http.get<RentalApplication[]>(`${this.apiUrl}/owner`);
  }

  approveApplication(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectApplication(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/reject`, {});
  }
}
