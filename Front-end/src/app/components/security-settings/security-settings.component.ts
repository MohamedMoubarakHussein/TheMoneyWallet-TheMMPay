import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { trigger, state, style, transition, animate, query, stagger, useAnimation } from '@angular/animations';
import { SecurityService, TwoFactorSetupRequest, PasswordChangeRequest } from '../../services/security/security.service';
import { SecuritySettings } from '../../entity/UnifiedResponse';
import { AnimationService } from '../../services/animation/animation.service';

@Component({
  selector: 'app-security-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './security-settings.component.html',
  styleUrls: ['./security-settings.component.css'],
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(15px) scale(0.97)' }),
        animate('0.4s cubic-bezier(0.35, 0, 0.25, 1)', 
          style({ opacity: 1, transform: 'translateY(0) scale(1)' })
        )
      ]),
      transition(':leave', [
        animate('0.3s ease-out', style({ opacity: 0, transform: 'scale(0.97)' }))
      ])
    ]),
    trigger('errorAnimation', [
      transition(':enter', [
        style({ opacity: 0, height: 0 }),
        animate('0.2s ease-out', style({ opacity: 1, height: '*' }))
      ]),
      transition(':leave', [
        animate('0.2s ease-out', style({ opacity: 0, height: 0 }))
      ])
    ])
  ]
})
export class SecuritySettingsComponent implements OnInit, OnDestroy {
  securitySettings: SecuritySettings | null = null;
  isLoading = false;
  showTwoFactorSetup = false;
  showPasswordChange = false;
  
  twoFactorForm: FormGroup;
  passwordForm: FormGroup;
  
  private destroy$ = new Subject<void>();

  constructor(
    private securityService: SecurityService,
    private fb: FormBuilder,
    private animationService: AnimationService
  ) {
    this.twoFactorForm = this.fb.group({
      method: ['authenticator', Validators.required],
      phoneNumber: [''],
      email: [''],
      code: ['', Validators.required]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    this.loadSecuritySettings();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadSecuritySettings(): void {
    this.isLoading = true;
    this.securityService.getSecuritySettings()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (settings) => {
          this.securitySettings = settings;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading security settings:', error);
          this.isLoading = false;
        }
      });
  }

  setupTwoFactor(): void {
    if (this.twoFactorForm.valid) {
      const formValue = this.twoFactorForm.value;
      const setupData: TwoFactorSetupRequest = {
        method: formValue.method,
        phoneNumber: formValue.phoneNumber,
        email: formValue.email
      };

      this.securityService.setupTwoFactor(setupData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.showTwoFactorSetup = true;
            }
          },
          error: (error) => {
            console.error('Error setting up 2FA:', error);
          }
        });
    }
  }

  verifyTwoFactorSetup(): void {
    if (this.twoFactorForm.valid) {
      const formValue = this.twoFactorForm.value;
      
      this.securityService.verifyTwoFactorSetup({
        code: formValue.code,
        method: formValue.method
      }).pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.enableTwoFactor();
            }
          },
          error: (error) => {
            console.error('Error verifying 2FA setup:', error);
          }
        });
    }
  }

  enableTwoFactor(): void {
    this.securityService.enableTwoFactor()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.loadSecuritySettings();
            this.showTwoFactorSetup = false;
            this.twoFactorForm.reset();
          }
        },
        error: (error) => {
          console.error('Error enabling 2FA:', error);
        }
      });
  }

  disableTwoFactor(): void {
    const password = prompt('Please enter your password to disable 2FA:');
    if (password) {
      this.securityService.disableTwoFactor(password)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.loadSecuritySettings();
            }
          },
          error: (error) => {
            console.error('Error disabling 2FA:', error);
          }
        });
    }
  }

  changePassword(): void {
    if (this.passwordForm.valid) {
      const formValue = this.passwordForm.value;
      const passwordData: PasswordChangeRequest = {
        currentPassword: formValue.currentPassword,
        newPassword: formValue.newPassword,
        confirmPassword: formValue.confirmPassword
      };

      this.securityService.changePassword(passwordData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.showPasswordChange = false;
              this.passwordForm.reset();
              alert('Password changed successfully!');
            }
          },
          error: (error) => {
            console.error('Error changing password:', error);
          }
        });
    }
  }

  updateSecuritySettings(updates: Partial<SecuritySettings>): void {
    this.securityService.updateSecuritySettings(updates)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (settings) => {
          this.securitySettings = settings;
        },
        error: (error) => {
          console.error('Error updating security settings:', error);
        }
      });
  }

  toggleLoginNotifications(): void {
    if (this.securitySettings) {
      this.updateSecuritySettings({
        loginNotifications: !this.securitySettings.loginNotifications
      });
    }
  }

  toggleTransactionNotifications(): void {
    if (this.securitySettings) {
      this.updateSecuritySettings({
        transactionNotifications: !this.securitySettings.transactionNotifications
      });
    }
  }

  updateSessionTimeout(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.value) {
      const timeout = +input.value;
      this.updateSecuritySettings({ sessionTimeout: timeout });
    }
  }

  updateMaxLoginAttempts(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.value) {
      const attempts = +input.value;
      this.updateSecuritySettings({ maxLoginAttempts: attempts });
    }
  }

  private passwordMatchValidator(group: FormGroup): { [key: string]: any } | null {
    const password = group.get('newPassword');
    const confirmPassword = group.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    
    return null;
  }

  toggleTwoFactorSetup(): void {
    this.showTwoFactorSetup = !this.showTwoFactorSetup;
    if (!this.showTwoFactorSetup) {
      this.twoFactorForm.reset();
    }
  }

  togglePasswordChange(): void {
    this.showPasswordChange = !this.showPasswordChange;
    if (!this.showPasswordChange) {
      this.passwordForm.reset();
    }
  }

  getTwoFactorMethodDisplayName(method: string): string {
    const methodNames: { [key: string]: string } = {
      'sms': 'SMS',
      'email': 'Email',
      'authenticator': 'Authenticator App'
    };
    return methodNames[method] || method;
  }

  getTwoFactorMethodIcon(method: string): string {
    const icons: { [key: string]: string } = {
      'sms': '📱',
      'email': '📧',
      'authenticator': '🔐'
    };
    return icons[method] || '🔐';
  }
} 