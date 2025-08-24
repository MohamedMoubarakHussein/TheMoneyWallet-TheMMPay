import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable, fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

export interface ScreenSize {
  width: number;
  height: number;
  isMobile: boolean;
  isTablet: boolean;
  isDesktop: boolean;
  breakpoint: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
}

export interface ResponsiveConfig {
  mobileBreakpoint: number;
  tabletBreakpoint: number;
  desktopBreakpoint: number;
  largeDesktopBreakpoint: number;
}

@Injectable({
  providedIn: 'root'
})
export class ResponsiveService {
  private readonly DEFAULT_CONFIG: ResponsiveConfig = {
    mobileBreakpoint: 480,
    tabletBreakpoint: 720,
    desktopBreakpoint: 1024,
    largeDesktopBreakpoint: 1440
  };

  private screenSizeSubject = new BehaviorSubject<ScreenSize>(this.getCurrentScreenSize());
  private isMobileSubject = new BehaviorSubject<boolean>(this.isMobileView());
  private isTabletSubject = new BehaviorSubject<boolean>(this.isTabletView());
  private isDesktopSubject = new BehaviorSubject<boolean>(this.isDesktopView());

  public screenSize$ = this.screenSizeSubject.asObservable();
  public isMobile$ = this.isMobileSubject.asObservable();
  public isTablet$ = this.isTabletSubject.asObservable();
  public isDesktop$ = this.isDesktopSubject.asObservable();

  private config: ResponsiveConfig;

  constructor(@Inject(PLATFORM_ID) private platformId: object) {
    this.config = this.DEFAULT_CONFIG;
    this.initializeResponsiveService();
  }

  private initializeResponsiveService(): void {
    // Only initialize browser-specific functionality if we're in a browser
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    // Listen for window resize events
    fromEvent(window, 'resize')
      .pipe(
        debounceTime(100), // Debounce resize events
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.updateScreenSize();
      });

    // Listen for orientation change events
    fromEvent(window, 'orientationchange')
      .pipe(
        debounceTime(100)
      )
      .subscribe(() => {
        setTimeout(() => this.updateScreenSize(), 100);
      });

