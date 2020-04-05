export interface PartialNotification {
  id: number;
  read: boolean;
  processed: boolean;
}

export interface Notification extends PartialNotification {
  type: string;
  content: string;
  username: string;
  volunteer: string;
  cta: string;
  processedCTA: string;
}
