import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'simple-user',
        data: { pageTitle: 'SimpleUsers' },
        loadChildren: () => import('./simple-user/simple-user.module').then(m => m.SimpleUserModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
