export interface UserActivity {
  eventType: string;
  payload: Record<string, any>;
  timestamp: string;
}
