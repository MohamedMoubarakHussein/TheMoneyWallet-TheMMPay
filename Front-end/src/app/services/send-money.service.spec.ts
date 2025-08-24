import { TestBed } from '@angular/core/testing';

import { SendMoneyService } from './send-money.service';

describe('SendMoneyService', () => {
  let service: SendMoneyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SendMoneyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
