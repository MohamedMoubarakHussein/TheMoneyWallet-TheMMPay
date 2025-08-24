import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

export type Theme = 'light' | 'dark' | 'auto';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'app-theme';
  private currentTheme = new BehaviorSubject<Theme>('light');
  private isDarkMode = new BehaviorSubject<boolean>(false);

  currentTheme$ = this.currentTheme.asObservable();
  isDarkMode$ = this.isDarkMode.asObservable();

  constructor(@Inject(PLATFORM_ID) private platformId: object) {
    this.initializeTheme();
  }

  private initializeTheme(): void {
    if (isPlatformBrowser(this.platformId)) {
      const savedTheme = localStorage.getItem(this.THEME_KEY) as Theme || 'auto';
      this.setTheme(savedTheme);
      
      window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
        if (this.currentTheme.value === 'auto') {
          this.applyTheme(e.matches ? 'dark' : 'light');
        }
      });
    }
  }

  setTheme(theme: Theme): void {
    this.currentTheme.next(theme);
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.THEME_KEY, theme);
    }
    
    let themeToApply: 'light' | 'dark' = 'light';
    if (theme === 'dark') {
      themeToApply = 'dark';
    } else if (theme === 'auto' && isPlatformBrowser(this.platformId)) {
      themeToApply = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    this.applyTheme(themeToApply);
  }

  private applyTheme(theme: 'light' | 'dark'): void {
    this.isDarkMode.next(theme === 'dark');
    if (isPlatformBrowser(this.platformId)) {
      document.documentElement.classList.toggle('dark', theme === 'dark');
    }
  }

  toggleTheme(): void {
    const current = this.currentTheme.value;
    this.setTheme(current === 'light' ? 'dark' : 'light');
  }

  exportThemeConfig(): Theme {
    return this.currentTheme.value;
  }

  importThemeConfig(theme: Theme): void {
    this.setTheme(theme);
  }
}