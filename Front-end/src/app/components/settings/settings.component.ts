import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ThemeService, Theme } from '../../services/theme/theme.service';
import { trigger, state, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css'],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('scaleIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8)' }),
        animate('0.4s ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('bounceIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.3)' }),
        animate('0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55)', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('0.3s ease-out', style({ opacity: 1 }))
      ])
    ])
  ]
})
export class SettingsComponent implements OnInit, OnDestroy {
  isLoading = false;
  currentTheme: Theme = 'light';
  isDarkMode = false;
  
  preferencesForm: FormGroup;
  notificationForm: FormGroup;
  
  private destroy$ = new Subject<void>();

  constructor(
    private themeService: ThemeService,
    private fb: FormBuilder
  ) {
    this.preferencesForm = this.fb.group({
      language: ['en', Validators.required],
      timezone: ['UTC', Validators.required],
      currency: ['USD', Validators.required],
      theme: ['light', Validators.required],
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
    this.loadSettings();
    this.setupThemeSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupThemeSubscription(): void {
    this.themeService.currentTheme$
      .pipe(takeUntil(this.destroy$))
      .subscribe(theme => {
        this.currentTheme = theme;
        this.preferencesForm.patchValue({ theme });
      });

    this.themeService.isDarkMode$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isDark => {
        this.isDarkMode = isDark;
      });
  }

  private loadSettings(): void {
    this.isLoading = true;
    
    // Load theme settings
    this.currentTheme = this.themeService.getCurrentTheme();
    this.isDarkMode = this.themeService.isDarkMode();
    
    // Load user preferences (mock data for now)
    this.preferencesForm.patchValue({
      language: 'en',
      timezone: 'UTC',
      currency: 'USD',
      theme: this.currentTheme,
      dateFormat: 'MM/DD/YYYY',
      timeFormat: '12h'
    });
    
    this.isLoading = false;
  }

  onThemeChange(theme: Theme): void {
    this.themeService.setTheme(theme);
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  onPreferencesSubmit(): void {
    if (this.preferencesForm.valid) {
      const formValue = this.preferencesForm.value;
      
      // Update theme if changed
      if (formValue.theme !== this.currentTheme) {
        this.themeService.setTheme(formValue.theme);
      }
      
      // Save other preferences (implement with actual service)
      console.log('Saving preferences:', formValue);
      
      // Show success message
      alert('Preferences saved successfully!');
    }
  }

  onNotificationSubmit(): void {
    if (this.notificationForm.valid) {
      const formValue = this.notificationForm.value;
      
      // Save notification preferences (implement with actual service)
      console.log('Saving notification preferences:', formValue);
      
      // Show success message
      alert('Notification preferences saved successfully!');
    }
  }

  resetPreferences(): void {
    if (confirm('Are you sure you want to reset all preferences to default?')) {
      this.preferencesForm.reset({
        language: 'en',
        timezone: 'UTC',
        currency: 'USD',
        theme: 'light',
        dateFormat: 'MM/DD/YYYY',
        timeFormat: '12h'
      });
      
      this.themeService.resetToDefault();
    }
  }

  resetNotifications(): void {
    if (confirm('Are you sure you want to reset notification preferences to default?')) {
      this.notificationForm.reset({
        emailNotifications: true,
        smsNotifications: false,
        pushNotifications: true,
        transactionAlerts: true,
        securityAlerts: true,
        marketingEmails: false,
        weeklyReports: true,
        monthlyReports: true
      });
    }
  }

  exportSettings(): void {
    const settings = {
      preferences: this.preferencesForm.value,
      notifications: this.notificationForm.value,
      theme: this.themeService.exportThemeConfig()
    };
    
    const dataStr = JSON.stringify(settings, null, 2);
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
          const settings = JSON.parse(e.target?.result as string);
          
          if (settings.preferences) {
            this.preferencesForm.patchValue(settings.preferences);
          }
          
          if (settings.notifications) {
            this.notificationForm.patchValue(settings.notifications);
          }
          
          if (settings.theme) {
            this.themeService.importThemeConfig(settings.theme);
          }
          
          alert('Settings imported successfully!');
        } catch (error) {
          alert('Error importing settings. Please check the file format.');
        }
      };
      reader.readAsText(input.files[0]);
    }
  }

