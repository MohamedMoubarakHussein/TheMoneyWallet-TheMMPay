import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

export type Theme = 'light' | 'dark' | 'auto';

export interface ThemeConfig {
  name: Theme;
  displayName: string;
  icon: string;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'app-theme';
  private readonly THEME_PREFERENCE_KEY = 'app-theme-preference';
  
  private currentThemeSubject = new BehaviorSubject<Theme>('light');
  private isDarkModeSubject = new BehaviorSubject<boolean>(false);
  
  public currentTheme$ = this.currentThemeSubject.asObservable();
  public isDarkMode$ = this.isDarkModeSubject.asObservable();

  private themes: ThemeConfig[] = [
    { name: 'light', displayName: 'Light', icon: '☀️', isActive: true },
    { name: 'dark', displayName: 'Dark', icon: '🌙', isActive: false },
    { name: 'auto', displayName: 'Auto', icon: '🔄', isActive: false }
  ];

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.initializeTheme();
  }

  private initializeTheme(): void {
    const savedTheme = this.getSavedTheme();
    const systemPrefersDark = this.getSystemPreference();
    
    let themeToApply: Theme = savedTheme;
    
    if (savedTheme === 'auto') {
      themeToApply = systemPrefersDark ? 'dark' : 'light';
    }
    
    this.applyTheme(themeToApply);
    this.updateThemeConfig(themeToApply);
    
    // Listen for system theme changes
    this.setupSystemThemeListener();
  }

  private getSavedTheme(): Theme {
    // Only access localStorage in browser environment
    if (!isPlatformBrowser(this.platformId)) {
      return 'light';
    }
    const saved = localStorage.getItem(this.THEME_KEY);
    return (saved as Theme) || 'light';
  }

  private getSystemPreference(): boolean {
    // Only access window in browser environment
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  private setupSystemThemeListener(): void {
    // Only access window in browser environment
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    
    mediaQuery.addEventListener('change', (e) => {
      if (this.currentThemeSubject.value === 'auto') {
        const newTheme = e.matches ? 'dark' : 'light';
        this.applyTheme(newTheme);
      }
    });
  }

  setTheme(theme: Theme): void {
    this.currentThemeSubject.next(theme);
    
    // Only access localStorage in browser environment
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.THEME_KEY, theme);
    }
    
    let themeToApply: Theme = theme;
    
    if (theme === 'auto') {
      themeToApply = this.getSystemPreference() ? 'dark' : 'light';
    }
    
    this.applyTheme(themeToApply);
    this.updateThemeConfig(theme);
  }

  private applyTheme(theme: Theme): void {
    const isDark = theme === 'dark';
    this.isDarkModeSubject.next(isDark);
    
    // Only access document in browser environment
    if (isPlatformBrowser(this.platformId)) {
      // Apply theme to document
      document.documentElement.setAttribute('data-theme', theme);
      document.body.classList.toggle('dark-mode', isDark);
      
      // Update CSS custom properties
      this.updateCSSVariables(theme);
    }
  }

  private updateCSSVariables(theme: Theme): void {
    const root = document.documentElement;
    
    if (theme === 'dark') {
      root.style.setProperty('--primary-bg', '#1a1a1a');
      root.style.setProperty('--secondary-bg', '#2d2d2d');
      root.style.setProperty('--text-primary', '#ffffff');
      root.style.setProperty('--text-secondary', '#b3b3b3');
      root.style.setProperty('--border-color', '#404040');
      root.style.setProperty('--shadow-color', 'rgba(0, 0, 0, 0.3)');
      root.style.setProperty('--card-bg', '#2d2d2d');
      root.style.setProperty('--hover-bg', '#404040');
    } else {
      root.style.setProperty('--primary-bg', '#ffffff');
      root.style.setProperty('--secondary-bg', '#f8f9fa');
      root.style.setProperty('--text-primary', '#212529');
      root.style.setProperty('--text-secondary', '#6c757d');
      root.style.setProperty('--border-color', '#dee2e6');
      root.style.setProperty('--shadow-color', 'rgba(0, 0, 0, 0.1)');
      root.style.setProperty('--card-bg', '#ffffff');
      root.style.setProperty('--hover-bg', '#e9ecef');
    }
  }

  private updateThemeConfig(activeTheme: Theme): void {
    this.themes.forEach(theme => {
      theme.isActive = theme.name === activeTheme;
    });
  }

  getCurrentTheme(): Theme {
    return this.currentThemeSubject.value;
  }

  isDarkMode(): boolean {
    return this.isDarkModeSubject.value;
  }

  getThemes(): ThemeConfig[] {
    return [...this.themes];
  }

  toggleTheme(): void {
    const current = this.currentThemeSubject.value;
    const nextTheme = current === 'light' ? 'dark' : 'light';
    this.setTheme(nextTheme);
  }

  // Get theme-specific color palette
  getColorPalette(): { [key: string]: string } {
    const isDark = this.isDarkModeSubject.value;
    
    if (isDark) {
      return {
        primary: '#4CAF50',
        secondary: '#2196F3',
        success: '#4CAF50',
        warning: '#FF9800',
        error: '#F44336',
        info: '#2196F3',
        background: '#1a1a1a',
        surface: '#2d2d2d',
        text: '#ffffff',
        textSecondary: '#b3b3b3'
      };
    } else {
      return {
        primary: '#4CAF50',
        secondary: '#2196F3',
        success: '#4CAF50',
        warning: '#FF9800',
        error: '#F44336',
        info: '#2196F3',
        background: '#ffffff',
        surface: '#f8f9fa',
        text: '#212529',
        textSecondary: '#6c757d'
      };
    }
  }

  // Get theme-specific spacing
  getSpacing(): { [key: string]: string } {
    return {
      xs: '4px',
      sm: '8px',
      md: '16px',
      lg: '24px',
      xl: '32px',
      xxl: '48px'
    };
  }

  // Get theme-specific typography
  getTypography(): { [key: string]: string } {
    return {
      fontFamily: '"Roboto", "Helvetica Neue", sans-serif',
      fontSizeSmall: '12px',
      fontSizeBase: '14px',
      fontSizeLarge: '16px',
      fontSizeXLarge: '18px',
      fontSizeXXLarge: '24px',
      fontWeightLight: '300',
      fontWeightNormal: '400',
      fontWeightMedium: '500',
      fontWeightBold: '700'
    };
  }

  // Get theme-specific shadows
  getShadows(): { [key: string]: string } {
    const isDark = this.isDarkModeSubject.value;
    const shadowColor = isDark ? 'rgba(0, 0, 0, 0.3)' : 'rgba(0, 0, 0, 0.1)';
    
    return {
      small: `0 2px 4px ${shadowColor}`,
      medium: `0 4px 8px ${shadowColor}`,
      large: `0 8px 16px ${shadowColor}`,
      xlarge: `0 16px 32px ${shadowColor}`
    };
  }

  // Get theme-specific transitions
  getTransitions(): { [key: string]: string } {
    return {
      fast: '0.15s ease-in-out',
      normal: '0.3s ease-in-out',
      slow: '0.5s ease-in-out'
    };
  }

  // Export current theme configuration
  exportThemeConfig(): string {
    const config = {
      theme: this.currentThemeSubject.value,
      isDarkMode: this.isDarkModeSubject.value,
      colors: this.getColorPalette(),
      spacing: this.getSpacing(),
      typography: this.getTypography(),
      shadows: this.getShadows(),
      transitions: this.getTransitions()
    };
    
    return JSON.stringify(config, null, 2);
  }

  // Import theme configuration
  importThemeConfig(config: string): boolean {
    try {
      const parsed = JSON.parse(config);
      if (parsed.theme) {
        this.setTheme(parsed.theme);
        return true;
      }
    } catch (error) {
      console.error('Error importing theme config:', error);
    }
    return false;
  }

  // Reset to default theme
  resetToDefault(): void {
    this.setTheme('light');
  }

  // Get theme statistics (for analytics)
  getThemeStats(): { light: number; dark: number; auto: number } {
    // Only access localStorage in browser environment
    if (!isPlatformBrowser(this.platformId)) {
      return { light: 0, dark: 0, auto: 0 };
    }
    
    const stats = localStorage.getItem(this.THEME_PREFERENCE_KEY);
    if (stats) {
      return JSON.parse(stats);
    }
    return { light: 0, dark: 0, auto: 0 };
  }

  // Update theme statistics
  private updateThemeStats(theme: Theme): void {
    const stats = this.getThemeStats();
    stats[theme]++;
    
    // Only access localStorage in browser environment
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.THEME_PREFERENCE_KEY, JSON.stringify(stats));
    }
  }
} 