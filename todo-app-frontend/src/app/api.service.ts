import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private BASE_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}
  register(userData: { username: string, email: string, password: string }): Observable<any> {
    return this.http.post(`${this.BASE_URL}/users/register`, userData);
  }

  login(username: string, password: string): Observable<string> {
    return this.http.post(`${this.BASE_URL}/auth/login`, { username, password }, {
      responseType: 'text'
    });
  }
  getTasks(token: string): Observable<any[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`${this.BASE_URL}/tasks`, { headers });
  }

}
