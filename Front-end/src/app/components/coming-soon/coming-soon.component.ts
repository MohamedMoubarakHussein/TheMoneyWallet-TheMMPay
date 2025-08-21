import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
@Component({
  selector: 'app-coming-soon',
  imports: [MatIconModule,CommonModule],
  templateUrl: './coming-soon.component.html',
  styleUrl: './coming-soon.component.css'
})
export class ComingSoonComponent {
  @Output() close = new EventEmitter<void>();  // Changed output name to 'close'

  onClose() {
    this.close.emit();  // Emit the 'close' event
  }

  @HostListener('document:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent) {
    this.onClose();
  }


}
