import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-progress-bar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './progress-bar.component.html',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ])
    ]),
  ]
})
export class ProgressBarComponent {
  @Input() value = 0;
  @Input() type: 'primary' | 'success' | 'warning' | 'danger' | 'info' = 'primary';
  @Input() showPercentage = true;
  @Input() label = '';
}