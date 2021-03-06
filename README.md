## MSA Chat ![workflow](https://github.com/oognuyh/msa-chat/actions/workflows/backend.yml/badge.svg) ![workflow](https://github.com/oognuyh/msa-chat/actions/workflows/frontend.yml/badge.svg)
<img src="https://img.shields.io/badge/Prometheus-E6522C.svg?&logo=Prometheus&logoColor=white"> <img src="https://img.shields.io/badge/Grafana-F46800.svg?&logo=Grafana&logoColor=white"> <img src="https://img.shields.io/badge/Vue.js-4FC08D.svg?&logo=Vue.js&logoColor=white"> <img src="https://img.shields.io/badge/Spring Boot-6DB33F.svg?&logo=SpringBoot&logoColor=white"> <img src="https://img.shields.io/badge/MongoDB-47A248.svg?&logo=MongoDB&logoColor=white"> <img src="https://img.shields.io/badge/NGINX-009639.svg?&logo=NGINX&logoColor=white">  <img src="https://img.shields.io/badge/GitHub Actions-2088FF.svg?&logo=GitHubActions&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED.svg?&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/Visual Studio Code-007ACC.svg?&logo=VisualStudioCode&logoColor=white"> <img src="https://img.shields.io/badge/Vuetify-1867C0.svg?&logo=Vuetify&logoColor=white"> <img src="https://img.shields.io/badge/Apache Kafka-231F20.svg?&logo=ApacheKafka&logoColor=white"> 

> 스프링 클라우드를 활용한 마이크로서비스 채팅 프로젝트

Spring Cloud, WebSocket, Vue, Kafka, Keycloak, Minio를 활용하여 구현한 채팅 프로젝트입니다. 해당 프로젝트는 Discovery / Gateway / User / Friend / Image / Chat / Notification / Config / Auth (Keycloak)으로 구성하였고 서비스간 통신은 Open Feign / Kafka를 활용했습니다. 또한, Zipkin, Micrometer, Fluent Bit, Prometheus, Grafana를 통해 Tracing, Metrics, Logging을 시각화하였습니다.

## Features
- 다이렉트/그룹 채팅
- 사용자/그룹 채팅 검색, 사용자 상태 변경
- 로그인/아웃, 회원가입, 회원정보 변경
- 새 메세지 알림

## Skills
- Backend
    - Java, Kotlin, Spring Boot, Spring WebSocket, Spring Cloud, Spring Data JPA, Spring Security, Gradle, Keycloak, Minio, Prometheus, Loki, Grafana ..
- Frontend
    - Vue.js, Vuetify, Vuex, Vue-Router, Axios, Nginx
- Database
    - MongoDB, MySQL, PostgresDB
- CI/CD
    - Github Actions
- Tool
    - Visual Studio Code, Intellij

## System architecture
![system-architecture](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/system-architecture.png)  
Github에 푸쉬하면 Github Actions를 활용하여 빌드 후 이미지를 Docker Hub에 푸쉬하며, 푸쉬된 이미지를 Docker Compose를 활용하여 다중 어플리케이션 환경을 구축했습니다. 서비스 간 통신이 필요한 경우 Kafka와 Open Feign을 활용했습니다. 인증, 인가 서비스를 Keycloak 오픈소스를 활용하였고 이미지 서비스에 MinIO 오브젝트 스토리지 서버를 연결했습니다. 또한, 서비스들의 로그, 메트릭 그리고 추적을 시각화하여 관리할 수 있도록 구성했습니다.

## ERD
![erd](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/erd.png)  

## Screen flow
![screen-flow](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/screen-flow.png)  

## Installation and Getting Started
```
git clone https://github.com/oognuyh/msa-chat.git
cd msa-chat
docker-compose up --build
```

| Port | Description | ID / PWD |
|:-:|:-:|:-:|
| 3000 | Frontend Web |  |
| 3001 | Grafana | admin/password |
| 8001 | Discovery |  |
| 8080 | Keycloak | admin/admin |
| 9001 | MinIO | minio/miniopwd |
| 9090 | Prometheus |  |
| 9411 | Zipkin |  |


## Screens
- /

| 홈 | 알림 |
|:---:|:---:|
|![home](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/home.png)|![notification](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/notification.png)|

| 좌측은 홈 화면 우측은 알림 메세지입니다.

