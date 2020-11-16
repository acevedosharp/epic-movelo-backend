package xyz.acevedosharp.epicmovelobackend.services

import org.springframework.stereotype.Service
import xyz.acevedosharp.epicmovelobackend.model.Biciusuario
import xyz.acevedosharp.epicmovelobackend.model.Empresa

@Service
class BiciusuarioService(
        val serviceFacade: ServiceFacade
) {
    fun agregarBiciusuario(biciusuario: Biciusuario): Boolean {
        serviceFacade.componentes.forEach {
            if (it is Biciusuario) {
                when {
                    it.cc == biciusuario.cc -> return false
                    it.nombre == biciusuario.nombre -> return false
                    it.direccion == biciusuario.direccion -> return false
                    it.telefono == biciusuario.telefono -> return false
                }
            }
        }

        serviceFacade.agregarComponente(biciusuario)
        return true
    }

    fun consultarBiciusuario(id: Int) = serviceFacade.findById(id) as Biciusuario?

    fun actualizarBiciusuario(biciusuario: Biciusuario): Boolean {
        val realBiciusuario: Biciusuario = consultarBiciusuario(biciusuario.id) ?: return false

        realBiciusuario.correo = biciusuario.correo
        realBiciusuario.contrasena = biciusuario.contrasena
        realBiciusuario.cc = biciusuario.cc
        realBiciusuario.nombre = biciusuario.nombre
        realBiciusuario.direccion = biciusuario.direccion
        realBiciusuario.telefono = biciusuario.telefono

        return true
    }
}