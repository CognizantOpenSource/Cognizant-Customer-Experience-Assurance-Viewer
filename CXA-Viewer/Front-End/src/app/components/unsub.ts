import { Subject, Observable } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { OnDestroy, Injectable } from '@angular/core';

@Injectable()
export class UnSubscribable implements OnDestroy {

    private destroy$: Subject<boolean> = new Subject<boolean>();
    takeUntilDestroy = takeUntil(this.destroy$);

    constructor() { }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    /**
     * auto unsubscribe when component is destroyed
     * @param source observable to unsubscribe
     */
    managed<T>(source: Observable<T>): Observable<T> {
        return source.pipe(this.takeUntilDestroy) as Observable<T>;
    }
}
