package xyz.acevedosharp.epicmovelobackend.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import xyz.acevedosharp.epicmovelobackend.events.UpdateComponentsCopyEvent
import xyz.acevedosharp.epicmovelobackend.events.UpdateUsersCopyEvent
import xyz.acevedosharp.epicmovelobackend.model.Biciusuario
import xyz.acevedosharp.epicmovelobackend.model.Componente
import xyz.acevedosharp.epicmovelobackend.model.Empresa
import xyz.acevedosharp.epicmovelobackend.model.User
import xyz.acevedosharp.epicmovelobackend.restcontrollers.FacadeProxy

@Service
class ServiceFacade(
        val applicationEventPublisher: ApplicationEventPublisher,
        val empresaService: EmpresaService,
        val biciusuarioService: BiciusuarioService
): ApplicationListener<UpdateComponentsCopyEvent> {
    val componentes = arrayListOf<Componente>()

    init {
        updateComponentsCopy()
    }

    // make facadeProxy map componentes again
    fun dumbCache() {
        applicationEventPublisher.publishEvent(UpdateUsersCopyEvent(this))
    }

    fun agregarComponente(componente: Componente) {
        componentes.add(componente)
        dumbCache()
    }

    fun findById(id: Int): Componente? = componentes.find { (it as User).id == id}

    // forward to empresa service
    fun agregarEmpresa(empresa: Empresa) = empresaService.agregarEmpresa(empresa)
    fun actualizarEmpresa(empresa: Empresa): Boolean {
        val res = empresaService.actualizarEmpresa(empresa)
        if (res) dumbCache()
        return res
    }
    fun eliminarComposicion(id1: Int, id2: Int) = empresaService.eliminarComposicion(id1, id2)

    // forward to biciusuario service
    fun agregarBiciusuario(biciusuario: Biciusuario) = biciusuarioService.agregarBiciusuario(biciusuario)
    fun actualizarBiciusuario(biciusuario: Biciusuario): Boolean {
        val res = biciusuarioService.actualizarBiciusuario(biciusuario)
        if (res) dumbCache()
        return res
    }


    private fun updateComponentsCopy() {
        componentes.clear()
        componentes.addAll(empresaService.empresas)
        componentes.addAll(biciusuarioService.biciusuarios)
    }
    override fun onApplicationEvent(event: UpdateComponentsCopyEvent) {
        updateComponentsCopy()
    }
}