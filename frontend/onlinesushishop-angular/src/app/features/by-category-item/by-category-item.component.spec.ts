import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ByCategoryItemComponent } from './by-category-item.component';

describe('ByCategoryItemComponent', () => {
  let component: ByCategoryItemComponent;
  let fixture: ComponentFixture<ByCategoryItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ByCategoryItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ByCategoryItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
