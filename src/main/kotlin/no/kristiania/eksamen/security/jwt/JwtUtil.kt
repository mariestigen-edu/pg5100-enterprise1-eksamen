package no.kristiania.eksamen.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.userdetails.User
import java.util.*

object JwtUtil {

    private const val SECRET = "i_am_totally_not_storing_a_secret_here_promise_look_in_the_other_house"

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun createToken(user: User, issuer: String) : String {
        return JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 10 * 60 * 1000))
            .withIssuer(issuer)
            .withClaim("authorities", user.authorities.map { it.authority })
            .sign(algorithm)
    }

    fun decodeToken(token: String): DecodedJWT {
        val jwtVerifier = JWT.require(algorithm).build()
        return jwtVerifier.verify(token)
    }
}