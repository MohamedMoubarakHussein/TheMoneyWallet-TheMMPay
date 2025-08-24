import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { SecuritySettings } from '../entity/UnifiedResponse';
import { TwoFactorSetupRequest } from './security/security.service';

@Injectable({
  providedIn: 'root'
})
export class SecuritySettingsService {
  private settings = new BehaviorSubject<SecuritySettings | null>(null);
  private isLoading = new BehaviorSubject<boolean>(false);

  constructor() {
    this.loadSettings();
  }

  getSettings(): Observable<SecuritySettings | null> { return this.settings.asObservable(); }
  isLoading$(): Observable<boolean> { return this.isLoading.asObservable(); }

  loadSettings() {
    this.isLoading.next(true);
    of({
      // Mock data
    }).pipe(delay(1000)).subscribe(settings => {
      this.settings.next(settings as SecuritySettings);
      this.isLoading.next(false);
    });
  }

  updateSettings(updates: Partial<SecuritySettings>): Observable<SecuritySettings> {
    const updatedSettings = { ...this.settings.value, ...updates } as SecuritySettings;
    this.settings.next(updatedSettings);
    return of(updatedSettings);
  }

  setupTwoFactor(): Observable<{ success: boolean }> {
    return of({ success: true });
  }

  verifyTwoFactorSetup(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    verificationData: { code: string, method: string }
  ): Observable<{ success: boolean }> {
    return of({ success: true });
  }

  enableTwoFactor(): Observable<{ success: boolean }> {
    return this.updateSettings({ twoFactorEnabled: true }).pipe(map(() => ({ success: true })));
  }

  disableTwoFactor(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    password: string
  ): Observable<{ success: boolean }> {
    return this.updateSettings({ twoFactorEnabled: false }).pipe(map(() => ({ success: true })));
  }

  changePassword(): Observable<{ success: boolean }> {
    return of({ success: true });
  }
}