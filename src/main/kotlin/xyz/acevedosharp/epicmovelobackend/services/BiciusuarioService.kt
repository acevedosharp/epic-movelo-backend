package xyz.acevedosharp.epicmovelobackend.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import xyz.acevedosharp.epicmovelobackend.events.DeleteBiciusuarioEvent
import xyz.acevedosharp.epicmovelobackend.events.UpdateComponentsCopyEvent
import xyz.acevedosharp.epicmovelobackend.model.Biciusuario

@Service
class BiciusuarioService(
        private val applicationEventPublisher: ApplicationEventPublisher
): ApplicationListener<DeleteBiciusuarioEvent> {
    val biciusuarios = arrayListOf<Biciusuario>()

    fun agregarBiciusuario(biciusuario: Biciusuario): Boolean {
        biciusuarios.forEach {
            when {
                it.cc == biciusuario.cc -> return false
                it.nombre == biciusuario.nombre -> return false
                it.direccion == biciusuario.direccion -> return false
                it.telefono == biciusuario.telefono -> return false
            }
        }

        biciusuarios.add(biciusuario)
        emitDataModified()
        return true
    }

    fun consultarBiciusuario(id: Int) = biciusuarios.find { it.id == id }

    fun actualizarBiciusuario(biciusuario: Biciusuario): Boolean {
        val realBiciusuario: Biciusuario = consultarBiciusuario(biciusuario.id) ?: return false

        realBiciusuario.correo = biciusuario.correo
        realBiciusuario.contrasena = biciusuario.contrasena
        realBiciusuario.cc = biciusuario.cc
        realBiciusuario.nombre = biciusuario.nombre
        realBiciusuario.direccion = biciusuario.direccion
        realBiciusuario.telefono = biciusuario.telefono

        emitDataModified()
        return true
    }

    fun eliminarBiciusuario(id: Int): Boolean {
        val res = biciusuarios.removeIf { it.id == id }
        if (res) emitDataModified()
        return res
    }

    private fun emitDataModified() {
        applicationEventPublisher.publishEvent(UpdateComponentsCopyEvent(this))
    }

    override fun onApplicationEvent(event: DeleteBiciusuarioEvent) {
        // don't use eliminarBiciusuario because the emitter of this event will emit a UpdateComponentsCopyEvent
        biciusuarios.removeIf { it.id == event.id }
    }
}