    // Initial screen size check
    this.updateScreenSize();
  }

  private updateScreenSize(): void {
    const screenSize = this.getCurrentScreenSize();
    this.screenSizeSubject.next(screenSize);
    
    // Update individual breakpoint subjects
    this.isMobileSubject.next(screenSize.isMobile);
    this.isTabletSubject.next(screenSize.isTablet);
    this.isDesktopSubject.next(screenSize.isDesktop);
  }

  private getCurrentScreenSize(): ScreenSize {
    // Check if we're in a browser environment
    if (!isPlatformBrowser(this.platformId)) {
      // Return default values for SSR
      return {
        width: 1024,
        height: 768,
        isMobile: false,
        isTablet: false,
        isDesktop: true,
        breakpoint: 'md'
      };
    }

    const width = window.innerWidth;
    const height = window.innerHeight;
    
    return {
      width,
      height,
      isMobile: this.isMobileView(),
      isTablet: this.isTabletView(),
      isDesktop: this.isDesktopView(),
      breakpoint: this.getBreakpoint(width)
    };
  }

  private isMobileView(): boolean {
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }
    return window.innerWidth < this.config.tabletBreakpoint;
  }

  private isTabletView(): boolean {
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }
    return window.innerWidth >= this.config.tabletBreakpoint && 
           window.innerWidth < this.config.desktopBreakpoint;
  }

  private isDesktopView(): boolean {
    if (!isPlatformBrowser(this.platformId)) {
      return true;
    }
    return window.innerWidth >= this.config.desktopBreakpoint;
  }

  private getBreakpoint(width: number): 'xs' | 'sm' | 'md' | 'lg' | 'xl' {
    if (width < this.config.mobileBreakpoint) return 'xs';
    if (width < this.config.tabletBreakpoint) return 'sm';
    if (width < this.config.desktopBreakpoint) return 'md';
    if (width < this.config.largeDesktopBreakpoint) return 'lg';
    return 'xl';
  }

  // Public methods
  getCurrentScreenSizeValue(): ScreenSize {
    return this.screenSizeSubject.value;
  }

  isMobile(): boolean {
    return this.isMobileSubject.value;
  }

  isTablet(): boolean {
    return this.isTabletSubject.value;
  }

  isDesktop(): boolean {
    return this.isDesktopSubject.value;
  }

  getCurrentBreakpoint(): 'xs' | 'sm' | 'md' | 'lg' | 'xl' {
    return this.screenSizeSubject.value.breakpoint;
  }

  getScreenWidth(): number {
    return this.screenSizeSubject.value.width;
  }

  getScreenHeight(): number {
    return this.screenSizeSubject.value.height;
  }

  // Check if screen is smaller than a specific breakpoint
  isSmallerThan(breakpoint: 'xs' | 'sm' | 'md' | 'lg' | 'xl'): boolean {
    const currentBreakpoint = this.getCurrentBreakpoint();
    const breakpointOrder = ['xs', 'sm', 'md', 'lg', 'xl'];
    const currentIndex = breakpointOrder.indexOf(currentBreakpoint);
    const targetIndex = breakpointOrder.indexOf(breakpoint);
    
    return currentIndex < targetIndex;
  }

  // Check if screen is larger than a specific breakpoint
  isLargerThan(breakpoint: 'xs' | 'sm' | 'md' | 'lg' | 'xl'): boolean {
    const currentBreakpoint = this.getCurrentBreakpoint();
    const breakpointOrder = ['xs', 'sm', 'md', 'lg', 'xl'];
    const currentIndex = breakpointOrder.indexOf(currentBreakpoint);
    const targetIndex = breakpointOrder.indexOf(breakpoint);
    
    return currentIndex > targetIndex;
  }

  // Check if screen is between two breakpoints
  isBetween(minBreakpoint: 'xs' | 'sm' | 'md' | 'lg' | 'xl', 
            maxBreakpoint: 'xs' | 'sm' | 'md' | 'lg' | 'xl'): boolean {
    return this.isLargerThan(minBreakpoint) && this.isSmallerThan(maxBreakpoint);
  }

  // Get responsive class based on current breakpoint
  getResponsiveClass(baseClass: string, mobileClass?: string, tabletClass?: string, desktopClass?: string): string {
    if (this.isMobile() && mobileClass) {
      return mobileClass;
    } else if (this.isTablet() && tabletClass) {
      return tabletClass;
    } else if (this.isDesktop() && desktopClass) {
      return desktopClass;
    }
    return baseClass;
  }

  // Get responsive value based on current breakpoint
  getResponsiveValue<T>(mobileValue: T, tabletValue: T, desktopValue: T): T {
    if (this.isMobile()) {
      return mobileValue;
    } else if (this.isTablet()) {
      return tabletValue;
    } else {
      return desktopValue;
    }
  }

  // Get responsive spacing
  getResponsiveSpacing(mobile: string, tablet: string, desktop: string): string {
    return this.getResponsiveValue(mobile, tablet, desktop);
  }

  // Get responsive font size
  getResponsiveFontSize(mobile: string, tablet: string, desktop: string): string {
    return this.getResponsiveValue(mobile, tablet, desktop);
  }

  // Check if device supports touch
  isTouchDevice(): boolean {
    return 'ontouchstart' in window || navigator.maxTouchPoints > 0;
  }

  // Check if device is in landscape mode
  isLandscape(): boolean {
    return window.innerWidth > window.innerHeight;
  }

  // Check if device is in portrait mode
  isPortrait(): boolean {
    return window.innerHeight > window.innerWidth;
  }

  // Get device pixel ratio
  getDevicePixelRatio(): number {
    return window.devicePixelRatio || 1;
  }

  // Check if device supports high DPI
  isHighDPI(): boolean {
    return this.getDevicePixelRatio() > 1;
  }

  // Get viewport dimensions
  getViewportDimensions(): { width: number; height: number } {
    return {
      width: window.innerWidth,
      height: window.innerHeight
    };
  }

  // Check if element is in viewport
  isElementInViewport(element: HTMLElement): boolean {
    const rect = element.getBoundingClientRect();
    return (
      rect.top >= 0 &&
      rect.left >= 0 &&
      rect.bottom <= window.innerHeight &&
      rect.right <= window.innerWidth
    );
  }

  // Get scroll position
  getScrollPosition(): { x: number; y: number } {
    return {
      x: window.pageXOffset || document.documentElement.scrollLeft,
      y: window.pageYOffset || document.documentElement.scrollTop
    };
  }

  // Check if user prefers reduced motion
  prefersReducedMotion(): boolean {
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  }

  // Check if user prefers dark mode
  prefersDarkMode(): boolean {
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  // Check if user prefers high contrast
  prefersHighContrast(): boolean {
    return window.matchMedia('(prefers-contrast: high)').matches;
  }

  // Update configuration
  updateConfig(newConfig: Partial<ResponsiveConfig>): void {
    this.config = { ...this.config, ...newConfig };
    this.updateScreenSize();
  }

  // Get current configuration
  getConfig(): ResponsiveConfig {
    return { ...this.config };
  }

  // Force refresh of screen size
  refresh(): void {
    this.updateScreenSize();
  }

  // Subscribe to specific breakpoint changes
  onBreakpointChange(breakpoint: 'xs' | 'sm' | 'md' | 'lg' | 'xl'): Observable<boolean> {
    return new Observable(observer => {
      const subscription = this.screenSize$.subscribe(screenSize => {
        observer.next(screenSize.breakpoint === breakpoint);
      });
      
      return () => subscription.unsubscribe();
    });
  }

  // Subscribe to mobile state changes
  onMobileChange(): Observable<boolean> {
    return this.isMobile$;
  }

  // Subscribe to tablet state changes
  onTabletChange(): Observable<boolean> {
    return this.isTablet$;
  }

  // Subscribe to desktop state changes
  onDesktopChange(): Observable<boolean> {
    return this.isDesktop$;
  }

  // Utility method to get CSS custom property value
  getCSSVariable(propertyName: string): string {
    return getComputedStyle(document.documentElement).getPropertyValue(propertyName).trim();
  }

  // Utility method to set CSS custom property value
  setCSSVariable(propertyName: string, value: string): void {
    document.documentElement.style.setProperty(propertyName, value);
  }

  // Get responsive CSS variables
  getResponsiveCSSVariables(): { [key: string]: string } {
    return {
      '--mobile-breakpoint': `${this.config.mobileBreakpoint}px`,
      '--tablet-breakpoint': `${this.config.tabletBreakpoint}px`,
      '--desktop-breakpoint': `${this.config.desktopBreakpoint}px`,
      '--large-desktop-breakpoint': `${this.config.largeDesktopBreakpoint}px`,
      '--current-breakpoint': this.getCurrentBreakpoint(),
      '--is-mobile': this.isMobile() ? 'true' : 'false',
      '--is-tablet': this.isTablet() ? 'true' : 'false',
      '--is-desktop': this.isDesktop() ? 'true' : 'false'
    };
  }

  // Apply responsive CSS variables
  applyResponsiveCSSVariables(): void {
    const variables = this.getResponsiveCSSVariables();
    Object.entries(variables).forEach(([property, value]) => {
      this.setCSSVariable(property, value);
    });
  }
} 