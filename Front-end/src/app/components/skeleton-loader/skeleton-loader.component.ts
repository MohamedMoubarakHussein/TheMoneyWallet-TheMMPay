import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-skeleton-loader',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div 
      class="skeleton-loader"
      [ngClass]="{
        'rounded': rounded,
        'card': type === 'card',
        'text': type === 'text',
        'avatar': type === 'avatar',
        'button': type === 'button',
        'thumbnail': type === 'thumbnail',
        'custom': type === 'custom'
      }"
      [ngStyle]="{
        'width': width,
        'height': height,
        'border-radius': borderRadius,
        'margin-bottom': marginBottom,
        'animation-delay': animationDelay + 'ms'
      }">
      <div class="shimmer-effect"></div>
    </div>
  `,
  styles: [`
    .skeleton-loader {
      position: relative;
      overflow: hidden;
      background-color: rgba(203, 213, 225, 0.2);
      margin-bottom: 8px;
      width: 100%;
      height: 16px;
      border-radius: 4px;
    }

    .skeleton-loader.rounded {
      border-radius: 9999px;
    }

    .skeleton-loader.card {
      height: 200px;
    }

    .skeleton-loader.text {
      height: 16px;
    }

    .skeleton-loader.avatar {
      width: 50px;
      height: 50px;
      border-radius: 50%;
    }

    .skeleton-loader.button {
      height: 40px;
      border-radius: 6px;
    }

    .skeleton-loader.thumbnail {
      height: 100px;
    }

    .shimmer-effect {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        90deg,
        transparent 0%,
        rgba(255, 255, 255, 0.15) 50%,
        transparent 100%
      );
      animation: shimmer 1.5s infinite;
      transform: translateX(-100%);
    }

    @keyframes shimmer {
      100% {
        transform: translateX(100%);
      }
    }
  `]
})
export class SkeletonLoaderComponent {
  @Input() type: 'text' | 'card' | 'avatar' | 'button' | 'thumbnail' | 'custom' = 'text';
  @Input() width: string = '100%';
  @Input() height: string = '';
  @Input() rounded: boolean = false;
  @Input() borderRadius: string = '';
  @Input() marginBottom: string = '8px';
  @Input() animationDelay: number = 0;
}
