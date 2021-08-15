package co.nilin.opex.port.api.binance.config

import co.nilin.opex.port.api.binance.security.AuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.util.Base64Utils
import org.springframework.util.FileCopyUtils
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec

@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http.csrf().disable()
            .cors().and()
            .authorizeExchange()
            .pathMatchers("/hello").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/swagger-ui/**").permitAll()
            .pathMatchers("/swagger-resources/**").permitAll()
            .pathMatchers("/v2/api-docs").permitAll()
            .pathMatchers("/**").hasAuthority("SCOPE_trust")
            .anyExchange().authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(AuthenticationConverter())
        return http.build()
    }

    @Bean
    @Throws(Exception::class)
    fun reactiveJwtDecoder(): ReactiveJwtDecoder? {
        val resource: Resource = ClassPathResource("/public.cert")
        val publicKey = String(FileCopyUtils.copyToByteArray(resource.inputStream))
            .replace("\r", "")
            .replace("-----BEGIN PUBLIC KEY-----\n", "")
            .replace("\n-----END PUBLIC KEY-----", "")
        val spec = X509EncodedKeySpec(Base64Utils.decodeFromString(publicKey))
        val kf = KeyFactory.getInstance("RSA")
        return NimbusReactiveJwtDecoder(kf.generatePublic(spec) as RSAPublicKey)
    }
}
