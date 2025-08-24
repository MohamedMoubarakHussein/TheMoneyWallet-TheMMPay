import { TestBed } from '@angular/core/testing';

import { CreateWalletService } from './create-wallet.service';

describe('CreateWalletService', () => {
  let service: CreateWalletService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CreateWalletService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
