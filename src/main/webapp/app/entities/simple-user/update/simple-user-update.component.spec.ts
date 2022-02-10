import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SimpleUserService } from '../service/simple-user.service';
import { ISimpleUser, SimpleUser } from '../simple-user.model';

import { SimpleUserUpdateComponent } from './simple-user-update.component';

describe('SimpleUser Management Update Component', () => {
  let comp: SimpleUserUpdateComponent;
  let fixture: ComponentFixture<SimpleUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let simpleUserService: SimpleUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SimpleUserUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SimpleUserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SimpleUserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    simpleUserService = TestBed.inject(SimpleUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const simpleUser: ISimpleUser = { id: 456 };

      activatedRoute.data = of({ simpleUser });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(simpleUser));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SimpleUser>>();
      const simpleUser = { id: 123 };
      jest.spyOn(simpleUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ simpleUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: simpleUser }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(simpleUserService.update).toHaveBeenCalledWith(simpleUser);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SimpleUser>>();
      const simpleUser = new SimpleUser();
      jest.spyOn(simpleUserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ simpleUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: simpleUser }));
      saveSubject.complete();

      // THEN
      expect(simpleUserService.create).toHaveBeenCalledWith(simpleUser);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SimpleUser>>();
      const simpleUser = { id: 123 };
      jest.spyOn(simpleUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ simpleUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(simpleUserService.update).toHaveBeenCalledWith(simpleUser);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
