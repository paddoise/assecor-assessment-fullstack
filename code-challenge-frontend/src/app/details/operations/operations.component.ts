import { Component, output, signal } from '@angular/core';

@Component({
  selector: 'app-operations',
  imports: [],
  templateUrl: './operations.component.html',
  styleUrl: './operations.component.scss'
})
export class OperationsComponent {
  isAddPerson = signal<boolean>(false);
  changedState = output<boolean>();

  setIsAddPerson(isAddPersonParam: boolean): void {
    this.isAddPerson.set(isAddPersonParam);
    this.changedState.emit(this.isAddPerson());
  }
}
