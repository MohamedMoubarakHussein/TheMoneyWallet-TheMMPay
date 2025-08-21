import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject, Observable, fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';

export interface TouchGesture {
  type: 'swipe' | 'pinch' | 'rotate' | 'longPress';
  direction?: 'left' | 'right' | 'up' | 'down';
  distance?: number;
  duration?: number;
  startX?: number;
  startY?: number;
  endX?: number;
  endY?: number;
}

export interface MobileConfig {
  enableTouchGestures: boolean;
  enableHapticFeedback: boolean;
  enablePullToRefresh: boolean;
  enableSwipeNavigation: boolean;
  touchSensitivity: 'low' | 'medium' | 'high';
  hapticIntensity: 'light' | 'medium' | 'strong';
}

@Injectable({
  providedIn: 'root'
})
export class MobileService {
  private readonly DEFAULT_CONFIG: MobileConfig = {
    enableTouchGestures: true,
    enableHapticFeedback: true,
    enablePullToRefresh: true,
    enableSwipeNavigation: true,
    touchSensitivity: 'medium',
    hapticIntensity: 'medium'
  };

  private config: MobileConfig;
  private touchStartSubject = new BehaviorSubject<TouchGesture | null>(null);
  private touchEndSubject = new BehaviorSubject<TouchGesture | null>(null);
  private gestureSubject = new BehaviorSubject<TouchGesture | null>(null);
  private isMobileSubject = new BehaviorSubject<boolean>(false);
  private orientationSubject = new BehaviorSubject<'portrait' | 'landscape'>('portrait');

  public touchStart$ = this.touchStartSubject.asObservable();
  public touchEnd$ = this.touchEndSubject.asObservable();
  public gesture$ = this.gestureSubject.asObservable();
  public isMobile$ = this.isMobileSubject.asObservable();
  public orientation$ = this.orientationSubject.asObservable();

  private touchStartTime = 0;
  private touchStartX = 0;
  private touchStartY = 0;
  private touchEndX = 0;
  private touchEndY = 0;
  private isLongPress = false;
  private longPressTimer: any;
  private readonly LONG_PRESS_DURATION = 500;
  private readonly SWIPE_THRESHOLD = 50;
  private readonly PINCH_THRESHOLD = 20;

  constructor(private ngZone: NgZone) {
    this.config = { ...this.DEFAULT_CONFIG };
    this.initializeMobileService();
  }

  private initializeMobileService(): void {
    this.detectMobile();
    this.setupTouchEvents();
    this.setupOrientationChange();
    this.setupResizeEvents();
  }

  private detectMobile(): void {
    const userAgent = navigator.userAgent.toLowerCase();
    const isMobile = /mobile|android|iphone|ipad|phone|blackberry|opera mini|windows phone/i.test(userAgent);
    this.isMobileSubject.next(isMobile);
  }

  private setupTouchEvents(): void {
    if (!this.config.enableTouchGestures) return;

    // Touch start event
    fromEvent<TouchEvent>(document, 'touchstart', { passive: false })
      .subscribe((event: TouchEvent) => {
        this.onTouchStart(event);
      });

    // Touch move event
    fromEvent<TouchEvent>(document, 'touchmove', { passive: false })
      .subscribe((event: TouchEvent) => {
        this.onTouchMove(event);
      });

    // Touch end event
    fromEvent<TouchEvent>(document, 'touchend', { passive: false })
      .subscribe((event: TouchEvent) => {
        this.onTouchEnd(event);
      });
  }

  private setupOrientationChange(): void {
    fromEvent(window, 'orientationchange')
      .pipe(debounceTime(100))
      .subscribe(() => {
        this.updateOrientation();
      });

    // Also listen for resize events for orientation changes
    fromEvent(window, 'resize')
      .pipe(
        debounceTime(100),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.updateOrientation();
      });

