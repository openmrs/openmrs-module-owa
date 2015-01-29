
function deleteApp( appId, appName ) {
  removeItem(appId, appName, i18n_confirm_delete, "deleteApp.action?appName=" + appName);
}
