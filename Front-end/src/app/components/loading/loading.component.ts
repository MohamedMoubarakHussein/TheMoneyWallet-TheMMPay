import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, animate, transition } from '@angular/animations';
import { LoadingService } from '../../services/loading-service.service';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.scss'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-out', style({ opacity: 0 }))
      ])
    ])
  ]
})
export class LoadingComponent implements OnInit {
  @Input() type: 'default' | 'dots' | 'progress' | 'bars' | 'pulse' = 'default';
  @Input() message: string = 'Loading';
  @Input() subtitle: string = '';
  @Input() progress: number = 0;
  
  isLoading: boolean = false;

  constructor(private loadingService: LoadingService) {}

  ngOnInit(): void {
    this.loadingService.isLoading$.subscribe(isLoading => {
      this.isLoading = isLoading;
    });
  }

  /**
   * Updates the progress value for progress type spinner
   * @param value Progress value (0-100)
   */
  updateProgress(value: number): void {
    this.progress = Math.min(Math.max(0, value), 100);
  }
}