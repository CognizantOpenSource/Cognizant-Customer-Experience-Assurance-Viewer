export class WorkBenchAPI {
    private base: string;
    constructor(base: string) {
        this.base = base;
    }

    get projects() {
        return `${this.base}projects`;
    }

    getProject(id: any): string {
        return `${this.projects}/${id}`;
    }

    get userDashboardConfig() {
        return `${this.base}users/settings/dashboard`;
    }
}
