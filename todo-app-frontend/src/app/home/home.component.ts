import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  tasks: any[] = [];
  formTask = {
    id: null,
    title: '',
    description: '',
    statusId: null,
    categoryId: null
  };

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      this.loadTasks(token);
      this.loadCategories(token);
      this.loadStatuses(token);
    } else {
      this.router.navigate(['/login']);
    }
  }

  categories: any[] = [];
  statuses: any[] = [];

  loadTasks(token: string) {
    this.api.getTasks(token).subscribe({
      next: (tasks) => {
        this.tasks = tasks.filter(task => task.title);
      },
      error: (err) => {
        console.error('Error loading tasks', err);
      },
    });
  }

  loadCategories(token: string): void {
    if (!token) {
      this.router.navigate(['/login']);
    }
    this.api.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (err) => {
        console.error('Error loading categories', err);
      },
    });
  }

  loadStatuses(token: string): void {
    if (!token) {
      this.router.navigate(['/login']);
    }
    this.api.getStatuses().subscribe({
      next: (statuses) => {
        this.statuses = statuses;
      },
      error: (err) => {
        console.error('Error loading statuses', err);
      },
    });
  }

  getStatusTitle(statusId: number): string {
    const status = this.statuses.find(s => s.id === statusId);
    return status ? status.title : 'Unknown';
  }
  getCategoryName(categoryId: number): string {
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : 'Unknown';
  }
  



  isEditing = false;
  formSubmitted = false;


  addTask() {
    this.formSubmitted = true;
  
    if (!this.formTask.title?.trim() || !this.formTask.statusId || !this.formTask.categoryId) return;
  
    const token = localStorage.getItem('token');
    if (!token) return;
  
    this.api.addTask(this.formTask, token).subscribe({
      next: (newTask) => {
        this.tasks.push(newTask);
        this.resetForm();
      },
      error: (err) => console.error('Błąd dodawania taska:', err)
    });
  }

editTask(task: any) {
  this.formTask = { ...task }; 
  this.isEditing = true;
}


updateTask() {
  this.formSubmitted = true;

  if (!this.formTask.title?.trim() || !this.formTask.statusId || !this.formTask.categoryId) return;

  const token = localStorage.getItem('token');
  if (!token || !this.formTask.id) return;

  this.api.updateTask(token, this.formTask).subscribe({
    next: (updatedTask) => {
      const index = this.tasks.findIndex(t => t.id === updatedTask.id);
      if (index !== -1) {
        this.tasks[index] = updatedTask;
      }
      this.resetForm();
    },
    error: (err) => console.error('Error updating task', err),
  });
}

deleteTask(task: any) {
  const token = localStorage.getItem('token');
  if (!token || !task.id) return;

  this.api.deleteTask(token, task.id).subscribe({
    next: () => {
      this.tasks = this.tasks.filter(t => t.id !== task.id);
    },
    error: (err) => console.error('Error deleting task', err),
  });
}

cancelEdit() {
  this.resetForm();
}

resetForm() {
  this.formTask = { id: null, title: '', description: '', statusId: null, categoryId: null };
  this.isEditing = false;
  this.formSubmitted = false;
}

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  goToCategories() {
    this.router.navigate(['/category']);
  }

  goToStatuses() {
    this.router.navigate(['/status']);
  }


}
