export class API {
    private base: string;
    constructor(base: string) {
        this.base = base;
    }
    get APIToken() {
        return `${this.base}external-token/current-user/token`;
    }
    get authToken() {
        return `${this.base}auth/token`;
    }
    get authCode() {
        return `http://localhost:3000/authCode`;
    }
    get profile() {
        return `${this.base}users/profile`;
    }
    updatePassword(params: any) {
        return `${this.base}users/password/update`;  
    }
}
