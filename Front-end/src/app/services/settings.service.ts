import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, tap } from 'rxjs/operators';
import { ThemeService } from './theme/theme.service';

export interface Preferences {
  language: string;
  timezone: string;
  currency: string;
  dateFormat: string;
  timeFormat: string;
}

export interface Notifications {
  emailNotifications: boolean;
  smsNotifications: boolean;
  pushNotifications: boolean;
  transactionAlerts: boolean;
  securityAlerts: boolean;
  marketingEmails: boolean;
  weeklyReports: boolean;
  monthlyReports: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private preferences = new BehaviorSubject<Preferences | null>(null);
  private notifications = new BehaviorSubject<Notifications | null>(null);
  private loading = new BehaviorSubject<boolean>(false);

  constructor(private themeService: ThemeService) {
    this.loadSettings();
  }

  getPreferences(): Observable<Preferences | null> { return this.preferences.asObservable(); }
  getNotifications(): Observable<Notifications | null> { return this.notifications.asObservable(); }
  isLoading(): Observable<boolean> { return this.loading.asObservable(); }

  loadSettings() {
    this.loading.next(true);
    // Mock data loading
    of({
      preferences: { language: 'en', timezone: 'UTC', currency: 'USD', dateFormat: 'MM/DD/YYYY', timeFormat: '12h' },
      notifications: { emailNotifications: true, smsNotifications: false, pushNotifications: true, transactionAlerts: true, securityAlerts: true, marketingEmails: false, weeklyReports: true, monthlyReports: true }
    }).pipe(delay(1000)).subscribe(settings => {
      this.preferences.next(settings.preferences);
      this.notifications.next(settings.notifications);
      this.loading.next(false);
    });
  }

  updatePreferences(preferences: Preferences): Observable<{ success: boolean }> {
    this.loading.next(true);
    return of({ success: true }).pipe(
      delay(1000),
      tap(() => {
        this.preferences.next(preferences);
        this.loading.next(false);
      })
    );
  }

  updateNotifications(notifications: Notifications): Observable<{ success: boolean }> {
    this.loading.next(true);
    return of({ success: true }).pipe(
      delay(1000),
      tap(() => {
        this.notifications.next(notifications);
        this.loading.next(false);
      })
    );
  }

  exportSettings(): string {
    const settings = {
      preferences: this.preferences.value,
      notifications: this.notifications.value,
      theme: this.themeService.exportThemeConfig()
    };
    return JSON.stringify(settings, null, 2);
  }

  importSettings(settingsJson: string): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        const settings = JSON.parse(settingsJson);
        if (settings.preferences) this.preferences.next(settings.preferences);
        if (settings.notifications) this.notifications.next(settings.notifications);
        if (settings.theme) this.themeService.importThemeConfig(settings.theme);
        resolve();
      } catch (error) {
        reject(error);
      }
    });
  }
}