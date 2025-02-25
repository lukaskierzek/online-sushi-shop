import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnSaleItemsComponent } from './on-sale-items.component';

describe('OnSaleItemsComponent', () => {
  let component: OnSaleItemsComponent;
  let fixture: ComponentFixture<OnSaleItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OnSaleItemsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OnSaleItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
