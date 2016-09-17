[![Build Status](https://travis-ci.org/openmrs/openmrs-module-owa.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-owa)

OpenMRS Open Web Apps module
============================
A packaged app is an [Open Web App] (https://developer.mozilla.org/en-US/docs/Open_Web_apps_and_Web_standards) that has all of its resources (HTML, CSS, JavaScript, app manifest, and so on) contained in a zip file. This module allows uploading Open Web Apps (OWA) to OpenMRS using the user-interface at runtime. A packaged app is a ZIP file with an [app manifest] (http://www.w3.org/2008/webapps/manifest/) in its root directory. The manifest must be named `manifest.webapp`. The [Mozilla page on apps] (https://developer.mozilla.org/en-US/Apps/Quickstart) provides a detailed description.

Purpose of packaged apps
--
The purpose of packaged apps is to create custom UI on-top of [OpenMRS REST API] (https://wiki.openmrs.org/display/docs/REST+Web+Services+API+For+Clients). An OpenMRS implementation will often have unique requirements. Apps provide a convenient way to customize the user interface and lowers the barrier to programmer entry, by allowing choice of any web technologies. Apps do not have permissions to interact directly with the OpenMRS Java API. Instead, apps are expected to use functionality and interact with OpenMRS REST Web Services.

Creating apps
--
Apps are constructed with HTML, JavaScript and CSS files. Although these can also be in Python, PHP, ASP etc., but such app deployment scenario will be described later. Apps need a special file called `manifest.webapp` which describes the contents of the app. This file should be in the format specified by the [W3C Manifest for Web Applications] (http://www.w3.org/2008/webapps/manifest/). A basic example of the manifest.webapp is shown below:
```
{
    "version": "0.1",
    "name": "My App",
    "description": "My App is a Packaged App",
    "launch_path": "index.html",
    "icons": {
        "16": "img/icons/mortar-16.png",
        "48": "img/icons/mortar-48.png",
        "128": "img/icons/mortar-128.png"
    },
    "developer": {
        "name": "Me",
        "url": "http://me.com"
    },
    "default_locale": "en",
    "activities": {
        "openmrs": {
            "href": "*"
        }
    }
}
```
The `manifest.webapp` file must be located at the root of the project. Among the properties, the icons->48 property is used for the icon that is displayed on the list of apps that are installed on a OpenMRS instance. The `activities` property is an openmrs-specific extension meant to differentiate between a standard Open Web App and apps that can be installed on OpenMRS. The `*` value for href is converted to the appropriate URL when the app is uploaded and installed in OpenMRS. This value can then be used by the application's JavaScript and HTML files to make calls to the OpenMRS REST Web Services API and identify the correct location of OpenMRS server on which the app is installed. e.g. once the above app is installed on the OpenMRS demo server, its `activities` section will get changed as below:
```
"activities": {
    "openmrs": {
        "href": "https://demo.openmrs.org/openmrs"
    }
 }
```
To read the JSON structure into Javascript, you can use a regular AJAX request and parse the JSON into an object. Most Javascript libraries provide some support, for instance with jQuery it can be done like this:
```
$.getJSON( "manifest.webapp", function( json ) {
    var apiBaseUrl = json.activities.openmrs.href + "/ws/rest/v1";
} );
```
The app can contain HTML, Javascript, CSS, images and other files whic may be required to support it. The file structure could look something like this:
```
/
/manifest.webapp    #manifest file (mandatory)
/css/               #css stylesheets (optional)
/img/               #images (optional)
/js/                #javascripts (optional)
```
Note that it is only the `manifest.webapp` file which must be placed in the root. It is upto the developer to organize CSS, images and Javascript files inside the app as needed. All the files in the project should be compressed into a standard zip archive. Note that the `manifest.webapp` file must be located on the root of the zip archive (do not include a parent directory in the archive). The zip archive can then be uploaded into OpenMRS.

Creating apps in PHP/ASP/Python or another webserver (**hacky/whacky method**)
--
The OWA module allows you to configure the location where your apps get installed on the server (`owa.appFolderPath` global property). This means that if you configure your `owa.appFolderPath` to `www` of your Apache2, then it gets deployed onto Apache2 instead of your OpenMRS tomcat or another servlet container. If you can configure a reverse proxy nginx/apache2 nicely, users will assume its a normal app that is actually served from apache/nginx or what server have you.

You also are able to set the base URL (`owa.appBaseUrl`) where the apps can be accessible from. base URL can be either full request URL (e.g. `http://localhost:8080/openmrs/owa`) or relative to OpenMRS context path (e.g. `/owa`).
 
User guide
--
User guide on how to install and configure the module can be found here - [https://wiki.openmrs.org/display/docs/Open+Web+Apps+Module](https://wiki.openmrs.org/display/docs/Open+Web+Apps+Module)

Developer tutorial
--
The tutorial to create Open Web Apps can be found here - [https://wiki.openmrs.org/x/_o2QBQ](https://wiki.openmrs.org/x/_o2QBQ)