    // Initial orientation check
    this.updateOrientation();
  }

  private setupResizeEvents(): void {
    fromEvent(window, 'resize')
      .pipe(
        debounceTime(100),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.detectMobile();
      });
  }

  private onTouchStart(event: TouchEvent): void {
    if (event.touches.length === 1) {
      const touch = event.touches[0];
      this.touchStartTime = Date.now();
      this.touchStartX = touch.clientX;
      this.touchStartY = touch.clientY;
      this.isLongPress = false;

      // Start long press timer
      this.longPressTimer = setTimeout(() => {
        this.isLongPress = true;
        this.triggerLongPress();
      }, this.LONG_PRESS_DURATION);

      const gesture: TouchGesture = {
        type: 'swipe',
        startX: this.touchStartX,
        startY: this.touchStartY
      };

      this.touchStartSubject.next(gesture);
    }
  }

  private onTouchMove(event: TouchEvent): void {
    if (event.touches.length === 1) {
      const touch = event.touches[0];
      this.touchEndX = touch.clientX;
      this.touchEndY = touch.clientY;

      // Clear long press timer if user moves finger
      if (this.longPressTimer) {
        clearTimeout(this.longPressTimer);
        this.longPressTimer = null;
      }
    } else if (event.touches.length === 2) {
      // Handle pinch gestures
      this.handlePinchGesture(event);
    }
  }

  private onTouchEnd(event: TouchEvent): void {
    if (this.longPressTimer) {
      clearTimeout(this.longPressTimer);
      this.longPressTimer = null;
    }

    if (!this.isLongPress && event.changedTouches.length === 1) {
      const touch = event.changedTouches[0];
      this.touchEndX = touch.clientX;
      this.touchEndY = touch.clientY;

      const duration = Date.now() - this.touchStartTime;
      const distance = Math.sqrt(
        Math.pow(this.touchEndX - this.touchStartX, 2) +
        Math.pow(this.touchEndY - this.touchStartY, 2)
      );

      if (distance > this.SWIPE_THRESHOLD) {
        this.handleSwipeGesture(duration, distance);
      }
    }

    const gesture: TouchGesture = {
      type: 'swipe',
      endX: this.touchEndX,
      endY: this.touchEndY
    };

    this.touchEndSubject.next(gesture);
  }

  private handleSwipeGesture(duration: number, distance: number): void {
    const deltaX = this.touchEndX - this.touchStartX;
    const deltaY = this.touchEndY - this.touchStartY;
    const absDeltaX = Math.abs(deltaX);
    const absDeltaY = Math.abs(deltaY);

    let direction: 'left' | 'right' | 'up' | 'down';

    if (absDeltaX > absDeltaY) {
      direction = deltaX > 0 ? 'right' : 'left';
    } else {
      direction = deltaY > 0 ? 'down' : 'up';
    }

    const gesture: TouchGesture = {
      type: 'swipe',
      direction,
      distance,
      duration,
      startX: this.touchStartX,
      startY: this.touchStartY,
      endX: this.touchEndX,
      endY: this.touchEndY
    };

    this.gestureSubject.next(gesture);
    this.handleSwipeAction(gesture);
  }

  private handlePinchGesture(event: TouchEvent): void {
    if (event.touches.length !== 2) return;

    const touch1 = event.touches[0];
    const touch2 = event.touches[1];

    const currentDistance = Math.sqrt(
      Math.pow(touch2.clientX - touch1.clientX, 2) +
      Math.pow(touch2.clientY - touch1.clientY, 2)
    );

    // This is a simplified pinch detection
    // In a real implementation, you'd track the previous distance
    const gesture: TouchGesture = {
      type: 'pinch',
      distance: currentDistance
    };

    this.gestureSubject.next(gesture);
  }

  private triggerLongPress(): void {
    const gesture: TouchGesture = {
      type: 'longPress',
      startX: this.touchStartX,
      startY: this.touchStartY
    };

    this.gestureSubject.next(gesture);
    this.handleLongPressAction(gesture);
  }

  private handleSwipeAction(gesture: TouchGesture): void {
    if (!this.config.enableSwipeNavigation) return;

    switch (gesture.direction) {
      case 'left':
        this.triggerHapticFeedback();
        // Could trigger next page navigation
        break;
      case 'right':
        this.triggerHapticFeedback();
        // Could trigger previous page navigation
        break;
      case 'up':
        this.triggerHapticFeedback();
        // Could trigger pull to refresh
        break;
      case 'down':
        this.triggerHapticFeedback();
        // Could trigger scroll to top
        break;
    }
  }

  private handleLongPressAction(gesture: TouchGesture): void {
    this.triggerHapticFeedback('strong');
    // Could trigger context menu or other long press actions
  }

  private updateOrientation(): void {
    const orientation = window.innerWidth > window.innerHeight ? 'landscape' : 'portrait';
    this.orientationSubject.next(orientation);
  }

  // Public methods
  isMobile(): boolean {
    return this.isMobileSubject.value;
  }

  isPortrait(): boolean {
    return this.orientationSubject.value === 'portrait';
  }

  isLandscape(): boolean {
    return this.orientationSubject.value === 'landscape';
  }

  // Haptic feedback
  triggerHapticFeedback(intensity: 'light' | 'medium' | 'strong' = 'medium'): void {
    if (!this.config.enableHapticFeedback) return;

    if ('vibrate' in navigator) {
      const intensityMap = {
        light: 10,
        medium: 20,
        strong: 50
      };

      navigator.vibrate(intensityMap[intensity]);
    }
  }

  // Pull to refresh
  enablePullToRefresh(element: HTMLElement): Observable<boolean> {
    if (!this.config.enablePullToRefresh) {
      return new Observable(observer => observer.next(false));
    }

    return new Observable(observer => {
      let startY = 0;
      let currentY = 0;
      let isPulling = false;

      const onTouchStart = (e: TouchEvent) => {
        if (element.scrollTop === 0) {
          startY = e.touches[0].clientY;
          isPulling = true;
        }
      };

      const onTouchMove = (e: TouchEvent) => {
        if (!isPulling) return;

        currentY = e.touches[0].clientY;
        const deltaY = currentY - startY;

        if (deltaY > 0 && deltaY > 50) {
          // Trigger pull to refresh
          observer.next(true);
          isPulling = false;
        }
      };

      const onTouchEnd = () => {
        isPulling = false;
      };

      element.addEventListener('touchstart', onTouchStart, { passive: true });
      element.addEventListener('touchmove', onTouchMove, { passive: false });
      element.addEventListener('touchend', onTouchEnd, { passive: true });

      return () => {
        element.removeEventListener('touchstart', onTouchStart);
        element.removeEventListener('touchmove', onTouchMove);
        element.removeEventListener('touchend', onTouchEnd);
      };
    });
  }

  // Swipe navigation
  enableSwipeNavigation(element: HTMLElement): Observable<'left' | 'right'> {
    if (!this.config.enableSwipeNavigation) {
      return new Observable(observer => observer.complete());
    }

    return new Observable(observer => {
      let startX = 0;
      let isSwiping = false;

      const onTouchStart = (e: TouchEvent) => {
        startX = e.touches[0].clientX;
        isSwiping = true;
      };

      const onTouchMove = (e: TouchEvent) => {
        if (!isSwiping) return;

        const currentX = e.touches[0].clientX;
        const deltaX = currentX - startX;

        if (Math.abs(deltaX) > this.SWIPE_THRESHOLD) {
          const direction = deltaX > 0 ? 'right' : 'left';
          observer.next(direction);
          isSwiping = false;
        }
      };

      const onTouchEnd = () => {
        isSwiping = false;
      };

      element.addEventListener('touchstart', onTouchStart, { passive: true });
      element.addEventListener('touchmove', onTouchMove, { passive: false });
      element.addEventListener('touchend', onTouchEnd, { passive: true });

      return () => {
        element.removeEventListener('touchstart', onTouchStart);
        element.removeEventListener('touchmove', onTouchMove);
        element.removeEventListener('touchend', onTouchEnd);
      };
    });
  }

  // Configuration methods
  updateConfig(newConfig: Partial<MobileConfig>): void {
    this.config = { ...this.config, ...newConfig };
  }

  getConfig(): MobileConfig {
    return { ...this.config };
  }

  // Utility methods
  getTouchSensitivity(): number {
    const sensitivityMap = {
      low: 100,
      medium: 50,
      high: 25
    };
    return sensitivityMap[this.config.touchSensitivity];
  }

  getHapticIntensity(): number {
    const intensityMap = {
      light: 10,
      medium: 20,
      strong: 50
    };
    return intensityMap[this.config.hapticIntensity];
  }

  // Device capabilities
  supportsTouch(): boolean {
    return 'ontouchstart' in window || navigator.maxTouchPoints > 0;
  }

  supportsHaptics(): boolean {
    return 'vibrate' in navigator;
  }

  supportsOrientationChange(): boolean {
    return 'onorientationchange' in window;
  }

  // Screen information
  getScreenSize(): { width: number; height: number } {
    return {
      width: window.innerWidth,
      height: window.innerHeight
    };
  }

  getPixelRatio(): number {
    return window.devicePixelRatio || 1;
  }

  isHighDPI(): boolean {
    return this.getPixelRatio() > 1;
  }

  // Viewport information
  getViewportSize(): { width: number; height: number } {
    return {
      width: document.documentElement.clientWidth,
      height: document.documentElement.clientHeight
    };
  }

  // Safe area handling for notched devices
  getSafeAreaInsets(): { top: number; right: number; bottom: number; left: number } {
    const style = getComputedStyle(document.documentElement);
    
    return {
      top: parseInt(style.getPropertyValue('--sat') || '0'),
      right: parseInt(style.getPropertyValue('--sar') || '0'),
      bottom: parseInt(style.getPropertyValue('--sab') || '0'),
      left: parseInt(style.getPropertyValue('--sal') || '0')
    };
  }

  // Apply safe area CSS variables
  applySafeAreaVariables(): void {
    const insets = this.getSafeAreaInsets();
    
    document.documentElement.style.setProperty('--sat', `${insets.top}px`);
    document.documentElement.style.setProperty('--sar', `${insets.right}px`);
    document.documentElement.style.setProperty('--sab', `${insets.bottom}px`);
    document.documentElement.style.setProperty('--sal', `${insets.left}px`);
  }

  // Cleanup
  destroy(): void {
    if (this.longPressTimer) {
      clearTimeout(this.longPressTimer);
    }
  }
} 