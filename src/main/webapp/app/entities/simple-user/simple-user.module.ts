import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SimpleUserComponent } from './list/simple-user.component';
import { SimpleUserDetailComponent } from './detail/simple-user-detail.component';
import { SimpleUserUpdateComponent } from './update/simple-user-update.component';
import { SimpleUserDeleteDialogComponent } from './delete/simple-user-delete-dialog.component';
import { SimpleUserRoutingModule } from './route/simple-user-routing.module';

@NgModule({
  imports: [SharedModule, SimpleUserRoutingModule],
  declarations: [SimpleUserComponent, SimpleUserDetailComponent, SimpleUserUpdateComponent, SimpleUserDeleteDialogComponent],
  entryComponents: [SimpleUserDeleteDialogComponent],
})
export class SimpleUserModule {}
