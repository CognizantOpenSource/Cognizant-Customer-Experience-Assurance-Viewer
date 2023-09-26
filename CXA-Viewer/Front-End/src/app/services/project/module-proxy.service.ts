import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, forkJoin, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { UserConfigService } from '@services/user-config.service';

interface Action {
    permission: string | RegExp;
    fn: (any, arg2?: any) => Observable<any>;
}
interface ProxyModule {
    name: string;
    actions: {
        createProject?: Action,
        deleteProject?: Action,
        updateUserProjects?: Action,
    };
}
@Injectable({
    providedIn: 'root'
})
export class ModuleProxyService {

    private _stateSource = new BehaviorSubject<ProxyModule[]>([]);

    constructor(
        private config: UserConfigService
    ) { }

    public deleteProject(project): Observable<any> {
        const reqs: any[] = [];
        if (project && project.id) {
            const { id, name } = project;
            for (const module of this._stateSource.value) {
                const action: Action | undefined = module.actions && module.actions.deleteProject;
                if (action) {
                    const hasPermission$ = this.config.hasAccessEnabled(action.permission);
                    reqs.push(hasPermission$.pipe(
                        switchMap(hasAccess => hasAccess ? action.fn({ id, name }) :
                            of({ status: hasAccess, module }))
                    ));
                }
            }
        }
        const array: any[] = reqs;
        return forkJoin(array);
    }
}
