export interface ChatLog {
  id: number;
  volunteer: string;
  username: string;
  rating: number;
  notes: string;
  startTime: string;
  endTime: string;
  transcriptId: number;
}

export interface Rating {
  id: number;
  message: string;
}

export const ratings: Rating[] = [
  { id: -3, message: 'Concern for volunteer safety, abusive, threatening' },
  { id: -2, message: 'Does not accept service, unwilling to end contact' },
  {
    id: -1,
    message: 'Not distressed, chatting, not prepared to talk feelings',
  },
  { id: 0, message: 'Distressed but not suicidal' },
  { id: 1, message: 'Suicidal feelings, distress & despair, but in control' },
  { id: 2, message: 'Suicidal feelings & plans. Extreme distress' },
  { id: 3, message: 'Suicide in progress' },
];
