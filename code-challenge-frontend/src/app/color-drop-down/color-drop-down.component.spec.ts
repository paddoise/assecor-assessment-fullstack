import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColorDropDownComponent } from './color-drop-down.component';

describe('ColorDropDownComponent', () => {
  let component: ColorDropDownComponent;
  let fixture: ComponentFixture<ColorDropDownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColorDropDownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ColorDropDownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
