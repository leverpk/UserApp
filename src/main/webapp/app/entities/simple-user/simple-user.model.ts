export interface ISimpleUser {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
}

export class SimpleUser implements ISimpleUser {
  constructor(public id?: number, public firstName?: string | null, public lastName?: string | null, public email?: string | null) {}
}

export function getSimpleUserIdentifier(simpleUser: ISimpleUser): number | undefined {
  return simpleUser.id;
}
