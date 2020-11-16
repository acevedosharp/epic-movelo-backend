package xyz.acevedosharp.epicmovelobackend.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import xyz.acevedosharp.epicmovelobackend.events.DeleteBiciusuarioEvent
import xyz.acevedosharp.epicmovelobackend.events.UpdateComponentsCopyEvent
import xyz.acevedosharp.epicmovelobackend.model.Biciusuario
import xyz.acevedosharp.epicmovelobackend.model.Empresa
import xyz.acevedosharp.epicmovelobackend.model.User

@Service
class EmpresaService(
        private val applicationEventPublisher: ApplicationEventPublisher
) {
    val empresas = arrayListOf<Empresa>()

    fun agregarEmpresa(empresa: Empresa): Boolean {
        // enforce rules
        empresas.forEach {
            when {
                it.nit == empresa.nit -> return false
                it.nombre == empresa.nombre -> return false
                it.direccion == empresa.direccion -> return false
                it.telefono == empresa.telefono -> return false
            }
        }

        empresas.add(empresa)
        return true
    }

    fun consultarEmpresa(id: Int) = empresas.find { it.id == id }

    fun actualizarEmpresa(empresa: Empresa): Boolean {
        val realEmpresa: Empresa = consultarEmpresa(empresa.id) ?: return false

        realEmpresa.correo = empresa.correo
        realEmpresa.contrasena = empresa.contrasena
        realEmpresa.nit = empresa.nit
        realEmpresa.nombre = empresa.nombre
        realEmpresa.direccion = empresa.direccion
        realEmpresa.telefono = empresa.telefono

        return true
    }

    fun eliminarEmpresa(id: Int): Boolean {
        val empresa = empresas.find { it.id == id}

        if (empresa != null) {
            // eliminate children
            empresa.children.forEach {
                if (it is Empresa) {
                    eliminarEmpresaRec(it.id)
                } else { // biciusuario
                    emitDeleteBiciusuario((it as Biciusuario).id)
                }
            }
            // remove component
            empresas.remove(empresa)
            emitDataModified()
            return true
        } else {
            return false
        }
    }

    // recursive part so that UpdateComponentsCopyEvent isn't emitted for every recursion
    private fun eliminarEmpresaRec(id: Int) {
        val empresa = empresas.find { it.id == id}

        if (empresa != null) {
            // eliminate children
            empresa.children.forEach {
                if (it is Empresa) {
                    eliminarEmpresaRec(it.id)
                } else { // biciusuario
                    emitDeleteBiciusuario((it as Biciusuario).id)
                }
            }
            // remove component
            empresas.remove(empresa)
        }
    }

    fun eliminarComposicion(id1: Int, id2: Int): Boolean {
        val comp1 = consultarEmpresa(id1) ?: return false

        return comp1.children.removeAll { (it as User).id == id2 }
    }

    private fun emitDataModified() {
        applicationEventPublisher.publishEvent(UpdateComponentsCopyEvent(this))
    }

    private fun emitDeleteBiciusuario(id: Int) {
        applicationEventPublisher.publishEvent(DeleteBiciusuarioEvent(this, id))
    }
}