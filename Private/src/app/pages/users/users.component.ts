import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';

import { ListUser } from './../../models/user';
import { UserService } from './../../services/user.service';
import { AlertService } from './../../services/alert.service';

import { Subscription } from 'rxjs';
import { DialogComponent } from 'src/app/components/dialog/dialog.component';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit, OnDestroy {
  loading = false;

  displayedColumns: string[] = [
    'name',
    'username',
    'email',
    'contactNumber',
    'lastActive',
    'edit',
    'delete',
  ];

  users: ListUser[] = [];
  dataSource: MatTableDataSource<ListUser>;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  isReloadRequiredSubscription: Subscription;

  constructor(
    public deleteDialog: MatDialog,
    private router: Router,
    private userService: UserService,
    private alertService: AlertService,
  ) {}

  ngOnInit() {
    this.loadUsers();

    this.isReloadRequiredSubscription = this.userService
      .isReloadRequired()
      .subscribe(result => {
        if (result) {
          this.loadUsers();
        }
      });
  }

  openDeleteDialog(userId: number) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = false;

    dialogConfig.data = { title: 'Confirm Delete' };

    dialogConfig.position = {
      'top': '36px',
    };

    const dialogRef = this.deleteDialog.open(DialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'delete') {
        this.deleteUser(userId);
      }
    });
  }

  editUser(user: ListUser) {
    this.router.navigate(['user', JSON.stringify(user.id)]);
  }

  addUser() {
    this.router.navigate(['register']);
  }

  search(event: Event) {
    const searchValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = searchValue.trim().toLowerCase();
  }

  private async loadUsers() {
    this.loading = true;
    try {
      const users = await this.userService.get().toPromise();
      this.users = users.map(user => ({
        id: user.id,
        name: `${user.firstName} ${user.lastName}`,
        username: user.username,
        email: user.email,
        contactNumber: user.contactNumber,
        lastActive: user.lastActive,
      }));
      setTimeout(() => {
        this.dataSource = new MatTableDataSource<ListUser>(this.users);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
    } catch (error) {
      this.alertService.error(error);
    }
    this.loading = false;
  }

  private deleteUser(userId: number) {
    this.loading = true;
    this.userService.delete(userId);
    this.loading = false;
  }

  ngOnDestroy() {
    this.isReloadRequiredSubscription.unsubscribe();
  }
}
