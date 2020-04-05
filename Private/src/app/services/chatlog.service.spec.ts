import { TestBed } from '@angular/core/testing';

import { ChatlogService } from './chatlog.service';

describe('ChatlogService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ChatlogService = TestBed.get(ChatlogService);
    expect(service).toBeTruthy();
  });
});
