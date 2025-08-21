import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HoverAnimationDirective } from './hover-animation/hover-animation.directive';
import { RippleDirective } from './ripple/ripple.directive';
import { ResponsiveDirective } from './responsive/responsive.directive';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HoverAnimationDirective,
    RippleDirective,
    ResponsiveDirective
  ],
  exports: [
    HoverAnimationDirective,
    RippleDirective,
    ResponsiveDirective
  ]
})
export class DirectivesModule { }
