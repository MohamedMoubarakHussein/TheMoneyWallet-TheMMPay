import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, Observable } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ThemeService, Theme } from '../../services/theme/theme.service';
import { SettingsService } from '../../services/settings.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css'],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class SettingsComponent implements OnInit, OnDestroy {
  isLoading$: Observable<boolean>;
  currentTheme$: Observable<Theme>;
  isDarkMode$: Observable<boolean>;
  
  preferencesForm: FormGroup;
  notificationForm: FormGroup;
  
  private destroy$ = new Subject<void>();

  constructor(
    private themeService: ThemeService,
    private settingsService: SettingsService,
    private fb: FormBuilder
  ) {
    this.isLoading$ = this.settingsService.isLoading();
    this.currentTheme$ = this.themeService.currentTheme$;
    this.isDarkMode$ = this.themeService.isDarkMode$;

    this.preferencesForm = this.fb.group({
      language: ['en', Validators.required],
      timezone: ['UTC', Validators.required],
      currency: ['USD', Validators.required],
      dateFormat: ['MM/DD/YYYY', Validators.required],
      timeFormat: ['12h', Validators.required]
    });

    this.notificationForm = this.fb.group({
      emailNotifications: [true],
      smsNotifications: [false],
      pushNotifications: [true],
      transactionAlerts: [true],
      securityAlerts: [true],
      marketingEmails: [false],
      weeklyReports: [true],
      monthlyReports: [true]
    });
  }

  ngOnInit(): void {
    this.settingsService.getPreferences().pipe(takeUntil(this.destroy$)).subscribe(prefs => {
      if (prefs) this.preferencesForm.patchValue(prefs);
    });
    this.settingsService.getNotifications().pipe(takeUntil(this.destroy$)).subscribe(notifs => {
      if (notifs) this.notificationForm.patchValue(notifs);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onThemeChange(theme: string): void {
    this.themeService.setTheme(theme as Theme);
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  onPreferencesSubmit(): void {
    if (this.preferencesForm.valid) {
      this.settingsService.updatePreferences(this.preferencesForm.value).subscribe();
    }
  }

  onNotificationSubmit(): void {
    if (this.notificationForm.valid) {
      this.settingsService.updateNotifications(this.notificationForm.value).subscribe();
    }
  }

  exportSettings(): void {
    const dataStr = this.settingsService.exportSettings();
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = window.URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'themmpay-settings.json';
    link.click();
    window.URL.revokeObjectURL(url);
  }

  importSettings(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const settingsJson = e.target?.result as string;
          this.settingsService.importSettings(settingsJson)
            .then(() => alert('Settings imported successfully!'))
            .catch(() => alert('Error importing settings.'));
        } catch (error) {
          alert('Error reading file.');
        }
      };
      reader.readAsText(input.files[0]);
    }
  }
  
  // Methods to get data for the template
  getAvailableLanguages = () => [{ code: 'en', name: 'English', flag: '🇺🇸' }, { code: 'es', name: 'Español', flag: '🇪🇸' }];
  getAvailableTimezones = () => [{ value: 'UTC', label: 'UTC' }, { value: 'EST', label: 'EST' }];
  getAvailableCurrencies = () => [{ code: 'USD', name: 'US Dollar', symbol: '$' }, { code: 'EUR', name: 'Euro', symbol: '€' }];
  getAvailableThemes = () => [{ value: 'light', label: 'Light', icon: '☀️' }, { value: 'dark', label: 'Dark', icon: '🌙' }, { value: 'auto', label: 'Auto', icon: '🔄' }];
  getAvailableDateFormats = () => [{ value: 'MM/DD/YYYY', label: 'MM/DD/YYYY', example: '12/25/2024' }];
  getAvailableTimeFormats = () => [{ value: '12h', label: '12-hour', example: '2:30 PM' }, { value: '24h', label: '24-hour', example: '14:30' }];
  
  getCurrentThemeName(theme: Theme | null): string {
    if (!theme) return '';
    return this.getAvailableThemes().find(t => t.value === theme)?.label || '';
  }
  
  getCurrentLanguageName(): string {
    const langCode = this.preferencesForm.get('language')?.value;
    return this.getAvailableLanguages().find(l => l.code === langCode)?.name || '';
  }
  
  getCurrentCurrencyName(): string {
    const currCode = this.preferencesForm.get('currency')?.value;
    return this.getAvailableCurrencies().find(c => c.code === currCode)?.name || '';
  }
  
  getCurrentTimezoneName(): string {
    const tzValue = this.preferencesForm.get('timezone')?.value;
    return this.getAvailableTimezones().find(t => t.value === tzValue)?.label || '';
  }
}