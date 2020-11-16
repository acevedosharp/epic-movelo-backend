package xyz.acevedosharp.epicmovelobackend.restcontrollers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.acevedosharp.epicmovelobackend.*
import xyz.acevedosharp.epicmovelobackend.events.UpdateUsersCopyEvent
import xyz.acevedosharp.epicmovelobackend.model.Biciusuario
import xyz.acevedosharp.epicmovelobackend.model.Componente
import xyz.acevedosharp.epicmovelobackend.model.Empresa
import xyz.acevedosharp.epicmovelobackend.model.User
import xyz.acevedosharp.epicmovelobackend.services.ServiceFacade
import java.lang.RuntimeException
import java.util.*

@RestController
class FacadeProxy(
        private val passwordEncoder: BCryptPasswordEncoder,
        private val serviceFacade: ServiceFacade
): ApplicationListener<UpdateUsersCopyEvent> {

    var idCounter = 0
    lateinit var usuarios: List<User>

    init {
        updateUsersCopy()
    }

    @PostMapping("/registrarBiciusuario")
    fun singUpBiciusuario(
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

        serviceFacade.agregarComponente(biciusuario)

        return StringResponse("Registrado con éxito.")
    }

    @PostMapping("/registrarEmpresa")
    fun signUpEmpresa(
            @RequestParam("correo") correo: String,
            @RequestParam("contrasena") contrasena: String,
            @RequestParam("nit") nit: String,
            @RequestParam("nombre") nombre: String,
            @RequestParam("direccion") direccion: String,
            @RequestParam("telefono") telefono: String
    ): StringResponse {
        val pHash = passwordEncoder.encode(contrasena)

        if (usuarios.any { it.correo == correo }) return StringResponse("Este correo ya está en uso.")

        val empresa = Empresa(getId(), correo, pHash, nit, nombre, direccion, telefono)

        serviceFacade.agregarComponente(empresa)

        return StringResponse("Registrado con éxito.")
    }

    @PostMapping("/login")
    fun login(
            @RequestParam("correo") correo: String,
            @RequestParam("contrasena") contrasena: String
    ): StringResponse {
        val usuario = usuarios.find { it.correo == correo }
        if (usuario != null) {
            if (passwordEncoder.matches(contrasena, usuario.contrasena)) {
                return StringResponse(generateToken(usuario))
            }
        }
        return StringResponse("Credenciales incorrectas.")
    }

    @GetMapping("/allUsuarios")
    fun all() = usuarios

    @GetMapping("/allComponentes")
    fun allComponentes() = serviceFacade.componentes

    @GetMapping("/allBiciusuarios")
    fun allBiciusuarios() = serviceFacade.componentes.filter { it is Biciusuario }.map { it as Biciusuario }

    @GetMapping("/allEmpresas")
    fun allEmpresas() = serviceFacade.componentes.filter { it is Empresa }.map { it as Empresa }

    @GetMapping("/")
    fun home() = StringResponse("Hey yooo")

    // extra functionality goes here
    @GetMapping("/consultarHuellaDeCarbono")
    fun consultarHuellaDeCarbono(
            @RequestParam("token") token: String
    ): StringResponse {
        // find componente from token
        val correo = JWT.decode(token).subject
        val usuario = usuarios.find { it.correo == correo }!! // can't fail
        val componente = serviceFacade.findById(usuario.id)!!


        if (componente is Biciusuario) {
            return StringResponse(componente.huellaCarbonoAcumulada.toString())
        } else {
            return StringResponse("Sólo válido para biciusuarios")
        }

    }

    @PostMapping("/enviarDistanciaRecorrido")
    fun enviarDistanciaRecorrido(
            @RequestParam("token") token: String,
            @RequestParam("distancia") distancia: Int
    ): StringResponse {
        // find componente from token
        val correo = JWT.decode(token).subject
        val usuario = usuarios.find { it.correo == correo }!! // can't fail
        val componente = serviceFacade.findById(usuario.id)!!

        if (componente is Biciusuario) {
            // sumar distancia
            componente.addMetrosRecorridos(distancia)
            componente.addMetrosNoPlantados(distancia)

            // calcular huella para recorrido
            val huella = 0.1 * distancia

            // sumar huella de carbono
            componente.addToHuella(huella)

            return StringResponse(componente.metrosRecorridos.toString()) // distancia total
        } else {
            return StringResponse("Sólo válido para biciusuarios")
        }
    }

    final fun updateUsersCopy() {
        usuarios = serviceFacade.componentes.map {
            it as User
        }
    }

    private fun generateToken(user: User): String {
        return JWT.create()
                .withSubject(user.correo)
                .withExpiresAt(Date(System.currentTimeMillis() + JWT_DURATION))
                .sign(Algorithm.HMAC512(SECRET_KEY.toByteArray()))
    }

    private fun getId() = idCounter++

    override fun onApplicationEvent(event: UpdateUsersCopyEvent) {
        updateUsersCopy()
    }
}

class StringResponse(val message: String)
