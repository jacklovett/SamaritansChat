import { FormGroup } from '@angular/forms';

export function passwordMatchValidation(newPassword: string, confirmPassword: string) {
    return (formGroup: FormGroup) => {

        const newValue = formGroup.controls[newPassword];
        const confirm = formGroup.controls[confirmPassword];

        if (confirm.errors && !confirm.errors.passwordMisMatch) {
            return;
        }

        if (newValue.value === confirm.value) {
          newValue.setErrors(null);
          confirm.setErrors(null);
        } else {
          newValue.setErrors({ passwordMisMatch: true });
          confirm.setErrors({ passwordMisMatch: true });
        }
    };
}
