import { Component, OnInit, Input, ViewChild, OnChanges } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { DatePipe } from '@angular/common';

import { ColumnDetails } from './columnDetails';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent implements OnInit, OnChanges {
  @Input() data: Object[];
  @Input() columnDetails: ColumnDetails[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  dataSource: MatTableDataSource<Object>;
  displayedColumns: string[];

  constructor(private datePipe: DatePipe) {}

  ngOnInit() {
    this.displayedColumns = this.columnDetails.map((item) => item.id);
    this.dataSource = new MatTableDataSource<Object>(this.data);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnChanges() {
    this.dataSource = new MatTableDataSource<Object>(this.data);
  }

  search(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = searchValue.trim().toLowerCase();
  }

  getColumnValue(column: ColumnDetails, data: Object) {
    const value = column.value(data);

    if (!value) {
      return;
    }

    if (column.isDateTime) {
      return this.formatDateTime(value);
    }
    return value;
  }

  private formatDateTime(value: string): string {
    return this.datePipe.transform(value, 'dd/MM/yyyy h:mm a');
  }
}
