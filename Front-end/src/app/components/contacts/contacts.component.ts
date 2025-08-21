import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Contact } from '../../entity/UnifiedResponse';
import { trigger, state, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.css'],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('scaleIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8)' }),
        animate('0.4s ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('bounceIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.3)' }),
        animate('0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55)', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('staggerIn', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(100, [
            animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ]),
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('0.3s ease-out', style({ opacity: 1 }))
      ])
    ])
  ]
})
export class ContactsComponent implements OnInit, OnDestroy {
  contacts: Contact[] = [];
  isLoading = false;
  showAddForm = false;
  selectedContact: Contact | null = null;
  
  contactForm: FormGroup;
  searchQuery = '';
  currentPage = 1;
  itemsPerPage = 20;
  
  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {
    this.contactForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.email]],
      phone: [''],
      isFavorite: [false]
    });
  }

  ngOnInit(): void {
    this.loadContacts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadContacts(): void {
    this.isLoading = true;
    // Mock data for now - replace with actual service call
    this.contacts = [
      {
        id: '1',
        userId: 'user1',
        name: 'John Doe',
        email: 'john@example.com',
        phone: '+1234567890',
        avatar: 'assets/avatar1.png',
        isFavorite: true,
        lastTransactionDate: new Date('2024-01-15'),
        totalTransactions: 5,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-15')
      },
      {
        id: '2',
        userId: 'user1',
        name: 'Jane Smith',
        email: 'jane@example.com',
        phone: '+1234567891',
        avatar: 'assets/avatar2.png',
        isFavorite: false,
        lastTransactionDate: new Date('2024-01-10'),
        totalTransactions: 3,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-01-10')
      }
    ];
    this.isLoading = false;
  }

  addContact(): void {
    if (this.contactForm.valid) {
      const formValue = this.contactForm.value;
      const newContact: Contact = {
        id: Date.now().toString(),
        userId: 'user1', // Replace with actual user ID
        name: formValue.name,
        email: formValue.email,
        phone: formValue.phone,
        avatar: '',
        isFavorite: formValue.isFavorite,
        lastTransactionDate: undefined,
        totalTransactions: 0,
        createdAt: new Date(),
        updatedAt: new Date()
      };

      this.contacts.unshift(newContact);
      this.showAddForm = false;
      this.contactForm.reset();
    }
  }

  selectContact(contact: Contact): void {
    this.selectedContact = contact;
  }

  editContact(contact: Contact): void {
    this.selectedContact = contact;
    this.contactForm.patchValue({
      name: contact.name,
      email: contact.email,
      phone: contact.phone,
      isFavorite: contact.isFavorite
    });
    this.showAddForm = true;
  }

  updateContact(): void {
    if (this.contactForm.valid && this.selectedContact) {
      const formValue = this.contactForm.value;
      const index = this.contacts.findIndex(c => c.id === this.selectedContact?.id);
      
      if (index !== -1) {
        this.contacts[index] = {
          ...this.contacts[index],
          name: formValue.name,
          email: formValue.email,
          phone: formValue.phone,
          isFavorite: formValue.isFavorite,
          updatedAt: new Date()
        };
      }
      
      this.showAddForm = false;
      this.selectedContact = null;
      this.contactForm.reset();
    }
  }

  deleteContact(contactId: string): void {
    if (confirm('Are you sure you want to delete this contact?')) {
      this.contacts = this.contacts.filter(c => c.id !== contactId);
      if (this.selectedContact?.id === contactId) {
        this.selectedContact = null;
      }
    }
  }

  toggleFavorite(contact: Contact): void {
    const index = this.contacts.findIndex(c => c.id === contact.id);
    if (index !== -1) {
      this.contacts[index] = {
        ...this.contacts[index],
        isFavorite: !this.contacts[index].isFavorite,
        updatedAt: new Date()
      };
    }
  }

  sendMoneyToContact(contact: Contact): void {
    // Navigate to send money with contact pre-filled
    console.log('Send money to:', contact.name);
  }

  get filteredContacts(): Contact[] {
    let filtered = this.contacts;
    
    if (this.searchQuery) {
      filtered = filtered.filter(contact => 
        contact.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        contact.email?.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        contact.phone?.includes(this.searchQuery)
      );
    }
    
    return filtered;
  }

  get paginatedContacts(): Contact[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredContacts.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredContacts.length / this.itemsPerPage);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  getFavoriteContacts(): Contact[] {
    return this.contacts.filter(c => c.isFavorite);
  }

  getRecentContacts(): Contact[] {
    return this.contacts
      .filter(c => c.lastTransactionDate)
      .sort((a, b) => new Date(b.lastTransactionDate!).getTime() - new Date(a.lastTransactionDate!).getTime())
      .slice(0, 5);
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.contactForm.reset();
      this.selectedContact = null;
    }
  }

  resetForm(): void {
    this.contactForm.reset();
    this.showAddForm = false;
    this.selectedContact = null;
  }

  getContactInitials(contact: Contact): string {
    return contact.name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }

  getContactAvatar(contact: Contact): string {
    if (contact.avatar) {
      return contact.avatar;
    }
    return `data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="35" r="25" fill="%23ccc"/><circle cx="50" cy="100" r="40" fill="%23ccc"/></svg>`;
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    if (target) {
      target.style.display = 'none';
    }
  }
} 