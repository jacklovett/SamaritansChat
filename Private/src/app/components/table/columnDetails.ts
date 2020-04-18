export interface ColumnDetails {
  id: string;
  title: string;
  value: (item: Object) => string;
  iconName?: string;
  tooltip?: string;
  isDateTime?: boolean;
  onClick?: (id: number) => void;
}
