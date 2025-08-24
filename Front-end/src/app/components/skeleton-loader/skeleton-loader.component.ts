import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-skeleton-loader',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './skeleton-loader.component.html',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ])
    ]),
  ]
})
export class SkeletonLoaderComponent {
  @Input() type: 'text' | 'avatar' | 'card' = 'text';
  @Input() width = '100%';
  @Input() height = '1rem';
  @Input() rounded = false;
}