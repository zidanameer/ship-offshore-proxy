# Ship-Offshore Proxy (Spring Boot)
## How to Run
Please use the commands below
The docker images are listed below
### Server
https://hub.docker.com/r/zidan98/offshore-proxy

### Client
https://hub.docker.com/r/zidan98/ship-proxy
### Build JARs
```bash
./mvn clean install
```
### Build the docker image
```bash
docker-compose up --build
```
### Run the curl command once the jars are up and running.

```bash
curl -x http://localhost:8080 http://httpforever.com/
```

