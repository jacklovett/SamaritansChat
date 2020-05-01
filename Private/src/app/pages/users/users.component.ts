import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

import { ListUser } from './../../models/user';
import { UserService } from './../../services/user.service';
import { AlertService } from './../../services/alert.service';

import { Subscription } from 'rxjs';
import { DialogComponent } from 'src/app/components/dialog/dialog.component';
import { ColumnDetails } from 'src/app/components/table/columnDetails';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit, OnDestroy {
  loading = false;

  users: ListUser[] = [];

  columnDetails: ColumnDetails[] = [
    {
      id: 'name',
      title: 'Name',
      value: (user: ListUser) => user.name,
    },
    {
      id: 'username',
      title: 'Username',
      value: (user: ListUser) => user.username,
    },
    {
      id: 'email',
      title: 'Email',
      value: (user: ListUser) => user.email,
    },
    {
      id: 'contact-number',
      title: 'Contact Number',
      value: (user: ListUser) => user.contactNumber,
    },
    {
      id: 'last-active',
      title: 'Last Active',
      value: (user: ListUser) => user.lastActive,
      isDateTime: true,
    },
    {
      id: 'edit',
      title: '',
      value: (user: ListUser) => `${user.id}`,
      iconName: 'edit',
      tooltip: 'Edit User',
      onClick: (id: number) => this.editUser(id),
    },
    {
      id: 'delete',
      title: '',
      value: (user: ListUser) => `${user.id}`,
      iconName: 'delete',
      tooltip: 'Delete User',
      onClick: (id: number) => this.openDeleteDialog(id),
    },
  ];

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
      .subscribe((result) => {
        if (result) {
          this.loadUsers();
        }
      });
  }

  openDeleteDialog(userId: number) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = false;

    const submitValue = 'delete';

    dialogConfig.data = {
      title: 'Confirm Delete',
      content: 'Are you sure you want to delete this user?',
      submitLabel: 'Yes',
      cancelLabel: 'No',
      submitValue,
    };

    dialogConfig.position = {
      'top': '36px',
    };

    const dialogRef = this.deleteDialog.open(DialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe((result) => {
      if (result === submitValue) {
        this.deleteUser(userId);
      }
    });
  }

  addUser = () => this.router.navigate(['register']);
  editUser = (id: number) => this.router.navigate(['user', id]);

  private async loadUsers() {
    this.loading = true;
    try {
      const users = await this.userService.get().toPromise();
      this.users = users.map((user) => ({
        id: user.id,
        name: `${user.firstName} ${user.lastName}`,
        username: user.username,
        email: user.email,
        contactNumber: user.contactNumber,
        lastActive: user.lastActive,
      }));
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