  getAvailableLanguages(): { code: string; name: string; flag: string }[] {
    return [
      { code: 'en', name: 'English', flag: '🇺🇸' },
      { code: 'es', name: 'Español', flag: '🇪🇸' },
      { code: 'fr', name: 'Français', flag: '🇫🇷' },
      { code: 'de', name: 'Deutsch', flag: '🇩🇪' },
      { code: 'it', name: 'Italiano', flag: '🇮🇹' },
      { code: 'pt', name: 'Português', flag: '🇵🇹' },
      { code: 'ar', name: 'العربية', flag: '🇸🇦' },
      { code: 'zh', name: '中文', flag: '🇨🇳' },
      { code: 'ja', name: '日本語', flag: '🇯🇵' },
      { code: 'ko', name: '한국어', flag: '🇰🇷' }
    ];
  }

  getAvailableTimezones(): { value: string; label: string }[] {
    return [
      { value: 'UTC', label: 'UTC (Coordinated Universal Time)' },
      { value: 'EST', label: 'EST (Eastern Standard Time)' },
      { value: 'CST', label: 'CST (Central Standard Time)' },
      { value: 'MST', label: 'MST (Mountain Standard Time)' },
      { value: 'PST', label: 'PST (Pacific Standard Time)' },
      { value: 'GMT', label: 'GMT (Greenwich Mean Time)' },
      { value: 'CET', label: 'CET (Central European Time)' },
      { value: 'JST', label: 'JST (Japan Standard Time)' },
      { value: 'IST', label: 'IST (India Standard Time)' },
      { value: 'AEST', label: 'AEST (Australian Eastern Standard Time)' }
    ];
  }

  getAvailableCurrencies(): { code: string; name: string; symbol: string }[] {
    return [
      { code: 'USD', name: 'US Dollar', symbol: '$' },
      { code: 'EUR', name: 'Euro', symbol: '€' },
      { code: 'GBP', name: 'British Pound', symbol: '£' },
      { code: 'JPY', name: 'Japanese Yen', symbol: '¥' },
      { code: 'CAD', name: 'Canadian Dollar', symbol: 'C$' },
      { code: 'AUD', name: 'Australian Dollar', symbol: 'A$' },
      { code: 'CHF', name: 'Swiss Franc', symbol: 'CHF' },
      { code: 'CNY', name: 'Chinese Yuan', symbol: '¥' },
      { code: 'INR', name: 'Indian Rupee', symbol: '₹' },
      { code: 'BRL', name: 'Brazilian Real', symbol: 'R$' }
    ];
  }

  getAvailableThemes(): { value: Theme; label: string; icon: string }[] {
    return [
      { value: 'light', label: 'Light', icon: '☀️' },
      { value: 'dark', label: 'Dark', icon: '🌙' },
      { value: 'auto', label: 'Auto', icon: '🔄' }
    ];
  }

  getAvailableDateFormats(): { value: string; label: string; example: string }[] {
    return [
      { value: 'MM/DD/YYYY', label: 'MM/DD/YYYY', example: '12/25/2024' },
      { value: 'DD/MM/YYYY', label: 'DD/MM/YYYY', example: '25/12/2024' },
      { value: 'YYYY-MM-DD', label: 'YYYY-MM-DD', example: '2024-12-25' },
      { value: 'MM-DD-YY', label: 'MM-DD-YY', example: '12-25-24' },
      { value: 'DD-MM-YY', label: 'DD-MM-YY', example: '25-12-24' }
    ];
  }

  getAvailableTimeFormats(): { value: string; label: string; example: string }[] {
    return [
      { value: '12h', label: '12-hour', example: '2:30 PM' },
      { value: '24h', label: '24-hour', example: '14:30' }
    ];
  }

  getCurrentLanguageName(): string {
    const language = this.getAvailableLanguages().find(l => l.code === this.preferencesForm.get('language')?.value);
    return language?.name || 'English';
  }

  getCurrentTimezoneName(): string {
    const timezone = this.getAvailableTimezones().find(t => t.value === this.preferencesForm.get('timezone')?.value);
    return timezone?.label || 'UTC';
  }

  getCurrentCurrencyName(): string {
    const currency = this.getAvailableCurrencies().find(c => c.code === this.preferencesForm.get('currency')?.value);
    return currency?.name || 'US Dollar';
  }

  getCurrentThemeName(): string {
    const theme = this.getAvailableThemes().find(t => t.value === this.currentTheme);
    return theme?.label || 'Light';
  }
} 