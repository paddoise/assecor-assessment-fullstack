import { Component, input, output } from '@angular/core';
import { Person } from './person.module';

@Component({
  selector: 'app-person',
  imports: [],
  templateUrl: './person.component.html',
  styleUrl: './person.component.scss'
})
export class PersonComponent {
  person = input.required<Person>();
  selected = output<Person>();

  gotSelected() {
    this.selected.emit(this.person());
  }
}
