import { TestBed } from '@angular/core/testing';

import { ChatAvailabilityService } from './chat.availability.service';

describe('ChatAvailabilityService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ChatAvailabilityService = TestBed.get(ChatAvailabilityService);
    expect(service).toBeTruthy();
  });
});
