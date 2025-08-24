import { TestBed } from '@angular/core/testing';

import { NotificationAnimationService } from './notification-animation.service';

describe('NotificationAnimationService', () => {
  let service: NotificationAnimationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationAnimationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
