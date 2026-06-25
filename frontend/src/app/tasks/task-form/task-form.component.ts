import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TaskService } from '../task.service';
import { Task } from '../task.model';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.scss']   // ✅ SCSS is separate
})
export class TaskFormComponent {
  form: FormGroup;
  isEdit: boolean;
  loading = false;

  statusOptions = [
    { value: 'PENDING', label: 'Pending', color: '#f44336' },
    { value: 'IN_PROGRESS', label: 'In Progress', color: '#ff9800' },
    { value: 'COMPLETED', label: 'Completed', color: '#4caf50' }
  ];

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private dialogRef: MatDialogRef<TaskFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { task: Task },
    private snackBar: MatSnackBar
  ) {
    this.isEdit = !!data.task.id;
    this.form = this.fb.group({
      title: [data.task.title || '', [Validators.required, Validators.minLength(3)]],
      description: [data.task.description || '', [Validators.maxLength(500)]],
      status: [data.task.status || 'PENDING', Validators.required]
    });
  }

  get titleControl() { return this.form.get('title'); }
  get descriptionControl() { return this.form.get('description'); }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    const taskData = this.form.value;
    const operation = this.isEdit
      ? this.taskService.updateTask(this.data.task.id!, taskData)
      : this.taskService.createTask(taskData);
    operation.subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open(this.isEdit ? '✅ Task updated' : '✅ Task created', 'Close', { duration: 3000, panelClass: 'success-snackbar' });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.loading = false;
        this.snackBar.open(err.error?.error || '❌ Something went wrong', 'Close', { duration: 5000, panelClass: 'error-snackbar' });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}