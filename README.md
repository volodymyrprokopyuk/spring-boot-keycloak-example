# Spring Boot Keycloak Web Application Example

1. Create Realm: WebApplication (container for Users / Roles and Clients)
1. Create Clients:
    1. ApiClient
        1. Redirect Root URL: http://localhost:8081/api
        1. Access Type: confidential
    1. WebClient
        1. Redirect Root URL: http://localhost:8082
        1. Access Type: public
1. Create Roles: ProductReader, CustomerReader
1. Create Users: vlad, svit
1. Assign Roles to Users: vlad < ProductReader, svit < CustomerReader
