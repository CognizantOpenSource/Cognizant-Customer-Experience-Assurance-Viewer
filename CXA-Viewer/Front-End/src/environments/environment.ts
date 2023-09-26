// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

const port = 9003;
const host = `http://localhost:${port}`
export const environment = {
  production: false,
  api: {
    auth: `${host}/authapi/`,
    workbench: `${host}/workbench/`,
    reports: `${host}/reports/`,
    execution: `${host}/execution/`,
    idashboard: `${host}/dashboard/`,
    selfheal: `${host}/selfheal/`
  },
  modules: {
    reports: true, execution: true, workbench: true
  },
  storagePrefix: 'leap-ui',
  logger: false
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
import 'zone.js/dist/zone-error';  // Included with Angular CLI.
