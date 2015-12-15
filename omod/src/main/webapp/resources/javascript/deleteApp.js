function deleteApp(appName) {
    var result = window.confirm('Do you want to delete this app?' + "\n\n" + appName);
    if (result)
    {
        window.location = window.location.pathname.replace("manage.form","deleteApp.htm?appName=" + appName);
    }
}
