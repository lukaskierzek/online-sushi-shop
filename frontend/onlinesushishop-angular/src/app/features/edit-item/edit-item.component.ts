import {Component, OnDestroy, OnInit} from '@angular/core';
import {ItemService} from '../services/item.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {NavbarService} from '../../layout/services/navbar.service';
import {Form, FormArray, FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {forkJoin, Subscription} from 'rxjs';
import {GlobalService} from '../../services/global.service';
import {JsonPipe} from '@angular/common';

@Component({
  selector: 'app-edit-item',
  imports: [
    ReactiveFormsModule,
    JsonPipe
  ],
  templateUrl: './edit-item.component.html',
  styleUrl: './edit-item.component.css'
})
export class EditItemComponent implements OnInit, OnDestroy {
  public itemId: any
  public itemById: any = {};
  public mainCategories: any = [];
  public subcategories: any = [];

  private getItemByIdSubscription?: Subscription;
  private getItemSubscription?: Subscription;
  private getMainCategoriesSubscription?: Subscription;
  private getSubcategoriesSubscription?: Subscription;

  editItemForm: any;

  constructor(private itemService: ItemService,
              private activatedRoute: ActivatedRoute,
              private navbarService: NavbarService,
              private fb: FormBuilder,
              private globalService: GlobalService) {

  }

  ngOnDestroy(): void {
    this.getItemByIdSubscription?.unsubscribe();
    this.getItemSubscription?.unsubscribe();
    this.getMainCategoriesSubscription?.unsubscribe();
    this.getSubcategoriesSubscription?.unsubscribe();
  }

  ngOnInit(): void {
    this.editItemForm = this.fb.group({
      id: [''],
      name: [''],
      actualPrice: [''],
      oldPrice: [''],
      description: [''],
      mainCategory: [''],
      imageURL: [''],
      isHidden: [''],
      subcategories: this.fb.array([])
    })

    this.activatedRoute.paramMap.subscribe((params: ParamMap): void => {
      this.itemId = params.get('id');
    });

    forkJoin([
      this.navbarService.getMainCategories(),
      this.itemService.getSubcategories()
    ]).subscribe(([mainCategories, subcategories]: [any, any]): void => {
      this.mainCategories = mainCategories;
      this.subcategories = subcategories;
      this.getItemById(this.itemId);
    })
  }

  patchSubcategories(id: number, check: boolean, name: string) {
    return this.fb.group({
      id: [id],
      isChecked: [check],
      name: [name]
    })
  }

  get getSubcategoriesFormArray() {
    return this.editItemForm.get('subcategories') as FormArray
  }

  private getItemById(itemId: any): void {
    this.globalService.logGetMessage("Item id", this.itemId);
    const controls = this.editItemForm.get('subcategories') as FormArray;
    this.getItemByIdSubscription = this.itemService.getItemById(itemId)
      .subscribe({
        next: (data: any): void => {
          this.itemById = data;
          this.globalService.logGetMessage(`Item by id ${itemId} from getItemById`, data);

          this.editItemForm.get('id')?.setValue(this.itemById.itemId);
          this.editItemForm.get('name')?.setValue(this.itemById.itemName);
          this.editItemForm.get('actualPrice')?.setValue(this.itemById.itemActualPrice);
          this.editItemForm.get('oldPrice')?.setValue(this.itemById.itemOldPrice);
          this.editItemForm.get('description')?.setValue(this.itemById.itemComment);
          this.editItemForm.get('imageURL')?.setValue(this.itemById.itemImageUrl);
          this.editItemForm.get('isHidden')?.setValue(this.itemById.itemIsHidden);
          this.editItemForm.get('mainCategory')?.setValue(this.itemById.itemMainCategory);

          controls.clear();

          this.subcategories.forEach((sub: any) => {
            if (this.itemById.itemSubcategories.find((value: any) => value.subcategoryName === sub.subcategoryName))
              controls.push(this.patchSubcategories(sub.subcategoryId, true, sub.subcategoryName))
            else
              controls.push(this.patchSubcategories(sub.subcategoryId, false, sub.subcategoryName))
          })


        },
        error: err => {
          console.error(err);
        }
      })
  }

  submitEditItem() {
    //TODO: Add update logic
    const editItemFormValue: any = this.editItemForm.value;
    const editItemFormValueArray = Array.isArray(editItemFormValue) ? editItemFormValue : [editItemFormValue];

    editItemFormValueArray.forEach((item: any) => {
      item.subcategories = item.subcategories
        .filter((sub: any) => sub.isChecked !== false)
        .map((sub: any) => {
          delete sub.isChecked;
          return sub;
        });
    });

    // editItemFormValueArray.forEach((item: any) => {
    //   item.subcategories = item.subcategories.filter((sub: any) => {
    //     return sub.isChecked !== false
    //   })
    //   item.subcategories.forEach((sub: any) => {
    //     delete sub.isChecked;
    //   })
    // })


    console.log("Update click!")
    console.log(editItemFormValue)
  }
}
