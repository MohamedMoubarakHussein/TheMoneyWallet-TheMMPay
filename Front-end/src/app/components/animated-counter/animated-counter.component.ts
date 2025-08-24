import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnimatedCounterService } from '../../services/animated-counter.service';

@Component({
  selector: 'app-animated-counter',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './animated-counter.component.html',
})
export class AnimatedCounterComponent implements OnInit, OnChanges {
  @Input() value = 0;
  @Input() startValue = 0;
  @Input() duration = 1000;
  @Input() decimal = 0;
  @Input() prefix = '';
  @Input() suffix = '';
  @Input() label = '';
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() color: 'primary' | 'success' | 'danger' | 'warning' | 'info' | 'default' = 'default';

  displayValue = '0';

  constructor(private animatedCounterService: AnimatedCounterService) {}

  ngOnInit(): void {
    this.startAnimation();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value']) {
      this.startAnimation();
    }
  }

  private startAnimation(): void {
    this.animatedCounterService.animate(this.startValue, this.value, this.duration)
      .subscribe(value => {
        this.displayValue = value.toFixed(this.decimal);
      });
  }
}