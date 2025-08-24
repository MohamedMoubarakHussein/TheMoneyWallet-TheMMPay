import { TestBed } from '@angular/core/testing';

import { AnimatedCounterService } from './animated-counter.service';

describe('AnimatedCounterService', () => {
  let service: AnimatedCounterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnimatedCounterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
