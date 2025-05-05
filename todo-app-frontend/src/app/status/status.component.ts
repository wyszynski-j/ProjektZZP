import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { Router } from '@angular/router';

export interface Status {
  id: number;
  title: string;
  ownerId: number;
}

@Component({
  selector: 'app-status',
  standalone: true,
  templateUrl: './status.component.html',
  imports: [CommonModule, FormsModule]
})
export class StatusComponent implements OnInit {
  statuses: Status[] = [];
  formStatus: Partial<Status> = { title: '' };
  isEditing = false;
  editStatusId: number | null = null;

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
    } else {
      this.loadStatuses(token);
    }
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
      }
    });
  }

  addStatus(): void {
    const token = localStorage.getItem('token');
    if (!token || !this.formStatus.title?.trim()) return;

    this.api.createStatus({ title: this.formStatus.title }).subscribe({
      next: (newStatus) => {
        this.statuses.push(newStatus);
        this.resetForm();
      },
      error: (err) => console.error('Error adding status:', err)
    });
  }

  editStatus(status: Status): void {
    this.isEditing = true;
    this.editStatusId = status.id;
    this.formStatus.title = status.title;
  }

  updateStatus(): void {
    const token = localStorage.getItem('token');
    if (!token || this.editStatusId === null || !this.formStatus.title?.trim()) return;

    this.api.updateStatus(this.editStatusId, { title: this.formStatus.title }).subscribe({
      next: () => {
        this.loadStatuses(token);
        this.resetForm();
      },
      error: (err) => console.error('Error updating status:', err)
    });
  }

  deleteStatus(id: number): void {
    if (confirm('Are you sure you want to delete this status?')) {
      const token = localStorage.getItem('token');
      if (!token) return;
      this.api.deleteStatus(id).subscribe({
        next: () => {
          this.statuses = this.statuses.filter(status => status.id !== id);
        },
        error: (err) => console.error('Error deleting status:', err)
      });
    }
  }

  cancelEdit(): void {
    this.resetForm();
  }

  resetForm(): void {
    this.formStatus = { title: '' };
    this.editStatusId = null;
    this.isEditing = false;
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }
}
