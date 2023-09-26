export class Project {
    name: string;
    id?: string;
    version: string;
    platform: string;
    ci: any;
    links?: any;
    creationDate?: number;
    selected?: boolean;
    public static parse(obj: any): Project {
        return Object.assign(new Project(), obj);
    }
    public static fromJson(json: string): Project {
        return Project.parse(JSON.parse(json));
    }
}
