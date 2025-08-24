import { TestBed } from '@angular/core/testing';

import { RequestMoneyService } from './request-money.service';

describe('RequestMoneyService', () => {
  let service: RequestMoneyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RequestMoneyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
