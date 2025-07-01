import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecentTransactions } from './recentTransactions.component';

describe('LasttranscationComponent', () => {
  let component: RecentTransactions;
  let fixture: ComponentFixture<RecentTransactions>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecentTransactions]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecentTransactions);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
