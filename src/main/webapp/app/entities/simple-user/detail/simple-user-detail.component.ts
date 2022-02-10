import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISimpleUser } from '../simple-user.model';

@Component({
  selector: 'jhi-simple-user-detail',
  templateUrl: './simple-user-detail.component.html',
})
export class SimpleUserDetailComponent implements OnInit {
  simpleUser: ISimpleUser | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ simpleUser }) => {
      this.simpleUser = simpleUser;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
