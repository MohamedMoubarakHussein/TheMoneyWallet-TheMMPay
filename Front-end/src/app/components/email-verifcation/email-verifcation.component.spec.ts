import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailVerifcationComponent } from './email-verifcation.component';

describe('EmailVerifcationComponent', () => {
  let component: EmailVerifcationComponent;
  let fixture: ComponentFixture<EmailVerifcationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailVerifcationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmailVerifcationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
