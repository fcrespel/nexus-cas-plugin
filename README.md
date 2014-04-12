Nexus CAS Plugin
================

This is a [Sonatype Nexus](http://www.sonatype.org/nexus/) plugin providing authentication with
[Jasig CAS](http://jasig.org/cas/) using its [REST API](https://wiki.jasig.org/display/CASUM/RESTful+API).

**IMPORTANT:** the CAS REST API is not enabled by default, please make sure to enable it by following
[these instructions](https://wiki.jasig.org/display/CASUM/RESTful+API).

Building from source
--------------------

1. Checkout or download the source code from the latest tag on GitHub.
2. Execute `mvn clean verify` from your local source code folder (install [Maven](http://maven.apache.org) if not already done).
3. Find the `nexus-cas-plugin-[version]-bundle.zip` file in the `target` subfolder.


Installation
------------

1. Create a file named `cas-plugin.xml` in your `sonatype-work/nexus/conf` folder, containing at least the following:
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


Configuration
-------------

The plugin configuration is stored in the `sonatype-work/nexus/conf/cas-plugin.xml` file.
The root `<casConfiguration>` element may contain the following children:

* **casServerUrl** *(required)*: full URL of the CAS server to use for ticket validation (e.g. https://example.org/cas/)
* **casRestTicketUrl** *(required)*: full URL of the CAS REST API to use for authentication (e.g. https://example.org/cas/v1/tickets/)
* **casService** *(required)*: full URL of the service to present to the CAS server (e.g. http://example.org/nexus/)
* **validationProtocol** *(default "CAS")*: CAS ticket validation protocol (CAS or SAML)
* **roleAttributeNames** *(default "groups,roles")*: comma-separated list of role attribute names
* **connectTimeout** *(default "5000")*: CAS REST client connect timeout (in milliseconds)
* **readTimeout** *(default "5000")*: CAS REST client read timeout (in milliseconds)


Changelog
---------

**Version 1.0.0**
* Initial release.

**Version 1.0.1**
* Fix for SAML 1.1 support.
* Fix for IncorrectCredentialsException during authentication.
* Support for attributes with multiple values.

**Version 1.1.0**
* Support for external user/group role mappings.

**Version 1.2.0**
* Compatibility with Nexus 2.7.x and higher.
* Conversion of Plexus components to JSR-330.

**Version 1.2.1**
* Compatibility with Nexus 2.8.x and higher.
