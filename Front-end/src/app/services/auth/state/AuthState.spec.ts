import { TestBed } from '@angular/core/testing';

import { AuthStateService } from './AuthState.service';

describe('StateService', () => {
  let service: AuthStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
