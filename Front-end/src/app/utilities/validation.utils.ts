import { AbstractControl } from '@angular/forms';

export class AuthValidators {
  static passwordMatch(control: AbstractControl) {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    
    if (password?.value !== confirmPassword?.value) {
      confirmPassword?.setErrors({ mismatch: true });
    }
    return null;
  }

  static capitalLetter(control: AbstractControl) {
    return /[A-Z]/.test(control.value) ? null : { capitalLetter: true };
  }
}