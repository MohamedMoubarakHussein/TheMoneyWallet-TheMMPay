import { TestBed } from '@angular/core/testing';

import { PayBillService } from './pay-bill.service';

describe('PayBillService', () => {
  let service: PayBillService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PayBillService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
