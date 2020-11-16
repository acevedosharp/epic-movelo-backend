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

@Suppress("LeakingThis")
@Service
class ServiceFacade(
        val applicationEventPublisher: ApplicationEventPublisher,
        val empresaService: EmpresaService,
        val biciusuarioService: BiciusuarioService
): ApplicationListener<UpdateComponentsCopyEvent> {
    final val componentes = arrayListOf<Componente>()

    init {
        componentes.clear()
        componentes.addAll(empresaService.empresas)
        componentes.addAll(biciusuarioService.biciusuarios)
    }

    // make facadeProxy map componentes again
    fun emitUpdateUsersCopy() {
        applicationEventPublisher.publishEvent(UpdateUsersCopyEvent(this))
    }

    fun agregarComponente(componente: Componente): Boolean {
        val res: Boolean
        if (componente is Empresa) {
            res = empresaService.agregarEmpresa(componente)
        } else { // biciusuario
            res = biciusuarioService.agregarBiciusuario(componente as Biciusuario)
        }
        return res
    }

    fun actualizarComponente(componente: Componente): Boolean {
        val res: Boolean
        if (componente is Empresa) {
            res = empresaService.actualizarEmpresa(componente)
        } else { // biciusuario
            res = biciusuarioService.actualizarBiciusuario(componente as Biciusuario)
        }
        emitUpdateUsersCopy()
        return res
    }

    fun findById(id: Int): Componente? = componentes.find { (it as User).id == id}

    // forward to empresa service
    fun eliminarComposicion(id1: Int, id2: Int) = empresaService.eliminarComposicion(id1, id2)


    private fun updateComponentsCopy() {
        componentes.clear()
        componentes.addAll(empresaService.empresas)
        componentes.addAll(biciusuarioService.biciusuarios)
        emitUpdateUsersCopy()
    }
    override fun onApplicationEvent(event: UpdateComponentsCopyEvent) {
        updateComponentsCopy()
    }
}