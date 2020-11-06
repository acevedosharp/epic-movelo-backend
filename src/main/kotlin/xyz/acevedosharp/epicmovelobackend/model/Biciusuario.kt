package xyz.acevedosharp.epicmovelobackend.model

class Biciusuario(
        id: Int,
        correo: String,
        password: String,
        val cc: String,
        val nombre: String,
        val direccion: String,
        val telefono: String
) : User(id, correo, password), Componente {
    val bicicletas: List<Bicicleta> = arrayListOf()
    val arbolesContribuidos: List<PuntoGeografico> = arrayListOf()
    val metrosNoPlantados = 0
    val metrosRecorridos = 0
    val huellaCarbonoAcumulada = 0.0

    override fun mostrarInformacion() = println(this.toString())
}
