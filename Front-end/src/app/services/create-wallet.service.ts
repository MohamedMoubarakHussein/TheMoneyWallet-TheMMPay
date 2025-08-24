import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { WalletService } from './wallet/wallet-service.service';
import { CreateWalletRequest, CURRENCIES, CURRENTWALLETTYPES, WalletType } from '../entity/UnifiedResponse';

export interface CreateWalletForm {
  walletName: string;
  selectedWalletType: string;
  setPrimaryWallet: boolean;
  currency: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class CreateWalletService {
  private isLoading = new BehaviorSubject<boolean>(false);
  private errorMessage = new BehaviorSubject<string>('');
  private successMessage = new BehaviorSubject<string>('');

  walletTypes: WalletType[] = CURRENTWALLETTYPES;
  currencies: string[] = CURRENCIES;

  constructor(private walletService: WalletService) { }

  isLoading$(): Observable<boolean> { return this.isLoading.asObservable(); }
  getErrorMessage(): Observable<string> { return this.errorMessage.asObservable(); }
  getSuccessMessage(): Observable<string> { return this.successMessage.asObservable(); }

  createWallet(formData: CreateWalletForm): Observable<UserWallet> {
    if (!formData.walletName.trim()) {
      this.errorMessage.next('Please enter a wallet name');
      return of(null);
    }
    if (!formData.selectedWalletType) {
      this.errorMessage.next('Please select a wallet type');
      return of(null);
    }

    this.isLoading.next(true);
    this.errorMessage.next('');
    this.successMessage.next('');

    const walletData: CreateWalletRequest = {
      name: formData.walletName.trim(),
      walletType: formData.selectedWalletType as 'Personal' | 'Business' | 'Savings',
      currency: formData.currency,
      description: formData.description?.trim() || undefined
    };

    return this.walletService.createWallet(walletData).pipe(
      tap(wallet => {
        this.successMessage.next(`Wallet "${wallet.name}" created successfully!`);
        this.isLoading.next(false);
      }),
      // TODO: Handle primary wallet logic
    );
  }
}