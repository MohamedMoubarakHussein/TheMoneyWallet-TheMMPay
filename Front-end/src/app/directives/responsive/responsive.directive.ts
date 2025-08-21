import { Directive, ElementRef, Input, OnInit, OnDestroy, HostListener, Renderer2 } from '@angular/core';
import { ResponsiveService } from '../../services/responsive/responsive.service';
import { MobileService } from '../../services/mobile/mobile.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

export interface ResponsiveConfig {
  mobileClass?: string;
  tabletClass?: string;
  desktopClass?: string;
  mobileBehavior?: 'stack' | 'hide' | 'transform' | 'custom';
  tabletBehavior?: 'stack' | 'hide' | 'transform' | 'custom';
  customMobileStyles?: { [key: string]: string };
  customTabletStyles?: { [key: string]: string };
  enableTouchGestures?: boolean;
  enableSwipeNavigation?: boolean;
  enablePullToRefresh?: boolean;
}

@Directive({
  selector: '[appResponsive]',
  standalone: true
})
export class ResponsiveDirective implements OnInit, OnDestroy {
  @Input() responsiveConfig: ResponsiveConfig = {};
  @Input() mobileBreakpoint: number = 720;
  @Input() tabletBreakpoint: number = 1024;

  private destroy$ = new Subject<void>();
  private currentBreakpoint: 'xs' | 'sm' | 'md' | 'lg' | 'xl' = 'md';
  private originalStyles: { [key: string]: string } = {};
  private isMobile = false;
  private isTablet = false;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
    private responsiveService: ResponsiveService,
    private mobileService: MobileService
  ) {}

  ngOnInit(): void {
    this.setupResponsiveService();
    this.setupMobileService();
    this.applyResponsiveBehavior();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupResponsiveService(): void {
    this.responsiveService.screenSize$
      .pipe(takeUntil(this.destroy$))
      .subscribe(screenSize => {
        this.currentBreakpoint = screenSize.breakpoint;
        this.isMobile = screenSize.isMobile;
        this.isTablet = screenSize.isTablet;
        this.applyResponsiveBehavior();
      });
  }

  private setupMobileService(): void {
    this.mobileService.isMobile$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isMobile => {
        this.isMobile = isMobile;
        this.applyResponsiveBehavior();
      });
  }

  private applyResponsiveBehavior(): void {
    this.clearResponsiveClasses();
    this.clearResponsiveStyles();

    if (this.isMobile) {
      this.applyMobileBehavior();
    } else if (this.isTablet) {
      this.applyTabletBehavior();
    } else {
      this.applyDesktopBehavior();
    }
  }

  private applyMobileBehavior(): void {
    const behavior = this.responsiveConfig.mobileBehavior || 'stack';

    switch (behavior) {
      case 'stack':
        this.applyStackBehavior();
        break;
      case 'hide':
        this.applyHideBehavior();
        break;
      case 'transform':
        this.applyTransformBehavior();
        break;
      case 'custom':
        this.applyCustomMobileStyles();
        break;
    }

    if (this.responsiveConfig.mobileClass) {
      this.renderer.addClass(this.el.nativeElement, this.responsiveConfig.mobileClass);
    }

    // Apply mobile-specific attributes
    this.renderer.setAttribute(this.el.nativeElement, 'data-responsive', 'mobile');
    this.renderer.setAttribute(this.el.nativeElement, 'data-breakpoint', this.currentBreakpoint);
  }

  private applyTabletBehavior(): void {
    const behavior = this.responsiveConfig.tabletBehavior || 'stack';

    switch (behavior) {
      case 'stack':
        this.applyStackBehavior();
        break;
      case 'hide':
        this.applyHideBehavior();
        break;
      case 'transform':
        this.applyTransformBehavior();
        break;
      case 'custom':
        this.applyCustomTabletStyles();
        break;
    }

    if (this.responsiveConfig.tabletClass) {
      this.renderer.addClass(this.el.nativeElement, this.responsiveConfig.tabletClass);
    }

    // Apply tablet-specific attributes
    this.renderer.setAttribute(this.el.nativeElement, 'data-responsive', 'tablet');
    this.renderer.setAttribute(this.el.nativeElement, 'data-breakpoint', this.currentBreakpoint);
  }

  private applyDesktopBehavior(): void {
    // Reset to desktop behavior
    this.renderer.setAttribute(this.el.nativeElement, 'data-responsive', 'desktop');
    this.renderer.setAttribute(this.el.nativeElement, 'data-breakpoint', this.currentBreakpoint);
  }

  private applyStackBehavior(): void {
    this.renderer.addClass(this.el.nativeElement, 'responsive-stack');
    this.renderer.setStyle(this.el.nativeElement, 'flex-direction', 'column');
    this.renderer.setStyle(this.el.nativeElement, 'width', '100%');
  }

  private applyHideBehavior(): void {
    this.renderer.addClass(this.el.nativeElement, 'responsive-hide');
    this.renderer.setStyle(this.el.nativeElement, 'display', 'none');
  }

  private applyTransformBehavior(): void {
    this.renderer.addClass(this.el.nativeElement, 'responsive-transform');
    this.renderer.setStyle(this.el.nativeElement, 'transform', 'scale(0.9)');
    this.renderer.setStyle(this.el.nativeElement, 'transform-origin', 'top left');
  }

  private applyCustomMobileStyles(): void {
    if (this.responsiveConfig.customMobileStyles) {
      Object.entries(this.responsiveConfig.customMobileStyles).forEach(([property, value]) => {
        this.renderer.setStyle(this.el.nativeElement, property, value);
      });
    }
  }

  private applyCustomTabletStyles(): void {
    if (this.responsiveConfig.customTabletStyles) {
      Object.entries(this.responsiveConfig.customTabletStyles).forEach(([property, value]) => {
        this.renderer.setStyle(this.el.nativeElement, property, value);
      });
    }
  }

  private clearResponsiveClasses(): void {
    this.renderer.removeClass(this.el.nativeElement, 'responsive-stack');
    this.renderer.removeClass(this.el.nativeElement, 'responsive-hide');
    this.renderer.removeClass(this.el.nativeElement, 'responsive-transform');
    
    if (this.responsiveConfig.mobileClass) {
      this.renderer.removeClass(this.el.nativeElement, this.responsiveConfig.mobileClass);
    }
    
    if (this.responsiveConfig.tabletClass) {
      this.renderer.removeClass(this.el.nativeElement, this.responsiveConfig.tabletClass);
    }
  }

  private clearResponsiveStyles(): void {
    this.renderer.removeStyle(this.el.nativeElement, 'flex-direction');
    this.renderer.removeStyle(this.el.nativeElement, 'width');
    this.renderer.removeStyle(this.el.nativeElement, 'display');
    this.renderer.removeStyle(this.el.nativeElement, 'transform');
    this.renderer.removeStyle(this.el.nativeElement, 'transform-origin');
  }

  // Touch event handlers
  @HostListener('touchstart', ['$event'])
  onTouchStart(event: TouchEvent): void {
    if (this.responsiveConfig.enableTouchGestures && this.isMobile) {
      this.handleTouchStart(event);
    }
  }

  @HostListener('touchmove', ['$event'])
  onTouchMove(event: TouchEvent): void {
    if (this.responsiveConfig.enableTouchGestures && this.isMobile) {
      this.handleTouchMove(event);
    }
  }

  @HostListener('touchend', ['$event'])
  onTouchEnd(event: TouchEvent): void {
    if (this.responsiveConfig.enableTouchGestures && this.isMobile) {
      this.handleTouchEnd(event);
    }
  }

  private handleTouchStart(event: TouchEvent): void {
    // Store touch start information
    const touch = event.touches[0];
    this.el.nativeElement.touchStartX = touch.clientX;
    this.el.nativeElement.touchStartY = touch.clientY;
    this.el.nativeElement.touchStartTime = Date.now();
  }

  private handleTouchMove(event: TouchEvent): void {
    // Handle touch move for gestures
    if (this.responsiveConfig.enableSwipeNavigation) {
      const touch = event.touches[0];
      const deltaX = touch.clientX - this.el.nativeElement.touchStartX;
      const deltaY = touch.clientY - this.el.nativeElement.touchStartY;

      // Prevent default for horizontal swipes
      if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 10) {
        event.preventDefault();
      }
    }
  }

  private handleTouchEnd(event: TouchEvent): void {
    // Handle touch end for gesture recognition
    if (this.responsiveConfig.enableSwipeNavigation) {
      const touch = event.changedTouches[0];
      const deltaX = touch.clientX - this.el.nativeElement.touchStartX;
      const deltaY = touch.clientY - this.el.nativeElement.touchStartY;
      const duration = Date.now() - this.el.nativeElement.touchStartTime;

      // Detect swipe gestures
      if (Math.abs(deltaX) > 50 && duration < 300) {
        if (deltaX > 0) {
          this.onSwipeRight();
        } else {
          this.onSwipeLeft();
        }
      }

      if (Math.abs(deltaY) > 50 && duration < 300) {
        if (deltaY > 0) {
          this.onSwipeDown();
        } else {
          this.onSwipeUp();
        }
      }
    }
  }

  // Swipe event handlers
  private onSwipeLeft(): void {
    this.emitSwipeEvent('left');
  }

  private onSwipeRight(): void {
    this.emitSwipeEvent('right');
  }

  private onSwipeUp(): void {
    this.emitSwipeEvent('up');
  }

  private onSwipeDown(): void {
    this.emitSwipeEvent('down');
  }

  private emitSwipeEvent(direction: string): void {
    // Create and dispatch custom swipe event
    const swipeEvent = new CustomEvent('swipe', {
      detail: { direction, element: this.el.nativeElement },
      bubbles: true
    });
    this.el.nativeElement.dispatchEvent(swipeEvent);
  }

  // Utility methods
  isMobileView(): boolean {
    return this.isMobile;
  }

  isTabletView(): boolean {
    return this.isTablet;
  }

  isDesktopView(): boolean {
    return !this.isMobile && !this.isTablet;
  }

  getCurrentBreakpoint(): string {
    return this.currentBreakpoint;
  }

  // Method to manually trigger responsive behavior
  refreshResponsiveBehavior(): void {
    this.applyResponsiveBehavior();
  }

  // Method to add custom responsive class
  addResponsiveClass(className: string): void {
    this.renderer.addClass(this.el.nativeElement, className);
  }

  // Method to remove custom responsive class
  removeResponsiveClass(className: string): void {
    this.renderer.removeClass(this.el.nativeElement, className);
  }

  // Method to set custom responsive style
  setResponsiveStyle(property: string, value: string): void {
    this.renderer.setStyle(this.el.nativeElement, property, value);
  }

  // Method to remove custom responsive style
  removeResponsiveStyle(property: string): void {
    this.renderer.removeStyle(this.el.nativeElement, property);
  }

  // Method to get element dimensions
  getElementDimensions(): { width: number; height: number } {
    const rect = this.el.nativeElement.getBoundingClientRect();
    return {
      width: rect.width,
      height: rect.height
    };
  }

  // Method to check if element is in viewport
  isInViewport(): boolean {
    const rect = this.el.nativeElement.getBoundingClientRect();
    return (
      rect.top >= 0 &&
      rect.left >= 0 &&
      rect.bottom <= window.innerHeight &&
      rect.right <= window.innerWidth
    );
  }

  // Method to scroll element into view
  scrollIntoView(behavior: ScrollBehavior = 'smooth'): void {
    this.el.nativeElement.scrollIntoView({ behavior });
  }

  // Method to focus element
  focus(): void {
    this.el.nativeElement.focus();
  }

  // Method to blur element
  blur(): void {
    this.el.nativeElement.blur();
  }

  // Method to check if element is focused
  isFocused(): boolean {
    return this.el.nativeElement === document.activeElement;
  }

  // Method to get computed styles
  getComputedStyle(property: string): string {
    return getComputedStyle(this.el.nativeElement).getPropertyValue(property);
  }

  // Method to set CSS custom property
  setCSSVariable(property: string, value: string): void {
    this.renderer.setStyle(this.el.nativeElement, `--${property}`, value);
  }

  // Method to get CSS custom property
  getCSSVariable(property: string): string {
    return this.getComputedStyle(`--${property}`);
  }
} 