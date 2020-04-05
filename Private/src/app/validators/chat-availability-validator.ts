import { FormGroup } from '@angular/forms';

export function chatAvailabilityValidation(availableFrom: string, availableUntil: string) {
    return (formGroup: FormGroup) => {

        const from = formGroup.controls[availableFrom];
        const until = formGroup.controls[availableUntil];

        if (until.errors && !until.errors.invalidTimes) {
            return;
        }

        if (from.value >= until.value) {
          until.setErrors({ invalidTimes: true });
        } else {
          until.setErrors(null);
        }
    };
}