새로운 메세지가 도착하면 현재 해당 채팅방에 있는지 여부를 확인하고 좌측 상단에서 알림을 보여줍니다.  
로그인 후 notification-service와 웹소켓을 연결하여 현 사용자가 온라인 상태임을 알리고 브라우저를 벗어나기 전에 오프라인 상태로 변경하여 현 사용자의 상태를 확인할 수 있도록 했습니다. 

- /channels/{channelId}

| 다이렉트 채팅 | 그룹 채팅 |
|:---:|:---:|
|![direct-channel](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/direct-channel.png)|![group-channel](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/group-channel.png)|

| 좌측은 다이렉트 채팅 우측은 그룹 채팅 화면입니다.

친구와 1:1 채팅 혹은 그룹 채팅을 검색하여 참가할 수 있습니다. 해당 채팅방을 나갈 경우 남은 참가자에게 Anonymous로 표시되도록 했습니다. 채팅은 chat-service로 POST를 통해 요청 후 새 메세지 타입의 알림 이벤트를 생성하여 참가자들에게 전달합니다. 수신 받은 참가자들은 해당 채팅방에 접속 중이면 read 요청을 통해 해당 메세지를 읽어오며 그렇지 않은 참가자는 읽지 않은 메세지 개수가 증가됩니다.

| 친구 검색 | 그룹 채팅 검색 |
|:---:|:---:|
|![search-friend](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/search-friend.png)|![search-group-channel](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/search-group-channel.png)|

| 좌측은 친구 검색 우측은 그룹 채팅 검색 화면입니다.

친구 검색 및 추가할 수 있으며, 다이렉트 채팅으로 이동할 경우 chat-service에서 해당 타입의 사용자와 친구가 존재하는 채널 정보를 수신하고 없으면 생성하여 이동합니다. 또한, 그룹 채널을 검색하여 참가할 수 있습니다.

- /profile, login

| 사용자 정보 | 로그인 |
|:---:|:---:|
|![profile](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/profile.png)|![login](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/login.png)|

| 좌측은 회원 정보를 변경할 수 있는 화면 우측은 로그인 화면입니다.

사용자 이미지, 상태 메세지와 같은 정보나 비밀번호를 변경할 수 있습니다.
사용자가 이미지를 변경할 시 image-service에서 아바타 변경 이벤트를 발생시키고 user-service에서 수신하여 사용자 변경 이벤트를 발생시킵니다. friend-service와 chat-service에서 해당 이벤트를 수신하여 변경된 사용자와 관련있는 사용자들을 찾고 각 서비스에서 알림 이벤트를 생성하고 notification-service에서 이를 수신하여 웹 소켓을 통해 전달합니다.  
로그인 화면은 Keycloak을 활용했으며 로그인 및 회원가입을 할 수 있습니다.

- Tracing, Logging and Metrics

| Zipkin | Grafana Loki |
|:---:|:---:|
|![zipkin](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/zipkin.png)|![loki-logs](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/loki-logs.png)|

| 좌측은 Zipkin 우측은 로그 확인할 수 있는 화면입니다.

Zipkin을 통해 마이크로서비스간의 트래픽을 확인할 수 있으며, 각 컨테이너에서 생성되는 로그들을 Fluent Bit을 통해 Loki로 푸쉬하여 Grafana에서 확인할 수 있습니다.

| Grafana JVM | Grafana SCG |
|:---:|:---:|
|![jvm-metrics](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/jvm-metrics.png)|![scg](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/scg.png)|

| 좌측은 JVM 우측은 Spring Cloud Gateway 대시보드 화면입니다.

Micrometer와 Prometheus를 통해 를 통해 수집된 Metrics를 Grafana로 확인할 수 있습니다.

## What i learned
- Spring Cloud
- Spring WebSocket
- Apache Kafka
- Keycloak
- Minio
- Github Actions
- Grafana

## What to add
- 채팅 참여 사용자 목록
- Test
- Swagger
- Keycloak event
- ...

## Library used
- [Vuetify](https://vuetifyjs.com)
- [Keycloak](https://keycloak.org)
- [Minio](https://min.io)
- [vue-keycloak-js](https://github.com/dsb-norge/vue-keycloak-js)
- [Micrometer](https://micrometer.io/)
- [Grafana](https://grafana.com/)
- [Prometheus](https://prometheus.io/)
- [Fluent-bit](https://fluentbit.io/)