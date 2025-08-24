import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { SecuritySettings } from '../../entity/UnifiedResponse';
import { SecuritySettingsService } from '../../services/security-settings.service';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-security-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './security-settings.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class SecuritySettingsComponent {
  settings$: Observable<SecuritySettings | null>;
  isLoading$: Observable<boolean>;

  showTwoFactorSetup = false;
  showPasswordChange = false;
  twoFactorForm: FormGroup;
  passwordForm: FormGroup;

  constructor(
    private securityService: SecuritySettingsService,
    private fb: FormBuilder
  ) {
    this.settings$ = this.securityService.getSettings();
    this.isLoading$ = this.securityService.isLoading$();

    this.twoFactorForm = this.fb.group({
      method: ['authenticator', Validators.required],
      code: ['', Validators.required]
    });
    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    });
  }

  setupTwoFactor(): void {
    if (this.twoFactorForm.valid) {
      this.securityService.setupTwoFactor(this.twoFactorForm.value).subscribe();
    }
  }

  changePassword(): void {
    if (this.passwordForm.valid) {
      this.securityService.changePassword(this.passwordForm.value).subscribe();
    }
  }
  
  toggleTwoFactorSetup(): void { this.showTwoFactorSetup = !this.showTwoFactorSetup; }
  togglePasswordChange(): void { this.showPasswordChange = !this.showPasswordChange; }
  
  getTwoFactorMethodDisplayName(method: string): string { return method; }
}