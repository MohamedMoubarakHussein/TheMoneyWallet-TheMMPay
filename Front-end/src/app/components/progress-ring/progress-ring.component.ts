import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-progress-ring',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './progress-ring.component.html',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ])
    ]),
  ]
})
export class ProgressRingComponent implements OnChanges {
  @Input() value = 0;
  @Input() size = 100;
  @Input() thickness = 8;
  @Input() color = '#3b82f6';
  @Input() showValue = true;
  @Input() label = '';

  radius = 0;
  circumference = 0;
  strokeDashoffset = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value'] || changes['size'] || changes['thickness']) {
      this.update();
    }
  }

  private update(): void {
    this.radius = (this.size / 2) - (this.thickness * 2);
    this.circumference = this.radius * 2 * Math.PI;
    this.strokeDashoffset = this.circumference - (this.value / 100) * this.circumference;
  }
}