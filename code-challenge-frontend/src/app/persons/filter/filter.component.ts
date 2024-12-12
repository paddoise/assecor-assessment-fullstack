import { Component, output, signal } from '@angular/core';
import { FormsModule } from "@angular/forms";

import { Filter } from './filter.module';
import { ColorDropDownComponent } from "../../color-drop-down/color-drop-down.component";
import { NotificationService } from '../../notification/notification.service';
import { FilterService } from './filter.service';

@Component({
  selector: 'app-filter',
  imports: [FormsModule, ColorDropDownComponent],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.scss'
})
export class FilterComponent {
  emitFilter = output<Filter>();
  
  enteredField: string = '';
  enteredTerm: string = '';
  inputType: string = 'text';

  constructor(
    private notificationService: NotificationService,
    private filterService: FilterService
  ) { }

  ngOnInit(): void {
    this.filterService.applyFilter$.subscribe(() => {
      this.applyFilter();
    });
  }

  setTerm(term: string): void {
    this.enteredTerm = term;
  }

  applyFilter() {
    const filter: Filter = {
      field: this.enteredField,
      term: this.enteredTerm
    }

    if (filter.term === '' && filter.field !== '') {
      this.notificationService.show('Please enter a term');
      return;
    }

    this.emitFilter.emit(filter);
  }

  onSelect(): void {
    if (this.enteredField === 'id') {
      this.inputType = 'number';
    } else {
      this.inputType = 'text';
    }

    this.enteredTerm = '';
  }
}
