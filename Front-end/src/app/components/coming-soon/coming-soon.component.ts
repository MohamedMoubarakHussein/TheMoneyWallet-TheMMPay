import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-coming-soon',
  imports: [MatIconModule,CommonModule],
  standalone: true,
  templateUrl: './coming-soon.component.html',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ])
    ]),
    trigger('scaleIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.9)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ])
  ]
})
export class ComingSoonComponent {
  @Output() closeModal = new EventEmitter<void>();

  onClose() {
    this.closeModal.emit();
  }

  @HostListener('document:keydown.escape')
  handleEscape(): void {
    this.onClose();
  }
}