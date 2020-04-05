import { ChatAvailabilityValidator } from './chat-availability-validator';

describe('ChatAvailabilityValidator', () => {
  it('should create an instance', () => {
    const directive = new ChatAvailabilityValidator('00', '02', null);
    expect(directive).toBeTruthy();
  });
});
