package xyz.acevedosharp.epicmovelobackend.model

class Biciusuario(
        id: Int,
        correo: String,
        contrasena: String,
        var cc: String,
        var nombre: String,
        var direccion: String,
        var telefono: String
) : User(id, correo, contrasena), Componente {
    val bicicletas = arrayListOf<Bicicleta>()
    val arbolesContribuidos = arrayListOf<PuntoGeografico>()
    var metrosNoPlantados = 0
        private set
    var metrosRecorridos = 0
        private set
    var huellaCarbonoAcumulada = 0.0
        private set

    fun addBicicleta(bicicleta: Bicicleta) = bicicletas.add(bicicleta)

    fun addToHuella(cantidad: Double): Double {
        huellaCarbonoAcumulada += cantidad
        return huellaCarbonoAcumulada
    }

    fun addMetrosRecorridos(cantidad: Int): Int {
        metrosRecorridos += cantidad
        return metrosRecorridos
    }

    fun addMetrosNoPlantados(cantidad: Int): Int {
        metrosNoPlantados += cantidad
        return metrosNoPlantados
    }

    fun reiniciarMetrosNoPlantados() {
        metrosNoPlantados = 0
    }

    fun addArbol(arbol: PuntoGeografico) = arbolesContribuidos.add(arbol)

    override fun mostrarInformacion() = println(this.toString())
}
