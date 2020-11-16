package xyz.acevedosharp.epicmovelobackend.model

class Empresa(
        id: Int,
        correo: String,
        password: String,
        var nit: String,
        var nombre: String,
        var direccion: String,
        var telefono: String
) : User(id, correo, password), Componente {
    val children = arrayListOf<Componente>()

    override fun mostrarInformacion() = println(this.toString())

    fun addComponente(componente: Componente) = children.add(componente)

    fun deleteComponente(id: Int) = children.removeIf { (it as User).id == id }
}