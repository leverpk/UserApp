import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SimpleUserDetailComponent } from './simple-user-detail.component';

describe('SimpleUser Management Detail Component', () => {
  let comp: SimpleUserDetailComponent;
  let fixture: ComponentFixture<SimpleUserDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SimpleUserDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ simpleUser: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SimpleUserDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SimpleUserDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load simpleUser on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.simpleUser).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
