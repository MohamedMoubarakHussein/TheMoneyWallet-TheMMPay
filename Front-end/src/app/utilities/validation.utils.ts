import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class AuthValidators {
  static passwordMatch(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    
    if (password?.value !== confirmPassword?.value) {
      confirmPassword?.setErrors({ mismatch: true });
      return { passwordMismatch: true };
    }
    
    if (confirmPassword?.errors?.['mismatch']) {
      delete confirmPassword.errors['mismatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    
    return null;
  }

  static capitalLetter(control: AbstractControl): ValidationErrors | null {
    return /[A-Z]/.test(control.value) ? null : { capitalLetter: true };
  }

  static lowercaseLetter(control: AbstractControl): ValidationErrors | null {
    return /[a-z]/.test(control.value) ? null : { lowercaseLetter: true };
  }

  static number(control: AbstractControl): ValidationErrors | null {
    return /\d/.test(control.value) ? null : { number: true };
  }

  static specialCharacter(control: AbstractControl): ValidationErrors | null {
    return /[!@#$%^&*(),.?":{}|<>]/.test(control.value) ? null : { specialCharacter: true };
  }

  static minLength(min: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return control.value && control.value.length >= min ? null : { minLength: { requiredLength: min, actualLength: control.value?.length } };
    };
  }

  static maxLength(max: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return control.value && control.value.length <= max ? null : { maxLength: { requiredLength: max, actualLength: control.value?.length } };
    };
  }

  static phoneNumber(control: AbstractControl): ValidationErrors | null {
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    return phoneRegex.test(control.value) ? null : { invalidPhone: true };
  }

  static email(control: AbstractControl): ValidationErrors | null {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(control.value) ? null : { invalidEmail: true };
  }

  static amount(control: AbstractControl): ValidationErrors | null {
    const amount = parseFloat(control.value);
    if (isNaN(amount) || amount <= 0) {
      return { invalidAmount: true };
    }
    
    // Check for maximum 2 decimal places
    const decimalPlaces = control.value.toString().split('.')[1]?.length || 0;
    if (decimalPlaces > 2) {
      return { tooManyDecimals: true };
    }
    
    return null;
  }

  static positiveNumber(control: AbstractControl): ValidationErrors | null {
    const num = parseFloat(control.value);
    return num > 0 ? null : { notPositive: true };
  }

  static nonNegativeNumber(control: AbstractControl): ValidationErrors | null {
    const num = parseFloat(control.value);
    return num >= 0 ? null : { negative: true };
  }

  static currencyCode(control: AbstractControl): ValidationErrors | null {
    const validCurrencies = ['USD', 'EUR', 'GBP', 'CAD', 'AUD', 'JPY', 'CHF', 'CNY', 'INR', 'BRL'];
    return validCurrencies.includes(control.value) ? null : { invalidCurrency: true };
  }

  static dateNotInPast(control: AbstractControl): ValidationErrors | null {
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return selectedDate >= today ? null : { pastDate: true };
  }

  static dateNotTooFar(control: AbstractControl): ValidationErrors | null {
    const selectedDate = new Date(control.value);
    const maxDate = new Date();
    maxDate.setFullYear(maxDate.getFullYear() + 10); // Max 10 years in future
    
    return selectedDate <= maxDate ? null : { tooFarInFuture: true };
  }

  static iban(control: AbstractControl): ValidationErrors | null {
    // Basic IBAN validation (simplified)
    const ibanRegex = /^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$/;
    return ibanRegex.test(control.value.replace(/\s/g, '')) ? null : { invalidIBAN: true };
  }

  static swiftCode(control: AbstractControl): ValidationErrors | null {
    const swiftRegex = /^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$/;
    return swiftRegex.test(control.value) ? null : { invalidSwift: true };
  }

  static accountNumber(control: AbstractControl): ValidationErrors | null {
    const accountRegex = /^[0-9]{8,17}$/;
    return accountRegex.test(control.value.replace(/\s/g, '')) ? null : { invalidAccountNumber: true };
  }

  static routingNumber(control: AbstractControl): ValidationErrors | null {
    const routingRegex = /^[0-9]{9}$/;
    return routingRegex.test(control.value) ? null : { invalidRoutingNumber: true };
  }

  static cardNumber(control: AbstractControl): ValidationErrors | null {
    const cardRegex = /^[0-9]{13,19}$/;
    if (!cardRegex.test(control.value.replace(/\s/g, ''))) {
      return { invalidCardNumber: true };
    }
    
    // Luhn algorithm check
    if (!this.luhnCheck(control.value.replace(/\s/g, ''))) {
      return { invalidCardNumber: true };
    }
    
    return null;
  }

  static cardExpiry(control: AbstractControl): ValidationErrors | null {
    const expiryRegex = /^(0[1-9]|1[0-2])\/([0-9]{2})$/;
    if (!expiryRegex.test(control.value)) {
      return { invalidExpiry: true };
    }
    
    const [month, year] = control.value.split('/');
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear() % 100;
    const currentMonth = currentDate.getMonth() + 1;
    
    if (parseInt(year) < currentYear || (parseInt(year) === currentYear && parseInt(month) < currentMonth)) {
      return { expired: true };
    }
    
    return null;
  }

  static cardCVC(control: AbstractControl): ValidationErrors | null {
    const cvcRegex = /^[0-9]{3,4}$/;
    return cvcRegex.test(control.value) ? null : { invalidCVC: true };
  }

  static zipCode(control: AbstractControl): ValidationErrors | null {
    const zipRegex = /^[0-9]{5}(-[0-9]{4})?$/;
    return zipRegex.test(control.value) ? null : { invalidZipCode: true };
  }

  static ssn(control: AbstractControl): ValidationErrors | null {
    const ssnRegex = /^[0-9]{3}-[0-9]{2}-[0-9]{4}$/;
    return ssnRegex.test(control.value) ? null : { invalidSSN: true };
  }

  static ein(control: AbstractControl): ValidationErrors | null {
    const einRegex = /^[0-9]{2}-[0-9]{7}$/;
    return einRegex.test(control.value) ? null : { invalidEIN: true };
  }

  static url(control: AbstractControl): ValidationErrors | null {
    try {
      new URL(control.value);
      return null;
    } catch {
      return { invalidURL: true };
    }
  }

  static alphanumeric(control: AbstractControl): ValidationErrors | null {
    const alphanumericRegex = /^[a-zA-Z0-9]+$/;
    return alphanumericRegex.test(control.value) ? null : { notAlphanumeric: true };
  }

  static noSpecialChars(control: AbstractControl): ValidationErrors | null {
    const noSpecialRegex = /^[a-zA-Z0-9\s]+$/;
    return noSpecialRegex.test(control.value) ? null : { containsSpecialChars: true };
  }

  static passwordStrength(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value) return null;
    
    const errors: ValidationErrors = {};
    
    if (value.length < 8) errors['tooShort'] = true;
    if (!/[A-Z]/.test(value)) errors['noUppercase'] = true;
    if (!/[a-z]/.test(value)) errors['noLowercase'] = true;
    if (!/\d/.test(value)) errors['noNumber'] = true;
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(value)) errors['noSpecialChar'] = true;
    
    return Object.keys(errors).length > 0 ? errors : null;
  }

  static getPasswordStrengthScore(password: string): number {
    let score = 0;
    
    if (password.length >= 8) score += 1;
    if (password.length >= 12) score += 1;
    if (/[A-Z]/.test(password)) score += 1;
    if (/[a-z]/.test(password)) score += 1;
    if (/\d/.test(password)) score += 1;
    if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score += 1;
    if (password.length > 8 && /[A-Z]/.test(password) && /[a-z]/.test(password) && /\d/.test(password)) score += 1;
    
    return Math.min(score, 7);
  }

  static getPasswordStrengthLabel(score: number): string {
    if (score <= 2) return 'Weak';
    if (score <= 4) return 'Fair';
    if (score <= 6) return 'Good';
    return 'Strong';
  }

  static getPasswordStrengthColor(score: number): string {
    if (score <= 2) return '#f44336';
    if (score <= 4) return '#ff9800';
    if (score <= 6) return '#ffc107';
    return '#4caf50';
  }

  // Luhn algorithm for credit card validation
  private static luhnCheck(cardNumber: string): boolean {
    let sum = 0;
    let isEven = false;
    
    for (let i = cardNumber.length - 1; i >= 0; i--) {
      let digit = parseInt(cardNumber.charAt(i));
      
      if (isEven) {
        digit *= 2;
        if (digit > 9) {
          digit -= 9;
        }
      }
      
      sum += digit;
      isEven = !isEven;
    }
    
    return sum % 10 === 0;
  }

  // Custom validators for specific use cases
  static conditionalRequired(condition: () => boolean): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (condition() && (!control.value || control.value.trim() === '')) {
        return { required: true };
      }
      return null;
    };
  }

  static uniqueValue(existingValues: string[], caseSensitive: boolean = false): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      
      const value = caseSensitive ? control.value : control.value.toLowerCase();
      const existing = caseSensitive ? existingValues : existingValues.map(v => v.toLowerCase());
      
      return existing.includes(value) ? { duplicate: true } : null;
    };
  }

  static futureDate(control: AbstractControl): ValidationErrors | null {
    const selectedDate = new Date(control.value);
    const today = new Date();
    today.setHours(23, 59, 59, 999);
    
    return selectedDate > today ? null : { notFutureDate: true };
  }

  static age(minAge: number, maxAge?: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      
      const birthDate = new Date(control.value);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      
      if (age < minAge) {
        return { tooYoung: { requiredAge: minAge, actualAge: age } };
      }
      
      if (maxAge && age > maxAge) {
        return { tooOld: { requiredAge: maxAge, actualAge: age } };
      }
      
      return null;
    };
  }
}