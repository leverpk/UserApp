import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SimpleUserComponent } from '../list/simple-user.component';
import { SimpleUserDetailComponent } from '../detail/simple-user-detail.component';
import { SimpleUserUpdateComponent } from '../update/simple-user-update.component';
import { SimpleUserRoutingResolveService } from './simple-user-routing-resolve.service';

const simpleUserRoute: Routes = [
  {
    path: '',
    component: SimpleUserComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SimpleUserDetailComponent,
    resolve: {
      simpleUser: SimpleUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SimpleUserUpdateComponent,
    resolve: {
      simpleUser: SimpleUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SimpleUserUpdateComponent,
    resolve: {
      simpleUser: SimpleUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(simpleUserRoute)],
  exports: [RouterModule],
})
export class SimpleUserRoutingModule {}
