import { TestBed } from '@angular/core/testing';

import { RecurringPaymentsManagementService } from './recurring-payments-management.service';

describe('RecurringPaymentsManagementService', () => {
  let service: RecurringPaymentsManagementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecurringPaymentsManagementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
