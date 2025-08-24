import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, animate, transition } from '@angular/animations';
import { LoadingService } from '../../services/loading-service.service';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading.component.html',
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
  @Input() type: 'default' | 'dots' | 'progress' = 'default';
  @Input() message = 'Loading...';
  @Input() progress = 0;
  
  isLoading = false;

  constructor(private loadingService: LoadingService) {}

  ngOnInit(): void {
    this.loadingService.isLoading$.subscribe(isLoading => {
      this.isLoading = isLoading;
    });
  }
}
