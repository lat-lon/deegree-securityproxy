deegree security proxy
======================

The deegree security proxy (DSP) is lightweight security framework for protecting OGC Web Services against unauthorised access using the [Spring Security](http://projects.spring.io/spring-security/) framework. 

# Continuous Integration
We are using the travis bot to build the software frequently and to verify each pull request. The current status for the master branch is:
[![Build Status](https://travis-ci.org/tfr42/deegree-securityproxy.png?branch=master)](https://travis-ci.org/tfr42/deegree-securityproxy)

# Support
There is commercial and community support available. For commercial support, please contact [lat/lon](http://www.lat-lon.de/en) the company which employs the people who wrote the deegree security proxy, and lead the development of the project. For community support, please use the github issue tracker.

# Contributing
Contributions are very welcome. Please get in contact with us via pull requests and the github issue tracker.

# License
deegree security proxy is distributed under the [GNU Affero General Public License, Version 3.0 (AGPL)](http://www.gnu.org/licenses/agpl-3.0.html).

# User documentation
The deegree security proxy requires Spring 3.2 with Spring Security 3.1 as a minimum and also requires Java SE 6 or higher. It can be installed on every Java EE 5 compliant web container such as Apache Tomcat 6.x. The deegree security proxy provides protection against unauthorised access for OGC Web Services such as WMS, WMTS, WCS, WFS, WPS, CSW served by deegree, GeoServer, MapServer or any other blackbox system. 

# Developer documentation
To build the DSP use Apache Maven with `mvn clean install`.

# Run the DSP docker container
`
docker run -p 8088:8080 --name dsp tfr42/deegree-security-proxy:latest
`