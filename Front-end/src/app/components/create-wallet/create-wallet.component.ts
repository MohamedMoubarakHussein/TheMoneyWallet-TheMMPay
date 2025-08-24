import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { CreateWalletService, CreateWalletForm } from '../../services/create-wallet.service';
import { WalletType } from '../../entity/UnifiedResponse';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-create-wallet',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-wallet.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class CreateWalletComponent {
  isLoading$: Observable<boolean>;
  errorMessage$: Observable<string>;
  successMessage$: Observable<string>;

  walletTypes: WalletType[];
  currencies: string[];

  formData: CreateWalletForm = {
    walletName: '',
    selectedWalletType: 'personal',
    setPrimaryWallet: false,
    currency: 'USD',
    description: ''
  };

  constructor(
    private createWalletService: CreateWalletService,
    private router: Router
  ) {
    this.isLoading$ = this.createWalletService.isLoading$();
    this.errorMessage$ = this.createWalletService.getErrorMessage();
    this.successMessage$ = this.createWalletService.getSuccessMessage();
    this.walletTypes = this.createWalletService.walletTypes;
    this.currencies = this.createWalletService.currencies;
  }

  createWallet(): void {
    this.createWalletService.createWallet(this.formData).subscribe(wallet => {
      if (wallet) {
        setTimeout(() => this.router.navigate(['/dashboard']), 1500);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  resetForm(): void {
    this.formData = {
      walletName: '',
      selectedWalletType: 'personal',
      setPrimaryWallet: false,
      currency: 'USD',
      description: ''
    };
  }
  
  togglePrimaryWallet(): void {
    this.formData.setPrimaryWallet = !this.formData.setPrimaryWallet;
  }
}
