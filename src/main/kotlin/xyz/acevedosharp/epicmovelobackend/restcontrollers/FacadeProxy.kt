package xyz.acevedosharp.epicmovelobackend.restcontrollers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.acevedosharp.epicmovelobackend.*
import xyz.acevedosharp.epicmovelobackend.model.Biciusuario
import xyz.acevedosharp.epicmovelobackend.model.Componente
import xyz.acevedosharp.epicmovelobackend.model.User
import xyz.acevedosharp.epicmovelobackend.services.ServiceFacade
import java.lang.RuntimeException
import java.util.*

@RestController
class FacadeProxy(private val passwordEncoder: BCryptPasswordEncoder, private val serviceFacade: ServiceFacade) {
    var idCounter = 0
    val usuarios = arrayListOf<User>()

    @PostMapping("/registrarBiciusuario")
    fun singUp(
            @RequestParam("correo") correo: String,
            @RequestParam("contrasena") contrasena: String,
            @RequestParam("cc") cc: String,
            @RequestParam("nombre") nombre: String,
            @RequestParam("direccion") direccion: String,
            @RequestParam("telefono") telefono: String
    ): StringResponse {
        val pHash = passwordEncoder.encode(contrasena)

        if (usuarios.any { it.correo == correo }) return StringResponse("Este correo ya está en uso.")

        val biciusuario = Biciusuario(getId(), correo, pHash, cc, nombre, direccion, telefono)

        serviceFacade.componentes.add(biciusuario)

        usuarios.add(User(biciusuario.id, biciusuario.correo, biciusuario.password))

        return StringResponse("Registrado con éxito.")
    }

    @PostMapping("/login")
    fun login(
            @RequestParam("correo") correo: String,
            @RequestParam("contrasena") contrasena: String
    ): StringResponse {
        val usuario = usuarios.find { it.correo == correo }
        if (usuario != null) {
            if (passwordEncoder.matches(contrasena, usuario.password)) {
                return StringResponse(generateToken(usuario))
            }
        }
        return StringResponse("Credenciales incorrectas.")
    }

    @GetMapping("/all")
    fun all() = usuarios

    @GetMapping("/")
    fun home() = StringResponse("Hey yooo")

    private fun generateToken(user: User): String {
        return JWT.create()
                .withSubject(user.correo)
                .withExpiresAt(Date(System.currentTimeMillis() + JWT_DURATION))
                .sign(Algorithm.HMAC512(SECRET_KEY.toByteArray()))
    }

    private fun getId() = idCounter++


}

class StringResponse(val message: String)
