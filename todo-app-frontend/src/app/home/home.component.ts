import { Component } from '@angular/core';
import { ApiService } from '../api.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  tasks: any[] = [];

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

  addTask() {
  }

  editTask(task: any) {
  }

  deleteTask(task: any) {

  }

}
