import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISimpleUser, SimpleUser } from '../simple-user.model';

import { SimpleUserService } from './simple-user.service';

describe('SimpleUser Service', () => {
  let service: SimpleUserService;
  let httpMock: HttpTestingController;
  let elemDefault: ISimpleUser;
  let expectedResult: ISimpleUser | ISimpleUser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SimpleUserService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      firstName: 'AAAAAAA',
      lastName: 'AAAAAAA',
      email: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a SimpleUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new SimpleUser()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SimpleUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          firstName: 'BBBBBB',
          lastName: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SimpleUser', () => {
      const patchObject = Object.assign(
        {
          firstName: 'BBBBBB',
        },
        new SimpleUser()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SimpleUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          firstName: 'BBBBBB',
          lastName: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a SimpleUser', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSimpleUserToCollectionIfMissing', () => {
      it('should add a SimpleUser to an empty array', () => {
        const simpleUser: ISimpleUser = { id: 123 };
        expectedResult = service.addSimpleUserToCollectionIfMissing([], simpleUser);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(simpleUser);
      });

      it('should not add a SimpleUser to an array that contains it', () => {
        const simpleUser: ISimpleUser = { id: 123 };
        const simpleUserCollection: ISimpleUser[] = [
          {
            ...simpleUser,
          },
          { id: 456 },
        ];
        expectedResult = service.addSimpleUserToCollectionIfMissing(simpleUserCollection, simpleUser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SimpleUser to an array that doesn't contain it", () => {
        const simpleUser: ISimpleUser = { id: 123 };
        const simpleUserCollection: ISimpleUser[] = [{ id: 456 }];
        expectedResult = service.addSimpleUserToCollectionIfMissing(simpleUserCollection, simpleUser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(simpleUser);
      });

      it('should add only unique SimpleUser to an array', () => {
        const simpleUserArray: ISimpleUser[] = [{ id: 123 }, { id: 456 }, { id: 87757 }];
        const simpleUserCollection: ISimpleUser[] = [{ id: 123 }];
        expectedResult = service.addSimpleUserToCollectionIfMissing(simpleUserCollection, ...simpleUserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const simpleUser: ISimpleUser = { id: 123 };
        const simpleUser2: ISimpleUser = { id: 456 };
        expectedResult = service.addSimpleUserToCollectionIfMissing([], simpleUser, simpleUser2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(simpleUser);
        expect(expectedResult).toContain(simpleUser2);
      });

      it('should accept null and undefined values', () => {
        const simpleUser: ISimpleUser = { id: 123 };
        expectedResult = service.addSimpleUserToCollectionIfMissing([], null, simpleUser, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(simpleUser);
      });

      it('should return initial array if no SimpleUser is added', () => {
        const simpleUserCollection: ISimpleUser[] = [{ id: 123 }];
        expectedResult = service.addSimpleUserToCollectionIfMissing(simpleUserCollection, undefined, null);
        expect(expectedResult).toEqual(simpleUserCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
