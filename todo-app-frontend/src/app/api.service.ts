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

  addTask(task: {
    title: string;
    description: string;
    statusId: number | null;
    categoryId: number | null;
  }, token: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    return this.http.post(`${this.BASE_URL}/tasks`, task, { headers });
  }


  updateTask(token: string, task: any): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<any>(`${this.BASE_URL}/tasks/${task.id}`, task, { headers });
  }

  deleteTask(token: string, taskId: number): Observable<void> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.BASE_URL}/tasks/${taskId}`, { headers });
  }
  
  createCategory(category: { name: string }) {
    return this.http.post<Category>(`${this.BASE_URL}/categories`, category, this.getAuthHeaders());
  }
  
  getCategories() {
    return this.http.get<Category[]>(`${this.BASE_URL}/categories`, this.getAuthHeaders());
  }
  
  updateCategory(id: number, category: { name: string }) {
    return this.http.put<Category>(`${this.BASE_URL}/categories/${id}`, category, this.getAuthHeaders());
  }
  
  deleteCategory(id: number) {
    return this.http.delete<void>(`${this.BASE_URL}/categories/${id}`, this.getAuthHeaders());
  }
  
  private getAuthHeaders() {
    const token = localStorage.getItem('token') || '';
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      })
    };
  }


  
  getStatuses() {
    return this.http.get<Status[]>(`${this.BASE_URL}/statuses`, this.getAuthHeaders());
  }
  
  createStatus(status: { title: string }) {
    return this.http.post<Status>(`${this.BASE_URL}/statuses`, status, this.getAuthHeaders());
  }
  
  updateStatus(id: number, status: { title: string }) {
    return this.http.put<Status>(`${this.BASE_URL}/statuses/${id}`, status, this.getAuthHeaders());
  }
  
  deleteStatus(id: number) {
    return this.http.delete<void>(`${this.BASE_URL}/statuses/${id}`, this.getAuthHeaders());
  }
  
  
}
export interface Category {
  id: number;
  name: string;
  ownerId: number;
}
export interface Status {
  id: number;
  title: string;
  ownerId: number;
}



