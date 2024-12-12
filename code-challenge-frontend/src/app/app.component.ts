import { Component, output, signal } from '@angular/core';

import { PersonsComponent } from "./persons/persons.component";
import { DetailsComponent } from "./details/details.component";
import { Person } from './persons/person/person.module';
import { NotificationComponent } from "./notification/notification/notification.component";

@Component({
  selector: 'app-root',
  imports: [PersonsComponent, DetailsComponent, NotificationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  currentPerson = signal<Person|undefined>(undefined);

  setCurrentPerson(person: Person): void {
    this.currentPerson.set(person);
  }
}
