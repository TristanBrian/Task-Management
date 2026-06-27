import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TaskService } from '../task.service';
import { Task } from '../task.model';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.scss']
})
export class TaskFormComponent {
  form: FormGroup;
  isEdit: boolean;
  loading = false;

  statusOptions = [
    { value: 'PENDING', label: 'Pending', color: '#e0e0e0' },
    { value: 'IN_PROGRESS', label: 'In Progress', color: '#2196f3' },
    { value: 'COMPLETED', label: 'Completed', color: '#4caf50' }
  ];

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private dialogRef: MatDialogRef<TaskFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { task?: Task } | null,
    private snackBar: MatSnackBar
  ) {
    // Explicitly casting to Partial<Task> fixes the compilation error
    const currentTask = (data?.task || {}) as Partial<Task>;
    this.isEdit = !!currentTask.id;

    this.form = this.fb.group({
      title: [currentTask.title || '', [Validators.required, Validators.minLength(3)]],
      description: [currentTask.description || '', [Validators.maxLength(500)]],
      status: [currentTask.status || 'PENDING', Validators.required]
    });
  }

  get titleControl() { return this.form.get('title'); }
  get descriptionControl() { return this.form.get('description'); }

  getSelectedStatusColor(): string {
    const value = this.form.get('status')?.value;
    return this.statusOptions.find(o => o.value === value)?.color || '#9e9e9e';
  }

  getSelectedStatusLabel(): string {
    const value = this.form.get('status')?.value;
    return this.statusOptions.find(o => o.value === value)?.label || '';
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    
    this.loading = true;
    const taskData = this.form.value;
    
    const operation = this.isEdit && this.data?.task?.id
      ? this.taskService.updateTask(this.data.task.id, taskData)
      : this.taskService.createTask(taskData);

    operation.subscribe({
      next: () => {
        this.loading = false;
        this.showSnackBar(this.isEdit ? '✅ Task updated successfully' : '✅ Task created successfully', 'success-snackbar');
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.loading = false;
        const errMsg = err?.error?.error || '❌ Something went wrong';
        this.showSnackBar(errMsg, 'error-snackbar', 5000);
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  private showSnackBar(message: string, panelClass: string, duration = 3000): void {
    this.snackBar.open(message, 'Close', { duration, panelClass });
  }
}