package co.nilin.opex.auth.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication(exclude = [LiquibaseAutoConfiguration::class])
@ComponentScan(basePackages = ["co.nilin.opex.auth.gateway"])
@EnableConfigurationProperties
class KeycloakGatewayApp

fun main(args: Array<String>) {
    System.setProperty("keycloak.theme.dir", "/themes")
    ApplicationContextHolder.setCurrentContext(runApplication<KeycloakGatewayApp>(*args))
}