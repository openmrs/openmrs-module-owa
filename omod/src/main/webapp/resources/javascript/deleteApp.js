function deleteApp(appName) {
    var result = window.confirm('Do you want to delete this app?' + "\n\n" + appName);
    if (result)
    {
        window.location = '/openmrs/module/owa/deleteApp.htm?appName=' + appName;
    }
}
