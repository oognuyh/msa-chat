## MSA Chat ![workflow](https://github.com/oognuyh/msa-chat/actions/workflows/backend.yml/badge.svg) ![workflow](https://github.com/oognuyh/msa-chat/actions/workflows/frontend.yml/badge.svg)
<img src="https://img.shields.io/badge/Vue.js-4FC08D.svg?&style=for-the-badge&logo=Vue.js&logoColor=white"> <img src="https://img.shields.io/badge/Spring Boot-6DB33F.svg?&style=for-the-badge&logo=SpringBoot&logoColor=white"> <img src="https://img.shields.io/badge/MongoDB-47A248.svg?&style=for-the-badge&logo=MongoDB&logoColor=white"> <img src="https://img.shields.io/badge/NGINX-009639.svg?&style=for-the-badge&logo=NGINX&logoColor=white">  <img src="https://img.shields.io/badge/GitHub Actions-2088FF.svg?&style=for-the-badge&logo=GitHubActions&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED.svg?&style=for-the-badge&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/Visual Studio Code-007ACC.svg?&style=for-the-badge&logo=VisualStudioCode&logoColor=white"> <img src="https://img.shields.io/badge/Vuetify-1867C0.svg?&style=for-the-badge&logo=Vuetify&logoColor=white"> <img src="https://img.shields.io/badge/Apache Kafka-231F20.svg?&style=for-the-badge&logo=ApacheKafka&logoColor=white"> 

> 스프링 클라우드를 활용한 마이크로서비스 채팅 프로젝트

Spring Cloud, WebSocket, Vue, Kafka, Keycloak, Minio를 활용하여 구현한 채팅 프로젝트입니다. 해당 프로젝트는 Discovery / Gateway / User / Friend / Image / Chat / Notification / Config / Auth (Keycloak)으로 구성하였고 서비스간 통신은 Open Feign / Kafka를 활용했습니다. 

## Features
- 다이렉트/그룹 채팅
- 사용자/그룹 채팅 검색, 사용자 상태 변경
- 로그인/아웃, 회원가입, 회원정보 변경
- 새 메세지 알림

## Skills
- Backend
    - Java, Kotlin, Spring Boot, Spring WebSocket, Spring Cloud, Spring Data JPA, Spring Security, Gradle, Keycloak, Minio ..
- Frontend
    - Vue.js, Vuetify, Vuex, Vue-Router, Axios, Nginx
- Database
    - MongoDB, MySQL, PostgresDB
- CI/CD
    - Github Actions
- Tool
    - Visual Studio Code, Intellij

## Systen architecture
![system-architecture](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/system-architecture.png)  
Github에 푸쉬하면 Github Actions를 활용하여 빌드 후 이미지를 Docker Hub에 푸쉬하도록 구성하였습니다. Docker Hub에 푸쉬된 이미지를 Docker Compose를 활용하여 다중 어플리케이션 환경을 구축하였습니다.  
각 서비스들은 랜덤포트로 설정했으며 서비스 간의 통신이 필요한 경우 Kafka와 Open Feign을 활용했습니다.

## ERD
![erd](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/erd.png)  

## Screen flow
![screen-flow](https://raw.githubusercontent.com/oognuyh/msa-chat/master/images/system-architecture.png)  

## Installation and Getting Started
```
git clone https://github.com/oognuyh/msa-chat.git
cd msa-chat
docker-compose up --build
```

| Port | Description |
|:-:|:-:|
| 3000 | Frontend web |
| 8001 | Discovery Console |
| 8080 | Keycloak Console |
| 9001 | MinIO Console |

## Screens
- /

- /channels/{channelId}

- /profile

## What i learned
- Spring Cloud
- Spring WebSocket
- Apache Kafka
- Keycloak
- Minio
- Github Actions

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
