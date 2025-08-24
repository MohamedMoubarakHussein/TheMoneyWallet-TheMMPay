import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HomeService {

  constructor() { }

  animateCounters() {
    console.log('HomeService: animateCounters called');
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          console.log('HomeService: Stats section is intersecting');
          const counters = entry.target.querySelectorAll('.stat-number');
          console.log('HomeService: Found counters', counters);
          counters.forEach((element: HTMLElement) => {
            const target = parseInt(element.getAttribute('data-count'));
            console.log('HomeService: Animating counter', element, 'target', target);
            this.animateCounter(element, target);
          });
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.5 });

    const statsSection = document.querySelector('.stats-section');
    if (statsSection) {
      console.log('HomeService: Found stats section', statsSection);
      observer.observe(statsSection);
    } else {
      console.log('HomeService: Stats section not found');
    }
  }

  private animateCounter(element: HTMLElement, target: number) {
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