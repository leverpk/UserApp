import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISimpleUser, getSimpleUserIdentifier } from '../simple-user.model';

export type EntityResponseType = HttpResponse<ISimpleUser>;
export type EntityArrayResponseType = HttpResponse<ISimpleUser[]>;

@Injectable({ providedIn: 'root' })
export class SimpleUserService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/simple-users');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(simpleUser: ISimpleUser): Observable<EntityResponseType> {
    return this.http.post<ISimpleUser>(this.resourceUrl, simpleUser, { observe: 'response' });
  }

  update(simpleUser: ISimpleUser): Observable<EntityResponseType> {
    return this.http.put<ISimpleUser>(`${this.resourceUrl}/${getSimpleUserIdentifier(simpleUser) as number}`, simpleUser, {
      observe: 'response',
    });
  }

  partialUpdate(simpleUser: ISimpleUser): Observable<EntityResponseType> {
    return this.http.patch<ISimpleUser>(`${this.resourceUrl}/${getSimpleUserIdentifier(simpleUser) as number}`, simpleUser, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISimpleUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISimpleUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSimpleUserToCollectionIfMissing(
    simpleUserCollection: ISimpleUser[],
    ...simpleUsersToCheck: (ISimpleUser | null | undefined)[]
  ): ISimpleUser[] {
    const simpleUsers: ISimpleUser[] = simpleUsersToCheck.filter(isPresent);
    if (simpleUsers.length > 0) {
      const simpleUserCollectionIdentifiers = simpleUserCollection.map(simpleUserItem => getSimpleUserIdentifier(simpleUserItem)!);
      const simpleUsersToAdd = simpleUsers.filter(simpleUserItem => {
        const simpleUserIdentifier = getSimpleUserIdentifier(simpleUserItem);
        if (simpleUserIdentifier == null || simpleUserCollectionIdentifiers.includes(simpleUserIdentifier)) {
          return false;
        }
        simpleUserCollectionIdentifiers.push(simpleUserIdentifier);
        return true;
      });
      return [...simpleUsersToAdd, ...simpleUserCollection];
    }
    return simpleUserCollection;
  }
}
