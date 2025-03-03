import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterOutlet } from '@angular/router';
import { LoadingComponent } from './components/loading/loading.component';
import { filter, timer } from 'rxjs';
import { LoadingService } from './services/loading-service.service';
import { CommonModule } from '@angular/common'; // Add this

@Component({
  selector: 'app-root',
  imports: [RouterOutlet , LoadingComponent,CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})

export class AppComponent implements OnInit {
  isLoading = false;

  constructor(private router: Router, private loadingService: LoadingService) {}

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationStart || event instanceof NavigationEnd)
    ).subscribe(event => {
      if (event instanceof NavigationStart) {
        this.loadingService.show();
        this.isLoading = true;
      } else if (event instanceof NavigationEnd) {
        // Ensure loader shows for at least 2 seconds
        timer(2000).subscribe(() => {
          this.loadingService.hide();
          this.isLoading = false;
        });
      }
    });
  }
}