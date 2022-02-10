import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISimpleUser } from '../simple-user.model';
import { SimpleUserService } from '../service/simple-user.service';

@Component({
  templateUrl: './simple-user-delete-dialog.component.html',
})
export class SimpleUserDeleteDialogComponent {
  simpleUser?: ISimpleUser;

  constructor(protected simpleUserService: SimpleUserService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.simpleUserService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
