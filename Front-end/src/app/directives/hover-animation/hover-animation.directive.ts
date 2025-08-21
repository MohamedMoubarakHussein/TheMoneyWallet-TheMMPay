import { Directive, ElementRef, HostListener, Input, OnInit, Renderer2 } from '@angular/core';
import { AnimationBuilder, style, animate } from '@angular/animations';

@Directive({
  selector: '[appHoverAnimation]',
  standalone: true,
})
export class HoverAnimationDirective implements OnInit {
  @Input() animationType: 'lift' | 'scale' | 'glow' | 'pulse' | 'bounce' = 'lift';
  @Input() duration = 300; // in milliseconds
  @Input() scale = 1.05;
  @Input() translateY = -4; // in pixels
  @Input() glowColor = 'rgba(59, 130, 246, 0.5)'; // blue glow default

  private initialBoxShadow: string = 'none';
  private initialTransform: string = 'none';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
    private animationBuilder: AnimationBuilder
  ) {}

  ngOnInit() {
    // Get computed styles to preserve existing transforms and shadows
    const computedStyle = window.getComputedStyle(this.el.nativeElement);
    this.initialBoxShadow = computedStyle.boxShadow;
    this.initialTransform = computedStyle.transform;
    
    // Set initial transform if it's 'none'
    if (this.initialTransform === 'none') {
      this.initialTransform = '';
    }

    // Apply transition to all transformable properties
    this.renderer.setStyle(
      this.el.nativeElement,
      'transition',
      `transform ${this.duration}ms ease, box-shadow ${this.duration}ms ease`
    );
  }

  @HostListener('mouseenter')
  onMouseEnter() {
    switch (this.animationType) {
      case 'lift':
        this.applyLiftEffect(true);
        break;
      case 'scale':
        this.applyScaleEffect(true);
        break;
      case 'glow':
        this.applyGlowEffect(true);
        break;
      case 'pulse':
        this.applyPulseEffect();
        break;
      case 'bounce':
        this.applyBounceEffect();
        break;
    }
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    switch (this.animationType) {
      case 'lift':
        this.applyLiftEffect(false);
        break;
      case 'scale':
        this.applyScaleEffect(false);
        break;
      case 'glow':
        this.applyGlowEffect(false);
        break;
      // Pulse and bounce are triggered once, no need to reset
    }
  }

  private applyLiftEffect(isHovering: boolean) {
    const transform = isHovering 
      ? `${this.initialTransform} translateY(${this.translateY}px)` 
      : this.initialTransform;
    
    const boxShadow = isHovering
      ? `0 10px 15px ${this.glowColor}`
      : this.initialBoxShadow;
    
    this.renderer.setStyle(this.el.nativeElement, 'transform', transform);
    this.renderer.setStyle(this.el.nativeElement, 'box-shadow', boxShadow);
  }

  private applyScaleEffect(isHovering: boolean) {
    const transform = isHovering 
      ? `${this.initialTransform} scale(${this.scale})` 
      : this.initialTransform;
    
    this.renderer.setStyle(this.el.nativeElement, 'transform', transform);
  }

  private applyGlowEffect(isHovering: boolean) {
    const boxShadow = isHovering
      ? `0 0 15px ${this.glowColor}`
      : this.initialBoxShadow;
    
    this.renderer.setStyle(this.el.nativeElement, 'box-shadow', boxShadow);
  }

  private applyPulseEffect() {
    // Create a pulse animation with AnimationBuilder
    const pulseAnimation = this.animationBuilder.build([
      style({ transform: `${this.initialTransform}` }),
      animate(`${this.duration * 0.5}ms ease-out`, 
        style({ transform: `${this.initialTransform} scale(${this.scale})` })),
      animate(`${this.duration * 0.5}ms ease-in`, 
        style({ transform: `${this.initialTransform}` })),
    ]);

    const player = pulseAnimation.create(this.el.nativeElement);
    player.play();
  }

  private applyBounceEffect() {
    // Create a bounce animation with AnimationBuilder
    const bounceAnimation = this.animationBuilder.build([
      style({ transform: `${this.initialTransform}` }),
      animate(`${this.duration * 0.2}ms ease-out`, 
        style({ transform: `${this.initialTransform} translateY(-10px)` })),
      animate(`${this.duration * 0.1}ms ease-in`, 
        style({ transform: `${this.initialTransform} translateY(0)` })),
      animate(`${this.duration * 0.1}ms ease-out`, 
        style({ transform: `${this.initialTransform} translateY(-5px)` })),
      animate(`${this.duration * 0.1}ms ease-in`, 
        style({ transform: `${this.initialTransform}` })),
    ]);

    const player = bounceAnimation.create(this.el.nativeElement);
    player.play();
  }
}
