export interface Task {
    id?: number;
    title: string;
    description?: string;
    status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
    createdAt?: string;
    updatedAt?: string;
  }