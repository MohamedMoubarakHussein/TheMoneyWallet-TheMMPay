import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { of, Subject } from 'rxjs';
import { takeUntil, switchMap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { WalletService } from '../../services/wallet/wallet-service.service';
import { CreateWalletRequest, CURRENCIES, CURRENTWALLETTYPES, User, WalletType } from '../../entity/UnifiedResponse';

@Component({
  selector: 'app-create-wallet',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-wallet.component.html',

})
export class CreateWalletComponent implements OnInit, OnDestroy {
  
  user: User | null = null;
  walletName: string = '';
  selectedWalletType: string = '';
  setPrimaryWallet: boolean = false;
  currency: string = 'USD';
  initialBalance: number = 0;
  description: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  walletTypes: WalletType[] = CURRENTWALLETTYPES;
  currencies: string[] = CURRENCIES;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private walletService: WalletService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.selectedWalletType = 'personal';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadUserData(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.user = user;
        if (user && (!user.wallets || user.wallets.length === 0)) {
          this.setPrimaryWallet = true;
        }
      });
  }

  selectWalletType(typeId: string): void {
    this.selectedWalletType = typeId;
    this.errorMessage = '';
  }

  getSelectedWalletType(): WalletType | undefined {
    return this.walletTypes.find(type => type.id === this.selectedWalletType);
  }

  togglePrimaryWallet(): void {
    this.setPrimaryWallet = !this.setPrimaryWallet;
  }

  createWallet(): void {
   
    if (!this.walletName.trim()) {
      this.errorMessage = 'Please enter a wallet name';
      return;
    }

    if (!this.selectedWalletType) {
      this.errorMessage = 'Please select a wallet type';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const walletData: CreateWalletRequest = {
      name: this.walletName.trim(),
      walletType: this.selectedWalletType as 'Personal' | 'Business' | 'Savings' ,
      currency: this.currency,
      description: this.description?.trim() || undefined
    };

      // TODO adding the primary wallet funcationallity 
    this.walletService.createWallet(walletData)
  .pipe(
    switchMap(createdWallet => {
     /* if (this.setPrimaryWallet) {
        return this.walletService.updatePrimaryWallet(createdWallet.id)
          .pipe(
         
            switchMap(() => of(createdWallet)) 
          );
      } else {
        
        return of(createdWallet);
      }*/
      return of(createdWallet);
    }),
    takeUntil(this.destroy$)
  )
  .subscribe({
    next: (wallet) => {
      this.successMessage = `Wallet "${wallet.name}" created successfully!`;
      console.log('Wallet created:', wallet);
      
      setTimeout(() => {
        this.router.navigate(['/dashboard']);
      }, 1500);
      
      this.isLoading = false;
    },
    error: (error) => {
      this.errorMessage = error.message || 'Failed to create wallet. Please try again.';
      this.isLoading = false;
      console.error('Error creating wallet:', error);
    }
  });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  resetForm(): void {
    this.walletName = '';
    this.selectedWalletType = 'personal';
    this.setPrimaryWallet = false;
    this.currency = 'USD';
    this.initialBalance = 0;
    this.description = '';
    this.errorMessage = '';
    this.successMessage = '';
  }
}