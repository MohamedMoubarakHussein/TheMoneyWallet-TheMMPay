import { TestBed } from '@angular/core/testing';

import { ButtonAnimationService } from './button-animation.service';

describe('ButtonAnimationService', () => {
  let service: ButtonAnimationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ButtonAnimationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
