import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Category } from '../api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-category',
  standalone: true,
  templateUrl: './category.component.html',
  imports: [CommonModule, FormsModule]
})
export class CategoryComponent implements OnInit {
  categories: Category[] = [];
  formCategory: Partial<Category> = { name: '' };
  isEditing = false;
  editCategoryId: number | null = null;

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
    } else {
      this.loadCategories(token);
    }
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
      }
    });
  }

  addCategory(): void {
    const token = localStorage.getItem('token');
    if (!token || !this.formCategory.name?.trim()) return;

    this.api.createCategory({ name: this.formCategory.name }).subscribe({
      next: (newCategory) => {
        this.categories.push(newCategory);
        this.resetForm();
      },
      error: (err) => console.error('Error adding category:', err)
    });
  }

  editCategory(category: Category): void {
    this.isEditing = true;
    this.editCategoryId = category.id;
    this.formCategory.name = category.name;
  }

  updateCategory(): void {
    const token = localStorage.getItem('token');
    if (!token || this.editCategoryId === null || !this.formCategory.name?.trim()) return;

    this.api.updateCategory(this.editCategoryId, { name: this.formCategory.name }).subscribe({
      next: () => {
        this.loadCategories(token);
        this.resetForm();
      },
      error: (err) => console.error('Error updating category:', err)
    });
  }

  deleteCategory(id: number): void {
    if (confirm('Are you sure you want to delete this category?')) {
      const token = localStorage.getItem('token');
      if (!token) return;
      this.api.deleteCategory(id).subscribe({
        next: () => {
          this.categories = this.categories.filter(category => category.id !== id);
        },
        error: (err) => console.error('Error deleting category:', err)
      });
    }
  }

  cancelEdit(): void {
    this.resetForm();
  }

  resetForm(): void {
    this.formCategory = { name: '' };
    this.editCategoryId = null;
    this.isEditing = false;
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }
}
