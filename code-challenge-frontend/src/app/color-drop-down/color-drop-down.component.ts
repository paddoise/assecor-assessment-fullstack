import { Component, output } from '@angular/core';
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-color-drop-down',
  imports: [FormsModule],
  templateUrl: './color-drop-down.component.html',
  styleUrl: './color-drop-down.component.scss'
})
export class ColorDropDownComponent {
  colorEmitter = output<string>();
  color: string = '';

  onSelect(): void {
    this.colorEmitter.emit(this.color);
  }
}
