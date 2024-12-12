import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FilterService {
  private applyFilterSubject = new Subject<void>();
  applyFilter$ = this.applyFilterSubject.asObservable();

  constructor() { }

  triggerApplyFilter(): void {
    this.applyFilterSubject.next();
  }
}
