Nexus CAS Plugin
================

This is a [Sonatype Nexus](http://www.sonatype.org/nexus/) plugin providing authentication with
[Jasig CAS](http://jasig.org/cas/) using its [REST API](https://wiki.jasig.org/display/CASUM/RESTful+API).

**IMPORTANT:** the CAS REST API is not enabled by default, please make sure to enable it by following
[these instructions](https://wiki.jasig.org/display/CASUM/RESTful+API).

Usage
-----

1. Create a file named `cas-plugin.xml` in your `sonatype-work/nexus/conf` folder.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<casConfiguration>
    <casServerUrl>https://[cas-host]:[cas-port]/cas/</casServerUrl>
    <casRestTicketUrl>https://[cas-host]:[cas-port]/cas/v1/tickets/</casRestTicketUrl>
    <casService>http://[nexus-host]:[nexus-port]/nexus/</casService>
</casConfiguration>
```

2. Unzip the `nexus-cas-plugin` bundle in your `sonatype-work/nexus/plugin-repository` folder.

3. (Re)start Nexus and use the Administration -> Server panel to add the `CAS Authentication Realm`
to the list of active realms.

4. Watch the Nexus and CAS logs to check whether authentication is working as expected.
