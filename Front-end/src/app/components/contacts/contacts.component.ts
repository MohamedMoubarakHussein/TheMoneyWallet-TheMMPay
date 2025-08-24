import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Contact } from '../../entity/UnifiedResponse';
import { ContactsService } from '../../services/contacts.service';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './contacts.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-20px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('staggerIn', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(100, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ])
  ]
})
export class ContactsComponent {
  contacts$: Observable<Contact[]>;
  paginatedContacts$: Observable<Contact[]>;
  isLoading$: Observable<boolean>;
  totalPages$: Observable<number>;
  
  showAddForm = false;
  selectedContact: Contact | null = null;
  contactForm: FormGroup;
  searchQuery = '';
  currentPage = 1;

  constructor(private contactsService: ContactsService, private fb: FormBuilder) {
    this.contacts$ = this.contactsService.getContacts();
    this.paginatedContacts$ = this.contactsService.getPaginatedContacts();
    this.isLoading$ = this.contactsService.isLoading.asObservable();
    this.totalPages$ = this.contactsService.getTotalPages();

    this.contactForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.email]],
      phone: [''],
      isFavorite: [false]
    });
  }

  onSearchQueryChanged() {
    this.contactsService.setSearchQuery(this.searchQuery);
  }

  addContact(): void {
    if (this.contactForm.valid) {
      this.contactsService.addContact(this.contactForm.value);
      this.resetForm();
    }
  }

  editContact(contact: Contact): void {
    this.selectedContact = contact;
    this.contactForm.patchValue(contact);
    this.showAddForm = true;
  }

  updateContact(): void {
    if (this.contactForm.valid && this.selectedContact) {
      const updatedContact = { ...this.selectedContact, ...this.contactForm.value };
      this.contactsService.updateContact(updatedContact);
      this.resetForm();
    }
  }

  deleteContact(contactId: string): void {
    if (confirm('Are you sure you want to delete this contact?')) {
      this.contactsService.deleteContact(contactId);
    }
  }

  toggleFavorite(contact: Contact): void {
    this.contactsService.toggleFavorite(contact);
  }

  nextPage(): void {
    this.contactsService.nextPage();
    this.currentPage++;
  }

  previousPage(): void {
    this.contactsService.previousPage();
    this.currentPage--;
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.resetForm();
    }
  }

  resetForm(): void {
    this.contactForm.reset();
    this.selectedContact = null;
    this.showAddForm = false;
  }
  
  getContactInitials(contact: Contact): string {
    return contact.name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }
}