import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'asStringArray',
  standalone: true
})
export class AsStringArrayPipe implements PipeTransform {
  transform(value: unknown): string[] {
    return value as string[];
  }
}
