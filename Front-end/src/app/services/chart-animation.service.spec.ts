import { TestBed } from '@angular/core/testing';

import { ChartAnimationService } from './chart-animation.service';

describe('ChartAnimationService', () => {
  let service: ChartAnimationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChartAnimationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
