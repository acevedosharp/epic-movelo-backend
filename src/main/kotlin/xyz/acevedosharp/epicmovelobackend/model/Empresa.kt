package xyz.acevedosharp.epicmovelobackend.model

class Empresa(
        id: Int,
        correo: String,
        password: String,
        val nit: String,
        val nombre: String,
        val direccion: String,
        val telefono: String
) : User(id, correo, password), Componente {
    val children: List<Componente> = arrayListOf()

    override fun mostrarInformacion() = println(this.toString())
}