import { Component, Input, Output, EventEmitter, HostListener, ElementRef, Renderer2, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-animated-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      class="animated-button"
      [ngClass]="[type, size, loading ? 'loading' : '', disabled ? 'disabled' : '']"
      [disabled]="disabled || loading"
      [@buttonState]="state"
      type="button"
    >
      <!-- Loading Spinner -->
      <span class="spinner" *ngIf="loading">
        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none" stroke-dasharray="30 150" />
        </svg>
      </span>
      
      <!-- Button Content -->
      <span class="button-content" [class.hidden]="loading">
        <!-- Icon (Left) -->
        <span class="icon icon-left" *ngIf="iconLeft">
          {{ iconLeft }}
        </span>
        
        <!-- Button Label -->
        <span class="label">{{ label }}</span>
        
        <!-- Icon (Right) -->
        <span class="icon icon-right" *ngIf="iconRight">
          {{ iconRight }}
        </span>
        
        <!-- Ripple Effect Container -->
        <span class="ripple-container"></span>
      </span>
    </button>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
    
    .animated-button {
      position: relative;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border: none;
      border-radius: 0.5rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;
      overflow: hidden;
    }
    
    /* Size Variants */
    .animated-button.small {
      padding: 0.5rem 1rem;
      font-size: 0.75rem;
    }
    
    .animated-button.medium {
      padding: 0.75rem 1.5rem;
      font-size: 0.875rem;
    }
    
    .animated-button.large {
      padding: 1rem 2rem;
      font-size: 1rem;
    }
    
    /* Type Variants */
    .animated-button.primary {
      background-color: #3b82f6;
      color: white;
    }
    
    .animated-button.primary:hover:not(.disabled):not(.loading) {
      background-color: #2563eb;
    }
    
    .animated-button.secondary {
      background-color: #f8fafc;
      color: #1e293b;
      border: 1px solid #e2e8f0;
    }
    
    .animated-button.secondary:hover:not(.disabled):not(.loading) {
      background-color: #f1f5f9;
    }
    
    .animated-button.success {
      background-color: #10b981;
      color: white;
    }
    
    .animated-button.success:hover:not(.disabled):not(.loading) {
      background-color: #059669;
    }
    
    .animated-button.danger {
      background-color: #ef4444;
      color: white;
    }
    
    .animated-button.danger:hover:not(.disabled):not(.loading) {
      background-color: #dc2626;
    }
    
    .animated-button.warning {
      background-color: #f59e0b;
      color: white;
    }
    
    .animated-button.warning:hover:not(.disabled):not(.loading) {
      background-color: #d97706;
    }
    
    .animated-button.info {
      background-color: #3b82f6;
      color: white;
    }
    
    .animated-button.info:hover:not(.disabled):not(.loading) {
      background-color: #2563eb;
    }
    
    .animated-button.outline {
      background-color: transparent;
      border: 1px solid currentColor;
    }
    
    .animated-button.outline.primary {
      color: #3b82f6;
    }
    
    .animated-button.outline.success {
      color: #10b981;
    }
    
    .animated-button.outline.danger {
      color: #ef4444;
    }
    
    .animated-button.outline.warning {
      color: #f59e0b;
    }
    
    .animated-button.outline.info {
      color: #3b82f6;
    }
    
    .animated-button.outline:hover:not(.disabled):not(.loading) {
      background-color: rgba(0, 0, 0, 0.05);
    }
    
    /* Ghost style */
    .animated-button.ghost {
      background-color: transparent;
    }
    
    .animated-button.ghost.primary {
      color: #3b82f6;
    }
    
    .animated-button.ghost.success {
      color: #10b981;
    }
    
    .animated-button.ghost.danger {
      color: #ef4444;
    }
    
    .animated-button.ghost.warning {
      color: #f59e0b;
    }
    
    .animated-button.ghost.info {
      color: #3b82f6;
    }
    
    .animated-button.ghost:hover:not(.disabled):not(.loading) {
      background-color: rgba(0, 0, 0, 0.05);
    }
    
    /* Disabled state */
    .animated-button.disabled {
      opacity: 0.65;
      cursor: not-allowed;
      pointer-events: none;
    }
    
    /* Loading state */
    .animated-button.loading {
      cursor: wait;
    }
    
    .spinner {
      display: flex;
      align-items: center;
      justify-content: center;
      animation: spin 1s linear infinite;
      width: 1.5rem;
      height: 1.5rem;
    }
    
    .spinner svg {
      width: 100%;
      height: 100%;
    }
    
    .button-content {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      transition: transform 0.2s ease, opacity 0.2s ease;
    }
    
    .button-content.hidden {
      opacity: 0;
    }
    
    .icon {
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .icon-left {
      margin-right: 0.25rem;
    }
    
    .icon-right {
      margin-left: 0.25rem;
    }
    
    /* Ripple effect */
    .ripple-container {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      overflow: hidden;
      border-radius: inherit;
    }
    
    .ripple {
      position: absolute;
      border-radius: 50%;
      transform: scale(0);
      background-color: rgba(255, 255, 255, 0.5);
      pointer-events: none;
      animation: ripple 0.6s linear;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    @keyframes ripple {
      to {
        transform: scale(4);
        opacity: 0;
      }
    }
  `],
  animations: [
    trigger('buttonState', [
      state('normal', style({
        transform: 'scale(1)'
      })),
      state('pressed', style({
        transform: 'scale(0.95)'
      })),
      state('loading', style({
        // No special style for loading, handled by CSS
      })),
      state('success', style({
        backgroundColor: '#10b981'
      })),
      state('error', style({
        backgroundColor: '#ef4444'
      })),
      transition('* => normal', animate('200ms ease-out')),
      transition('* => pressed', animate('100ms ease-in')),
      transition('* => success', animate('300ms ease-in-out')),
      transition('* => error', animate('300ms ease-in-out')),
      transition('success => normal', animate('500ms ease-out')),
      transition('error => normal', animate('500ms ease-out'))
    ])
  ]
})
export class AnimatedButtonComponent implements OnInit {
  @Input() label: string = 'Button';
  @Input() type: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'outline' | 'ghost' = 'primary';
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() iconLeft: string = '';
  @Input() iconRight: string = '';
  @Input() disabled: boolean = false;
  @Input() loading: boolean = false;
  @Input() animationDuration: number = 300; // ms
  @Input() rippleEffect: boolean = true;
  @Input() pulseOnMount: boolean = false;
  @Input() hoverScale: boolean = true;
  
  @Output() click = new EventEmitter<MouseEvent>();
  @Output() loadingChange = new EventEmitter<boolean>();
  
  state: 'normal' | 'pressed' | 'loading' | 'success' | 'error' = 'normal';

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngOnInit(): void {
    if (this.pulseOnMount) {
      setTimeout(() => {
        this.pulse();
      }, 500);
    }
    
    // Apply hover scale effect if enabled
    if (this.hoverScale) {
      this.renderer.listen(this.el.nativeElement.querySelector('button'), 'mouseenter', () => {
        if (!this.disabled && !this.loading) {
          this.renderer.setStyle(
            this.el.nativeElement.querySelector('button'), 
            'transform', 
            'translateY(-2px)'
          );
        }
      });
      
      this.renderer.listen(this.el.nativeElement.querySelector('button'), 'mouseleave', () => {
        this.renderer.setStyle(
          this.el.nativeElement.querySelector('button'), 
          'transform', 
          'translateY(0)'
        );
      });
    }
  }

  @HostListener('mousedown', ['$event'])
  onMouseDown(event: MouseEvent): void {
    if (this.disabled || this.loading) return;
    
    this.state = 'pressed';
    
    if (this.rippleEffect) {
      this.createRipple(event);
    }
  }
  
  @HostListener('mouseup', ['$event'])
  onMouseUp(event: MouseEvent): void {
    if (this.disabled || this.loading) return;
    
    this.state = 'normal';
  }
  
  @HostListener('mouseleave')
  onMouseLeave(): void {
    if (this.state === 'pressed') {
      this.state = 'normal';
    }
  }
  
  @HostListener('click', ['$event'])
  onClick(event: MouseEvent): void {
    if (this.disabled || this.loading) return;
    
    this.click.emit(event);
  }

  /**
   * Create ripple effect at the click position
   */
  private createRipple(event: MouseEvent): void {
    const button = this.el.nativeElement.querySelector('button');
    const rippleContainer = button.querySelector('.ripple-container');
    
    const rect = rippleContainer.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;
    
    const ripple = this.renderer.createElement('span');
    this.renderer.addClass(ripple, 'ripple');
    this.renderer.setStyle(ripple, 'width', `${size}px`);
    this.renderer.setStyle(ripple, 'height', `${size}px`);
    this.renderer.setStyle(ripple, 'top', `${y}px`);
    this.renderer.setStyle(ripple, 'left', `${x}px`);
    
    this.renderer.appendChild(rippleContainer, ripple);
    
    // Remove ripple after animation
    setTimeout(() => {
      if (ripple && rippleContainer.contains(ripple)) {
        this.renderer.removeChild(rippleContainer, ripple);
      }
    }, 600);
  }
  
  /**
   * Show success state animation
   * @param duration Time in ms before returning to normal state
   */
  showSuccess(duration: number = 2000): void {
    this.state = 'success';
    
    setTimeout(() => {
      this.state = 'normal';
    }, duration);
  }
  
  /**
   * Show error state animation
   * @param duration Time in ms before returning to normal state
   */
  showError(duration: number = 2000): void {
    this.state = 'error';
    
    setTimeout(() => {
      this.state = 'normal';
    }, duration);
  }
  
  /**
   * Set loading state
   */
  setLoading(isLoading: boolean): void {
    this.loading = isLoading;
    this.loadingChange.emit(isLoading);
    this.state = isLoading ? 'loading' : 'normal';
  }
  
  /**
   * Pulse animation effect
   */
  pulse(): void {
    const button = this.el.nativeElement.querySelector('button');
    
    this.renderer.setStyle(button, 'animation', `pulse ${this.animationDuration}ms ease-in-out`);
    
    // Remove animation after it completes
    setTimeout(() => {
      this.renderer.removeStyle(button, 'animation');
    }, this.animationDuration);
  }
  
  /**
   * Shake animation effect (for errors)
   */
  shake(): void {
    const button = this.el.nativeElement.querySelector('button');
    
    this.renderer.setStyle(button, 'animation', `shake ${this.animationDuration}ms ease-in-out`);
    
    // Remove animation after it completes
    setTimeout(() => {
      this.renderer.removeStyle(button, 'animation');
    }, this.animationDuration);
  }
}
