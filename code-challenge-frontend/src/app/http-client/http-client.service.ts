import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Person } from '../persons/person/person.module';
import { Filter } from '../persons/filter/filter.module';

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {
  private uri = 'http://localhost:8080';
  private header = new HttpHeaders();

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.header.append('Content-Type', 'application/json');
  }
  
  getData(): Observable<any> {
    return this.getDataFromEndpoint('/persons');
  }
  
  getDataById(filter: Filter): Observable<any> {
    return this.getDataFromEndpoint(`/persons/${filter.term}`);
  }
  
  getDataByColor(filter: Filter): Observable<any> {
    return this.getDataFromEndpoint(`/persons/color/${filter.term}`);
  }

  postPerson(person: Person): Observable<any> {
    let endpoint = '/person';
    let url = `${this.uri}${endpoint}`;
    let postData = {
      "name": person.name,
      "lastname": person.lastname,
      "zipcode": person.zipcode,
      "city": person.city,
      "color": person.color
    }

    return this.http.post(url, postData, { observe: 'response' });
  }

  private getDataFromEndpoint(endpoint: string): Observable<any> {
    const url = `${this.uri}${endpoint}`;
    return this.http.get(url, {
      headers: this.header,
      observe: 'response'
    });
  }
}
