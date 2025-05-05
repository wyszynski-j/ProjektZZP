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
    status: 'TODO',
    categoryId: null
  };

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      this.loadTasks(token);
    } else {
      this.router.navigate(['/login']);
    }
  }

  loadTasks(token: string) {
    this.api.getTasks(token).subscribe({
      next: (tasks) => {
        this.tasks = tasks.filter(task => task.title && task.status);
      },
      error: (err) => {
        console.error('Error loading tasks', err);
      },
    });
  }



  isEditing = false;

  addTask() {
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
  this.formTask = { ...task }; // shallow copy
  this.isEditing = true;
}

updateTask() {
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
  this.formTask = { id: null, title: '', description: '', status: 'TODO', categoryId: null };
  this.isEditing = false;
}

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

}
