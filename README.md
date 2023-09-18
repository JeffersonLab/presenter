# presenter [![CI](https://github.com/JeffersonLab/presenter/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/presenter/actions/workflows/ci.yml) [![Docker](https://img.shields.io/docker/v/jeffersonlab/presenter?sort=semver&label=DockerHub)](https://hub.docker.com/r/jeffersonlab/presenter)
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application for Program Deputy (PD) presentations at Jefferson Lab.

![Screenshot](https://github.com/JeffersonLab/presenter/raw/main/Screenshot.png?raw=true "Screenshot")

---
- [Overview](https://github.com/JeffersonLab/presenter#overview)
- [Quick Start with Compose](https://github.com/JeffersonLab/presenter#quick-start-with-compose)
- [Install](https://github.com/JeffersonLab/presenter#install)
- [Configure](https://github.com/JeffersonLab/presenter#configure)
- [Build](https://github.com/JeffersonLab/presenter#build)
- [Develop](https://github.com/JeffersonLab/presenter#develop) 
- [Release](https://github.com/JeffersonLab/presenter#release)
- [Deploy](https://github.com/JeffersonLab/presenter#deploy)
- [See Also](https://github.com/JeffersonLab/presenter#see-also)
---

## Overview
The JLab presenter app assists crew chiefs in documenting and program deputies reporting on shift activity.  The app tightly integrates with other JLab apps to report downtime ([DTM](https://github.com/JeffersonLab/dtm)), system readiness ([SRM](https://github.com/JeffersonLab/srm)), schedules ([Calendar](https://github.com/JeffersonLab/calendar)), time accounting ([BTM](https://github.com/JeffersonLab/btm)), and geographical task hazards ([Workmap](https://github.com/JeffersonLab/workmap)).  The [resize](https://github.com/JeffersonLab/resize) app ensures images are reasonably sized.

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/presenter
cd presenter
```
2. Launch [Compose](https://github.com/docker/compose)
```
docker compose up
```
3. Navigate to page
```
http://localhost:8080/presenter
```

**Note**: Login with demo username "tbrown" and password "password".

**See**: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install
This application requires a Java 11+ JVM and standard library to run, plus a Java EE 8+ application server (developed with Wildfly).

1. Install service [dependencies](https://github.com/JeffersonLab/presenter/blob/main/deps.yml)
2. Download [Wildfly 26.1.3](https://www.wildfly.org/downloads/)
3. [Configure](https://github.com/JeffersonLab/presenter#configure) Wildfly and start it
4. Download [presenter.war](https://github.com/JeffersonLab/presenter/releases) and deploy it to Wildfly
5. Navigate your web browser to [localhost:8080/presenter](http://localhost:8080/presenter)

## Configure

### Configtime
Wildfly must be pre-configured before the first deployment of the app.  The [wildfly bash scripts](https://github.com/JeffersonLab/wildfly#configure) can be used to accomplish this.  See the [Dockerfile](https://github.com/JeffersonLab/presenter/blob/main/Dockerfile) for an example.

### Runtime
Uses a subset of the [Smoothness Environment Variables](https://github.com/JeffersonLab/smoothness#global-runtime) including:
 - BACKEND_SERVER_URL
 - FRONTEND_SERVER_URL
 - PUPPET_SHOW_SERVER_URL
 
 The following application specific envs are also used:

| Name                              | Description                                                                                                                                 |
|-----------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| CALENDAR_URL                      | ATLis SAD Calendar URL                                                                                                                      |
| BTM_URL                           | Beam time mananger URL               |
| DTM_URL                           | Down time manager URL                |
| POWER_URL                         | Power meters URL                     |
| RESIZE_URL                        | ImageMagick resize service URL       |
| SRM_URL                           | System readiness manager URL         |
| WEATHER_URL                       | Weather app URL                      |
| WHITEBOARD_URL                    | Whiteboard app URL                   |
| WORKMAP_URL                       | ATLis Workmap URL                    |

**See**: Docker [config](https://github.com/JeffersonLab/presenter/blob/main/docker-compose.yml) example.

### Database
The application requires an Oracle 18+ database with the following [schema](https://github.com/JeffersonLab/presenter/tree/main/docker/oracle/setup) installed.   The application server hosting the app must also be configured with a JNDI datasource.

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/presenter
cd presenter
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

## Develop
In order to iterate rapidly when making changes it's often useful to run the app directly on the local workstation, perhaps leveraging an IDE.  In this scenario run the service dependencies with:
```
docker compose -f deps.yml up
```
**Note**: The local install of Wildfly should be [configured](https://github.com/JeffersonLab/presenter#configure) to proxy connections to services via localhost and therefore the environment variables should contain:
```
KEYCLOAK_BACKEND_SERVER_URL=http://localhost:8081
FRONTEND_SERVER_URL=https://localhost:8443
```
Further, the local DataSource must also leverage localhost port forwarding so the `standalone.xml` connection-url field should be: `jdbc:oracle:thin:@//localhost:1521/xepdb1`.  

The [server](https://github.com/JeffersonLab/wildfly/blob/main/scripts/server-setup.sh) and [app](https://github.com/JeffersonLab/wildfly/blob/main/scripts/app-setup.sh) setup scripts can be used to setup a local instance of Wildfly. 

## Release
1. Bump the release date and version number in build.gradle and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
2. Create a new release on the GitHub Releases page corresponding to the same version in the build.gradle.   The release should enumerate changes and link issues.   A war artifact can be attached to the release to facilitate easy install by users.
3. [Publish to DockerHub](https://github.com/JeffersonLab/presenter/actions/workflows/docker-publish.yml) GitHub Action should run automatically.
4. Bump and commit quick start [image version](https://github.com/JeffersonLab/presenter/blob/main/docker-compose.override.yml)

## Deploy
At JLab this app is found at [ace.jlab.org/presenter](https://ace.jlab.org/presenter) and internally at [acctest.acc.jlab.org/presenter](https://acctest.acc.jlab.org/presenter).  However, those servers are proxies for `wildfly6.acc.jlab.org` and `wildflytest6.acc.jlab.org` respectively.   A [deploy script](https://github.com/JeffersonLab/wildfly/blob/main/scripts/deploy.sh) is provided to automate wget and deploy.  Example:

```
/root/setup/deploy.sh presenter v1.2.3
```

**JLab Internal Docs**:  [InstallGuideWildflyRHEL9](https://accwiki.acc.jlab.org/do/view/SysAdmin/InstallGuideWildflyRHEL9)

## See Also
 - [JLab ACE management-app list](https://github.com/search?q=org%3Ajeffersonlab+topic%3Aace+topic%3Amanagement-app&type=repositories)
