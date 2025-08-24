import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { Contact } from '../entity/UnifiedResponse';

@Injectable({
  providedIn: 'root'
})
export class ContactsService {
  private contacts = new BehaviorSubject<Contact[]>([]);
  public isLoading = new BehaviorSubject<boolean>(false);
  private searchQuery = new BehaviorSubject<string>('');
  private currentPage = new BehaviorSubject<number>(1);
  itemsPerPage = 20;

  constructor() {
    this.loadContacts();
  }

  getContacts(): Observable<Contact[]> { return this.contacts.asObservable(); }
  
  getFilteredContacts(): Observable<Contact[]> {
    return this.contacts.pipe(
      map(contacts => 
        contacts.filter(contact => 
          contact.name.toLowerCase().includes(this.searchQuery.value.toLowerCase())
        )
      )
    );
  }

  getPaginatedContacts(): Observable<Contact[]> {
    return this.getFilteredContacts().pipe(
      map(contacts => {
        const startIndex = (this.currentPage.value - 1) * this.itemsPerPage;
        return contacts.slice(startIndex, startIndex + this.itemsPerPage);
      })
    );
  }

  getTotalPages(): Observable<number> {
    return this.getFilteredContacts().pipe(
      map(contacts => Math.ceil(contacts.length / this.itemsPerPage))
    );
  }

  loadContacts() {
    this.isLoading.next(true);
    of([
      { id: '1', userId: 'user1', name: 'John Doe', email: 'john@example.com', phone: '+1234567890', avatar: 'assets/avatar1.png', isFavorite: true, lastTransactionDate: new Date('2024-01-15'), totalTransactions: 5, createdAt: new Date('2024-01-01'), updatedAt: new Date('2024-01-15') },
      { id: '2', userId: 'user1', name: 'Jane Smith', email: 'jane@example.com', phone: '+1234567891', avatar: 'assets/avatar2.png', isFavorite: false, lastTransactionDate: new Date('2024-01-10'), totalTransactions: 3, createdAt: new Date('2024-01-01'), updatedAt: new Date('2024-01-10') }
    ]).pipe(delay(1000)).subscribe(contacts => {
      this.contacts.next(contacts);
      this.isLoading.next(false);
    });
  }

  addContact(contact: Omit<Contact, 'id' | 'userId' | 'createdAt' | 'updatedAt' | 'lastTransactionDate' | 'totalTransactions'>): void {
    const newContact: Contact = {
      id: Date.now().toString(),
      userId: 'user1',
      ...contact,
      lastTransactionDate: undefined,
      totalTransactions: 0,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    this.contacts.next([newContact, ...this.contacts.value]);
  }

  updateContact(updatedContact: Contact): void {
    const currentContacts = this.contacts.value;
    const index = currentContacts.findIndex(c => c.id === updatedContact.id);
    if (index !== -1) {
      currentContacts[index] = { ...updatedContact, updatedAt: new Date() };
      this.contacts.next([...currentContacts]);
    }
  }

  deleteContact(contactId: string): void {
    this.contacts.next(this.contacts.value.filter(c => c.id !== contactId));
  }

  toggleFavorite(contact: Contact): void {
    this.updateContact({ ...contact, isFavorite: !contact.isFavorite });
  }

  setSearchQuery(query: string) {
    this.searchQuery.next(query);
  }

  nextPage() {
    this.currentPage.next(this.currentPage.value + 1);
  }

  previousPage() {
    this.currentPage.next(this.currentPage.value - 1);
  }
}