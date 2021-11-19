import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentHotelsComponent } from './content-hotels.component';

describe('ContentHotelsComponent', () => {
  let component: ContentHotelsComponent;
  let fixture: ComponentFixture<ContentHotelsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContentHotelsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentHotelsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
