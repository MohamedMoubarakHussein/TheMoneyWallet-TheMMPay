import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { EditProfileService } from '../../services/edit-profile.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-profile.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
      ]),
    ]),
  ],
})
export class EditProfileComponent implements OnInit {
  profileForm: FormGroup;
  loading$: Observable<boolean>;
  profileImage: string | ArrayBuffer | null = null;

  constructor(private fb: FormBuilder, private editProfileService: EditProfileService) {
    this.profileForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      currency: ['USD'],
      language: ['en'],
      newsletter: [true],
      twoFactorAuth: [false],
      password: [''],
      confirmPassword: ['']
    });
    this.loading$ = this.editProfileService.isLoading();
  }

  ngOnInit(): void {
    this.editProfileService.getUserProfile().subscribe(profile => {
      if (profile) {
        this.profileForm.patchValue(profile);
        this.profileImage = profile.profileImage || 'assets/default-avatar.png';
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const reader = new FileReader();
      reader.onload = () => {
        this.profileImage = reader.result;
      };
      reader.readAsDataURL(input.files[0]);
    }
  }

  onSubmit(): void {
    if (this.profileForm.valid) {
      const updatedProfile = { ...this.profileForm.value, profileImage: this.profileImage };
      this.editProfileService.updateUserProfile(updatedProfile).subscribe();
    }
  }
}
