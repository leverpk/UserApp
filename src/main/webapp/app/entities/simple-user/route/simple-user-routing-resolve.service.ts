import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISimpleUser, SimpleUser } from '../simple-user.model';
import { SimpleUserService } from '../service/simple-user.service';

@Injectable({ providedIn: 'root' })
export class SimpleUserRoutingResolveService implements Resolve<ISimpleUser> {
  constructor(protected service: SimpleUserService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISimpleUser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((simpleUser: HttpResponse<SimpleUser>) => {
          if (simpleUser.body) {
            return of(simpleUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SimpleUser());
  }
}
