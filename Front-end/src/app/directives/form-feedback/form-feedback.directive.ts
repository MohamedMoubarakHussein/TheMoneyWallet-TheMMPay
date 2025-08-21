import { Directive, ElementRef, Input, OnInit, OnDestroy, Renderer2, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AnimationBuilder, style, animate } from '@angular/animations';

/**
 * FormFeedback directive provides real-time animations and visual feedback for form controls
 * It enhances the user experience by providing subtle animations on input, focus, validation states
 */
@Directive({
  selector: '[appFormFeedback]',
  standalone: true
})
export class FormFeedbackDirective implements OnInit, OnDestroy {
  @Input() successIcon: string = '✓';
  @Input() errorIcon: string = '✗';
  @Input() loadingIcon: string = '⟳';
  @Input() validateOnBlur: boolean = false;
  @Input() shake: boolean = true;
  @Input() pulse: boolean = true;
  @Input() highlightValid: boolean = true;
  @Input() highlightInvalid: boolean = true;
  @Input() messagePosition: 'bottom' | 'right' | 'tooltip' = 'bottom';
  
  private iconElement: HTMLElement | null = null;
  private messageElement: HTMLElement | null = null;
  private statusSubscription: Subscription | null = null;
  private valueSubscription: Subscription | null = null;
  private originalPaddingRight: string = '';
  private originalBoxShadow: string = '';
  private isProcessing: boolean = false;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
    private control: NgControl,
    private animationBuilder: AnimationBuilder
  ) {}

  ngOnInit(): void {
    // Save original styles
    const computedStyle = getComputedStyle(this.el.nativeElement);
    this.originalPaddingRight = computedStyle.paddingRight;
    this.originalBoxShadow = computedStyle.boxShadow;
    
    // Add position relative for absolute positioning of children
    this.renderer.setStyle(this.el.nativeElement, 'position', 'relative');
    
    // Subscribe to control status changes
    if (this.control && this.control.statusChanges) {
      this.statusSubscription = this.control.statusChanges.subscribe(status => {
        if (!this.validateOnBlur || (this.validateOnBlur && this.control.touched)) {
          this.updateValidationState(status);
        }
      });
    }
    
    // Subscribe to value changes
    if (this.control && this.control.valueChanges) {
      this.valueSubscription = this.control.valueChanges.subscribe(() => {
        this.onValueChange();
      });
    }
    
    // Add right padding to make room for icons
    this.renderer.setStyle(this.el.nativeElement, 'paddingRight', 'calc(' + this.originalPaddingRight + ' + 30px)');
    
    // Create and append the icon element
    this.createIconElement();
  }
  
  ngOnDestroy(): void {
    // Clean up subscriptions
    if (this.statusSubscription) {
      this.statusSubscription.unsubscribe();
    }
    if (this.valueSubscription) {
      this.valueSubscription.unsubscribe();
    }
    
    // Remove added elements
    this.removeIconElement();
    this.removeMessageElement();
  }
  
  @HostListener('focus')
  onFocus(): void {
    // Apply focused animation
    const focusAnimation = this.animationBuilder.build([
      style({ boxShadow: this.originalBoxShadow }),
      animate('300ms ease-out', style({ boxShadow: '0 0 0 3px rgba(59, 130, 246, 0.3)' }))
    ]);
    
    const player = focusAnimation.create(this.el.nativeElement);
    player.play();
  }
  
  @HostListener('blur')
  onBlur(): void {
    // If validateOnBlur is true, update validation state
    if (this.validateOnBlur && this.control) {
      this.updateValidationState(this.control.status);
    }
    
    // Restore original box shadow
    this.renderer.setStyle(this.el.nativeElement, 'boxShadow', this.originalBoxShadow);
  }

  /**
   * When value changes, show processing state
   */
  private onValueChange(): void {
    if (!this.isProcessing) {
      this.isProcessing = true;
      
      // Show loading indicator
      if (this.iconElement) {
        this.renderer.setProperty(this.iconElement, 'textContent', this.loadingIcon);
        this.renderer.addClass(this.iconElement, 'processing');
        
        // Hide any existing messages
        this.removeMessageElement();
      }
      
      // Reset processing state after a short delay
      setTimeout(() => {
        this.isProcessing = false;
        
        // Update validation state if control has been touched
        if (this.control && this.control.touched) {
          this.updateValidationState(this.control.status);
        } else {
          // Hide icon if not validated yet
          this.hideIcon();
        }
      }, 300);
    }
  }
  
  /**
   * Update the validation state and apply appropriate animations
   */
  private updateValidationState(status: string): void {
    if (!this.iconElement) return;
    
    // Reset classes
    this.renderer.removeClass(this.iconElement, 'valid');
    this.renderer.removeClass(this.iconElement, 'invalid');
    this.renderer.removeClass(this.iconElement, 'processing');
    
    // Remove highlights from input
    this.renderer.removeClass(this.el.nativeElement, 'valid-highlight');
    this.renderer.removeClass(this.el.nativeElement, 'invalid-highlight');
    
    // Update based on status
    if (status === 'VALID') {
      // Show valid state
      this.showValidState();
    } else if (status === 'INVALID') {
      // Show invalid state
      this.showInvalidState();
    } else {
      // Hide for other states (PENDING, DISABLED)
      this.hideIcon();
    }
  }
  
  /**
   * Show valid state with success icon and animation
   */
  private showValidState(): void {
    if (!this.iconElement) return;
    
    // Set success icon and class
    this.renderer.setProperty(this.iconElement, 'textContent', this.successIcon);
    this.renderer.addClass(this.iconElement, 'valid');
    this.renderer.setStyle(this.iconElement, 'display', 'flex');
    
    // Add highlight to input if enabled
    if (this.highlightValid) {
      this.renderer.addClass(this.el.nativeElement, 'valid-highlight');
      
      // Reset box shadow
      this.renderer.setStyle(this.el.nativeElement, 'boxShadow', '0 0 0 2px rgba(16, 185, 129, 0.3)');
    }
    
    // Add success pulse animation
    if (this.pulse) {
      const pulseAnimation = this.animationBuilder.build([
        style({ transform: 'scale(1)' }),
        animate('300ms ease-out', style({ transform: 'scale(1.2)' })),
        animate('200ms ease-in', style({ transform: 'scale(1)' }))
      ]);
      
      const player = pulseAnimation.create(this.iconElement);
      player.play();
    }
    
    // Remove any error messages
    this.removeMessageElement();
  }
  
  /**
   * Show invalid state with error icon, message and animation
   */
  private showInvalidState(): void {
    if (!this.iconElement || !this.control) return;
    
    // Set error icon and class
    this.renderer.setProperty(this.iconElement, 'textContent', this.errorIcon);
    this.renderer.addClass(this.iconElement, 'invalid');
    this.renderer.setStyle(this.iconElement, 'display', 'flex');
    
    // Add highlight to input if enabled
    if (this.highlightInvalid) {
      this.renderer.addClass(this.el.nativeElement, 'invalid-highlight');
      
      // Set error box shadow
      this.renderer.setStyle(this.el.nativeElement, 'boxShadow', '0 0 0 2px rgba(239, 68, 68, 0.3)');
    }
    
    // Add shake animation if enabled
    if (this.shake) {
      const shakeAnimation = this.animationBuilder.build([
        animate('300ms ease-in-out', style({ transform: 'translateX(0)' })),
        animate('100ms ease-in-out', style({ transform: 'translateX(-5px)' })),
        animate('100ms ease-in-out', style({ transform: 'translateX(5px)' })),
        animate('100ms ease-in-out', style({ transform: 'translateX(-3px)' })),
        animate('100ms ease-in-out', style({ transform: 'translateX(0)' }))
      ]);
      
      const player = shakeAnimation.create(this.el.nativeElement);
      player.play();
    }
    
    // Show error message if there are errors
    if (this.control.errors) {
      this.showErrorMessage(this.getErrorMessage(this.control.errors));
    }
  }
  
  /**
   * Hide the validation icon
   */
  private hideIcon(): void {
    if (this.iconElement) {
      this.renderer.setStyle(this.iconElement, 'display', 'none');
    }
    this.removeMessageElement();
    
    // Reset input styles
    this.renderer.removeClass(this.el.nativeElement, 'valid-highlight');
    this.renderer.removeClass(this.el.nativeElement, 'invalid-highlight');
    this.renderer.setStyle(this.el.nativeElement, 'boxShadow', this.originalBoxShadow);
  }
  
  /**
   * Create and append the icon element
   */
  private createIconElement(): void {
    this.iconElement = this.renderer.createElement('div');
    this.renderer.addClass(this.iconElement, 'form-feedback-icon');
    this.renderer.setStyle(this.iconElement, 'position', 'absolute');
    this.renderer.setStyle(this.iconElement, 'right', '10px');
    this.renderer.setStyle(this.iconElement, 'top', '50%');
    this.renderer.setStyle(this.iconElement, 'transform', 'translateY(-50%)');
    this.renderer.setStyle(this.iconElement, 'width', '20px');
    this.renderer.setStyle(this.iconElement, 'height', '20px');
    this.renderer.setStyle(this.iconElement, 'display', 'none');
    this.renderer.setStyle(this.iconElement, 'align-items', 'center');
    this.renderer.setStyle(this.iconElement, 'justify-content', 'center');
    this.renderer.setStyle(this.iconElement, 'border-radius', '50%');
    this.renderer.setStyle(this.iconElement, 'font-size', '12px');
    
    this.renderer.appendChild(this.el.nativeElement.parentNode, this.iconElement);
    
    // Initially hide the icon
    this.hideIcon();
  }
  
  /**
   * Remove the icon element
   */
  private removeIconElement(): void {
    if (this.iconElement && this.iconElement.parentNode) {
      this.renderer.removeChild(this.iconElement.parentNode, this.iconElement);
    }
    this.iconElement = null;
  }
  
  /**
   * Show error message
   */
  private showErrorMessage(message: string): void {
    // Remove any existing message first
    this.removeMessageElement();
    
    // Create new message element
    this.messageElement = this.renderer.createElement('div');
    this.renderer.addClass(this.messageElement, 'form-feedback-message');
    this.renderer.setStyle(this.messageElement, 'font-size', '12px');
    this.renderer.setStyle(this.messageElement, 'color', '#ef4444');
    this.renderer.setProperty(this.messageElement, 'textContent', message);
    
    // Position the message based on messagePosition
    switch (this.messagePosition) {
      case 'bottom':
        this.renderer.setStyle(this.messageElement, 'position', 'absolute');
        this.renderer.setStyle(this.messageElement, 'left', '0');
        this.renderer.setStyle(this.messageElement, 'top', '100%');
        this.renderer.setStyle(this.messageElement, 'margin-top', '4px');
        break;
        
      case 'right':
        this.renderer.setStyle(this.messageElement, 'position', 'absolute');
        this.renderer.setStyle(this.messageElement, 'left', '100%');
        this.renderer.setStyle(this.messageElement, 'top', '50%');
        this.renderer.setStyle(this.messageElement, 'transform', 'translateY(-50%)');
        this.renderer.setStyle(this.messageElement, 'margin-left', '10px');
        break;
        
      case 'tooltip':
        this.renderer.setStyle(this.messageElement, 'position', 'absolute');
        this.renderer.setStyle(this.messageElement, 'top', '-30px');
        this.renderer.setStyle(this.messageElement, 'right', '0');
        this.renderer.setStyle(this.messageElement, 'background-color', 'rgba(239, 68, 68, 0.9)');
        this.renderer.setStyle(this.messageElement, 'color', 'white');
        this.renderer.setStyle(this.messageElement, 'padding', '4px 8px');
        this.renderer.setStyle(this.messageElement, 'border-radius', '4px');
        this.renderer.setStyle(this.messageElement, 'white-space', 'nowrap');
        
        // Add arrow
        const arrow = this.renderer.createElement('div');
        this.renderer.setStyle(arrow, 'position', 'absolute');
        this.renderer.setStyle(arrow, 'bottom', '-4px');
        this.renderer.setStyle(arrow, 'right', '10px');
        this.renderer.setStyle(arrow, 'width', '8px');
        this.renderer.setStyle(arrow, 'height', '8px');
        this.renderer.setStyle(arrow, 'background-color', 'rgba(239, 68, 68, 0.9)');
        this.renderer.setStyle(arrow, 'transform', 'rotate(45deg)');
        this.renderer.appendChild(this.messageElement, arrow);
        break;
    }
    
    // Append message to parent
    this.renderer.appendChild(this.el.nativeElement.parentNode, this.messageElement);
    
    // Animate in
    const fadeInAnimation = this.animationBuilder.build([
      style({ opacity: 0, transform: 'translateY(5px)' }),
      animate('200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
    ]);
    
    const player = fadeInAnimation.create(this.messageElement);
    player.play();
  }
  
  /**
   * Remove the message element
   */
  private removeMessageElement(): void {
    if (this.messageElement && this.messageElement.parentNode) {
      this.renderer.removeChild(this.messageElement.parentNode, this.messageElement);
    }
    this.messageElement = null;
  }
  
  /**
   * Get user-friendly error message from validation errors
   */
  private getErrorMessage(errors: any): string {
    if (errors.required) {
      return 'This field is required';
    }
    
    if (errors.minlength) {
      return `Must be at least ${errors.minlength.requiredLength} characters`;
    }
    
    if (errors.maxlength) {
      return `Cannot exceed ${errors.maxlength.requiredLength} characters`;
    }
    
    if (errors.email) {
      return 'Please enter a valid email address';
    }
    
    if (errors.pattern) {
      return 'Invalid format';
    }
    
    if (errors.min) {
      return `Value must be greater than or equal to ${errors.min.min}`;
    }
    
    if (errors.max) {
      return `Value must be less than or equal to ${errors.max.max}`;
    }
    
    // Return a generic message if error type is not recognized
    return 'Invalid input';
  }
}
