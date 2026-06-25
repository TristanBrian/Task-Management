import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task.service';
import { Task } from '../task.model';
import { MatDialog } from '@angular/material/dialog';
import { TaskFormComponent } from '../task-form/task-form.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {

  tasks: Task[] = [];
  displayedColumns: string[] = ['title', 'description', 'status', 'actions'];

  constructor(private taskService: TaskService,
              private dialog: MatDialog,
              private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: data => this.tasks = data,
      error: () => this.snackBar.open('Error loading tasks', 'Close', { duration: 3000 })
    });
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(TaskFormComponent, {
      width: '500px',
      data: { task: { status: 'PENDING' } as Task }
    });
    dialogRef.afterClosed().subscribe(result => { if (result) this.loadTasks(); });
  }

  editTask(task: Task): void {
    const dialogRef = this.dialog.open(TaskFormComponent, {
      width: '500px',
      data: { task: { ...task } }
    });
    dialogRef.afterClosed().subscribe(result => { if (result) this.loadTasks(); });
  }

  deleteTask(id: number): void {
    if (confirm('Are you sure?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => this.loadTasks(),
        error: () => this.snackBar.open('Error deleting task', 'Close', { duration: 3000 })
      });
    }
  }

  updateStatus(task: Task, status: string): void {
    const updated = { ...task, status: status as any };
    this.taskService.updateTask(task.id!, updated).subscribe({
      next: () => this.loadTasks(),
      error: () => this.snackBar.open('Error updating status', 'Close', { duration: 3000 })
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING': return 'warn';
      case 'IN_PROGRESS': return 'accent';
      case 'COMPLETED': return 'primary';
      default: return '';
    }
  }
}