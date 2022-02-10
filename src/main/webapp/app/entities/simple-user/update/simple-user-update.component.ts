import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISimpleUser, SimpleUser } from '../simple-user.model';
import { SimpleUserService } from '../service/simple-user.service';

@Component({
  selector: 'jhi-simple-user-update',
  templateUrl: './simple-user-update.component.html',
})
export class SimpleUserUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    firstName: [],
    lastName: [],
    email: [],
  });

  constructor(protected simpleUserService: SimpleUserService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ simpleUser }) => {
      this.updateForm(simpleUser);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const simpleUser = this.createFromForm();
    if (simpleUser.id !== undefined) {
      this.subscribeToSaveResponse(this.simpleUserService.update(simpleUser));
    } else {
      this.subscribeToSaveResponse(this.simpleUserService.create(simpleUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISimpleUser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(simpleUser: ISimpleUser): void {
    this.editForm.patchValue({
      id: simpleUser.id,
      firstName: simpleUser.firstName,
      lastName: simpleUser.lastName,
      email: simpleUser.email,
    });
  }

  protected createFromForm(): ISimpleUser {
    return {
      ...new SimpleUser(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
    };
  }
}
