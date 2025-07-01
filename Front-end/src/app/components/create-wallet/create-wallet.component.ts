import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { User } from '../../entity/UnifiedResponse';

interface WalletType {
  id: string;
  name: string;
  description: string;
  icon: string;
  gradient: string;
  features: string[];
}

@Component({
  selector: 'app-create-wallet',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-wallet.component.html',
  styleUrls: ['./create-wallet.component.scss']
})
export class CreateWalletComponent implements OnInit, OnDestroy {

  user: User | null = null;
  walletName: string = '';
  selectedWalletType: string = '';
  setPrimaryWallet: boolean = false;
  currency: string = 'USD';
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  walletTypes: WalletType[] = [
    {
      id: 'personal',
      name: 'Personal Wallet',
      description: 'Perfect for everyday spending and personal finance management',
      icon: 'fas fa-wallet',
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      features: ['Daily transactions', 'Budget tracking', 'Expense categorization']
    },
    {
      id: 'business',
      name: 'Business Wallet',
      description: 'Designed for business operations and professional transactions',
      icon: 'fas fa-building',
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      features: ['Invoice management', 'Tax tracking', 'Business analytics']
    },
    {
      id: 'savings',
      name: 'Savings Wallet',
      description: 'Built for long-term savings goals and investment tracking',
      icon: 'fas fa-piggy-bank',
      gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      features: ['Goal setting', 'Interest tracking', 'Savings milestones']
    }
  ];

  currencies: string[] = ['USD', 'EUR', 'GBP', 'CAD', 'AUD', 'JPY', 'CHF'];

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.selectedWalletType = 'personal'; // Default selection
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
        // If user has no wallets, automatically set as primary
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

    const walletData = {
      name: this.walletName.trim(),
      type: this.selectedWalletType,
      currency: this.currency,
      isPrimary: this.setPrimaryWallet
    };

    // Simulate API call
    setTimeout(() => {
      this.successMessage = 'Wallet created successfully!';
      console.log('Creating wallet:', walletData);
      
      setTimeout(() => {
        this.router.navigate(['/dashboard']);
      }, 1500);
      
      this.isLoading = false;
    }, 2000);
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  resetForm(): void {
    this.walletName = '';
    this.selectedWalletType = 'personal';
    this.setPrimaryWallet = false;
    this.currency = 'USD';
    this.errorMessage = '';
    this.successMessage = '';
  }
}