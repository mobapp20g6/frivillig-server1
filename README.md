# Frivillig server
This server is part of a project assigment done during the course ID303911 - Mobile og distribuerte applikasjoner at NTNU Ålesund.
Implemented using Jakarta EE (Payara Micro), PostgreSQL as persistance backend with a front-end proxy based on Traefik. These three services runs as
docker containers, as part of a docker-compose stack. This has been used as a common development platform as well as a production platform.

### To build
1. Follow instructions as stated in `/appsrv/README_DOCKER.md`. It is also possible to build the project without Docker, but then you need an already running PostgresSQL and Glassfish/Payara server.
