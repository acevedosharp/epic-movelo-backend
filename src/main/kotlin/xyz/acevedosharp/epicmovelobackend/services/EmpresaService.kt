package xyz.acevedosharp.epicmovelobackend.services

import org.springframework.stereotype.Service
import xyz.acevedosharp.epicmovelobackend.model.Empresa
import xyz.acevedosharp.epicmovelobackend.model.User

@Service
class EmpresaService(
        val serviceFacade: ServiceFacade
) {
    fun agregarEmpresa(empresa: Empresa): Boolean {
        // enforce rules
        serviceFacade.componentes.forEach {
            if (it is Empresa) {
                when {
                    it.nit == empresa.nit -> return false
                    it.nombre == empresa.nombre -> return false
                    it.direccion == empresa.direccion -> return false
                    it.telefono == empresa.telefono -> return false
                }
            }
        }

        serviceFacade.agregarComponente(empresa)
        return true
    }

    fun consultarEmpresa(id: Int) = serviceFacade.findById(id) as Empresa?

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

    fun eliminarComposicion(id1: Int, id2: Int): Boolean {
        val comp1 = consultarEmpresa(id1) ?: return false

        return comp1.children.removeAll { (it as User).id == id2 }
    }
}