import { CheckPasswordValidator } from './check-password-validator';

describe('CheckPasswordValidator', () => {
  it('should create an instance', () => {
    const directive = new CheckPasswordValidator();
    expect(directive).toBeTruthy();
  });
});
