import { Component, input, output, signal } from '@angular/core';
import { OperationsComponent } from "./operations/operations.component";
import { FormsModule } from "@angular/forms";

import { Person } from '../persons/person/person.module';
import { HttpClientService } from '../http-client/http-client.service';
import { ColorDropDownComponent } from "../color-drop-down/color-drop-down.component";
import { NotificationService } from '../notification/notification.service';
import { FilterService } from '../persons/filter/filter.service';

@Component({
  selector: 'app-details',
  imports: [OperationsComponent, FormsModule, ColorDropDownComponent],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss'
})
export class DetailsComponent {
  private httpClientService: HttpClientService; 
  isAddPerson = signal<boolean>(false);
  currentPerson = input<Person>();

  postName = '';
  postLastname = '';
  postZipcode = '';
  postCity = '';
  postColor = '';

  constructor(
    httpClientService: HttpClientService,
    private notificationService: NotificationService,
    private filterService: FilterService
  ) {
    this.httpClientService = httpClientService;  
  }

  setIsAddPerson(isAddPersonParam: boolean): void {
    this.isAddPerson.set(isAddPersonParam);
  }

  addPerson(): void {
    const person: Person = {
      id: 0,
      name: this.postName,
      lastname: this.postLastname,
      zipcode: this.postZipcode,
      city: this.postCity,
      color: this.postColor
    }

    if (this.isPersonNotComplete(person)) {
      this.notificationService.show('Please fill all fields');
      return;
    }

    this.httpClientService.postPerson(person).subscribe(res => {
      if (res.status === 201) {
        this.filterService.triggerApplyFilter();
        this.notificationService.show('Person created. Filter reapplied.');
      } else {
        this.notificationService.show('Could not create person. See console logs.');
        console.warn(res);
      }
    });
  }

  isPersonNotComplete(person: Person): boolean {
    return (
      !person.name
      || !person.lastname
      || !person.zipcode
      || !person.city
      || !person.color
    );
  }

  setPostColor(color: string): void {
    this.postColor = color;
  }
}
