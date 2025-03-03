// loading.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="loader-overlay">
  <div class="holographic-loader">
    <div class="security-badge">Secure</div>
    <div class="spinner"></div>
    <div class="text">Loading</div>
  </div>
</div>
  `,
  styleUrls: ['./loading.component.scss']
})
export class LoadingComponent {}