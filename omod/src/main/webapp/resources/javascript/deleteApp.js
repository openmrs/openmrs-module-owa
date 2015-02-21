
function deleteApp( appName ) {
    var result = window.confirm( 'Do you want to delete this app?' + "\n\n" + appName );
    
    if ( result )
    {
        $.ajax({
            url: '/openmrs/module/owa/deleteApp.htm',
            type: 'GET',
            dataType: 'html',
            cache: false,
            data: {
               appName: appName
            }
        })
        .done(function() {
            alert( "success" );
        })
        .fail(function() {
            alert( "error" );
        })
    }
    
  }
