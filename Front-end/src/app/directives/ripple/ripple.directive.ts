import { Directive, ElementRef, HostListener, Input, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appRipple]',
  standalone: true
})
export class RippleDirective {
  @Input() rippleColor = 'rgba(255, 255, 255, 0.35)';
  @Input() rippleDuration = 700; // ms

  constructor(private el: ElementRef, private renderer: Renderer2) {
    // Add relative position if needed
    if (window.getComputedStyle(this.el.nativeElement).position === 'static') {
      this.renderer.setStyle(this.el.nativeElement, 'position', 'relative');
    }
    
    // Add overflow hidden to contain ripple
    this.renderer.setStyle(this.el.nativeElement, 'overflow', 'hidden');
  }

  @HostListener('mousedown', ['$event'])
  onMouseDown(event: MouseEvent): void {
    this.createRippleEffect(event);
  }

  @HostListener('touchstart', ['$event'])
  onTouchStart(event: TouchEvent): void {
    const touch = event.touches[0];
    const touchEvent = {
      clientX: touch.clientX,
      clientY: touch.clientY
    } as MouseEvent;
    this.createRippleEffect(touchEvent);
  }

  private createRippleEffect(event: MouseEvent): void {
    // Get element dimensions and position
    const rect = this.el.nativeElement.getBoundingClientRect();
    
    // Calculate ripple position
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    
    // Calculate ripple size (covering the entire element)
    const width = rect.width;
    const height = rect.height;
    const rippleSize = Math.max(width, height) * 2;
    
    // Create ripple element
    const ripple = this.renderer.createElement('span');
    
    // Set ripple styles
    this.renderer.setAttribute(ripple, 'class', 'ripple-effect');
    this.renderer.setStyle(ripple, 'position', 'absolute');
    this.renderer.setStyle(ripple, 'top', `${y - rippleSize / 2}px`);
    this.renderer.setStyle(ripple, 'left', `${x - rippleSize / 2}px`);
    this.renderer.setStyle(ripple, 'width', `${rippleSize}px`);
    this.renderer.setStyle(ripple, 'height', `${rippleSize}px`);
    this.renderer.setStyle(ripple, 'background-color', this.rippleColor);
    this.renderer.setStyle(ripple, 'border-radius', '50%');
    this.renderer.setStyle(ripple, 'transform', 'scale(0)');
    this.renderer.setStyle(ripple, 'opacity', '1');
    this.renderer.setStyle(ripple, 'pointer-events', 'none');
    this.renderer.setStyle(ripple, 'animation', `ripple ${this.rippleDuration}ms ease-out forwards`);
    
    // Add ripple to element
    this.renderer.appendChild(this.el.nativeElement, ripple);
    
    // Remove ripple after animation
    setTimeout(() => {
      if (this.el.nativeElement.contains(ripple)) {
        this.renderer.removeChild(this.el.nativeElement, ripple);
      }
    }, this.rippleDuration);
  }
}
