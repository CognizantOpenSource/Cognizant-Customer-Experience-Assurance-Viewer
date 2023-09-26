export const environment = {
  production: true,
  api: {
    auth: `${window.location.origin}/authapi/`,
    workbench: `${window.location.origin}/workbench/`,
    reports: `${window.location.origin}/reports/`,
    execution: `${window.location.origin}/execution/`,
    idashboard: `${window.location.origin}/dashboard/`,
    selfheal: `${window.location.origin}/selfheal/`
  },
  modules: {
    reports: true, execution: true, workbench: true
  },
  storagePrefix: 'leap-ui',
  logger: false
};
