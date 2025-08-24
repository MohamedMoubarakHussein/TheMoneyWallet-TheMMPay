import { TestBed } from '@angular/core/testing';

import { BudgetManagementService } from './budget-management.service';

describe('BudgetManagementService', () => {
  let service: BudgetManagementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BudgetManagementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
