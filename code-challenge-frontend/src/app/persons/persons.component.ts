import { Component, output, signal } from '@angular/core';
import { HttpClientService } from '../http-client/http-client.service';

import { Person } from './person/person.module';
import { PersonComponent } from './person/person.component';
import { FilterComponent } from "./filter/filter.component";
import { Filter } from './filter/filter.module';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-persons',
  imports: [PersonComponent, FilterComponent],
  templateUrl: './persons.component.html',
  styleUrl: './persons.component.scss'
})
export class PersonsComponent {
  private httpClientService: HttpClientService; 
  persons = signal<Person[]>([]);
  emitCurrentPerson = output<Person>();

  constructor(
    httpClientService: HttpClientService,
    private notificationService: NotificationService
  ) {  
    this.httpClientService = httpClientService;  
  }

  ngOnInit(): void {
    this.fetchData();
  }

  fetchData(): void {
    this.httpClientService.getData().subscribe(this.handleResponse.bind(this));
  }

  passCurrentPerson(person: Person) {
    this.emitCurrentPerson.emit(person);
  }

  applyFilter(filter: Filter): void {
    switch (filter.field) {
      case 'id':
        this.httpClientService.getDataById(filter).subscribe(this.handleResponse.bind(this));        
        break;

      case 'color':
        this.httpClientService.getDataByColor(filter).subscribe(this.handleResponse.bind(this));
        break;
      
      case '':
        this.fetchData();
        break;

      default:
        this.fetchData();
        console.warn('Unknown filter type:', filter.field);
        break;
    }
  }

  private handleResponse(res: any): void {
    if (!res.status || res.status !== 200) {
      return;
    }
  
    const data: object = res.body?.msg;
  
    if (!data) {
      return;
    }
  
    const dataArr: Array<object> = Object.values(data);
    let tmpPersons: Person[] = [];
  
    dataArr.forEach((person: any) => {
      if (!this.isPerson(person)) {
        return;
      }
  
      tmpPersons.push(person);
    });
  
    if (tmpPersons.length <= 0) {
      this.notificationService.show('No person found');
    }
  
    this.persons.set(tmpPersons);
  }

  private isPerson(person: any): person is Person {
    return (
      person !== null &&
      typeof person.id === 'number' &&
      typeof person.name === 'string' &&
      typeof person.lastname === 'string' &&
      typeof person.zipcode === 'string' &&
      typeof person.city === 'string' &&
      typeof person.color === 'string'
    );
  }
}
