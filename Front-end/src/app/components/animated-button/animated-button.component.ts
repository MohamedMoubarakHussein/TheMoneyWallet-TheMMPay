import { Component, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonAnimationService } from '../../services/button-animation.service';

@Component({
  selector: 'app-animated-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './animated-button.component.html',
  animations: [ButtonAnimationService.prototype.buttonState]
})
export class AnimatedButtonComponent {
  @Input() label = 'Button';
  @Input() type: 'primary' | 'secondary' | 'success' | 'danger' = 'primary';
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() iconLeft = '';
  @Input() iconRight = '';
  @Input() disabled = false;
  @Input() loading = false;
  
  @Output() buttonClick = new EventEmitter<MouseEvent>();
  
  state: 'normal' | 'pressed' = 'normal';

  @HostListener('mousedown') onMouseDown() { if (!this.disabled && !this.loading) this.state = 'pressed'; }
  @HostListener('mouseup') onMouseUp() { this.state = 'normal'; }
  @HostListener('mouseleave') onMouseLeave() { this.state = 'normal'; }

  constructor() {}

  onClick(event: MouseEvent) {
    if (!this.disabled && !this.loading) {
      this.buttonClick.emit(event);
    }
  }
}