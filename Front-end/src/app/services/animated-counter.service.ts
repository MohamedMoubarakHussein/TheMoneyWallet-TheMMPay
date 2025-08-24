import { Injectable } from '@angular/core';
import { Observable, animationFrameScheduler, interval } from 'rxjs';
import { map, takeWhile } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AnimatedCounterService {

  constructor() { }

  animate(start: number, end: number, duration: number): Observable<number> {
    const startTime = animationFrameScheduler.now();
    return interval(0, animationFrameScheduler).pipe(
      map(() => (animationFrameScheduler.now() - startTime) / duration),
      takeWhile(t => t <= 1),
      map(t => start + t * (end - start))
    );
  }
}