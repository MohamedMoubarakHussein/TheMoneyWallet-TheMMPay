import { Component, ChangeDetectionStrategy, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { Router } from '@angular/router';
import { FooterComponent } from '../footer/footer.component';

declare var AOS: any;

@Component({
  selector: 'app-home',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {
  constructor(private router: Router) {}

  ngOnInit() {
    // Initialize AOS with enhanced configuration
    if (typeof AOS !== 'undefined') {
      AOS.init({
        duration: 1000,
        easing: 'ease-out-cubic',
        once: false,
        mirror: true,
        anchorPlacement: 'top-center',
        disable: 'mobile',  // Disable on mobile for better performance
        startEvent: 'DOMContentLoaded'
      });
    }
  }

  ngAfterViewInit() {
    // Start counter animations when stats section comes into view
    this.animateCounters();
  }

  goToSingUp() {
    this.router.navigate(['/signup']);
  }

  private animateCounters() {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const counters = entry.target.querySelectorAll('.stat-number');
          counters.forEach((element: any) => {
            const target = parseInt(element.getAttribute('data-count'));
            this.animateCounter(element, target);
          });
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.5 });

    const statsSection = document.querySelector('.stats-section');
    if (statsSection) {
      observer.observe(statsSection);
    }
  }

  private animateCounter(element: any, target: number) {
    let current = 0;
    const increment = target / 100;
    const timer = setInterval(() => {
      current += increment;
      if (current >= target) {
        current = target;
        clearInterval(timer);
      }
      element.textContent = Math.floor(current);
    }, 20);
  }
}
