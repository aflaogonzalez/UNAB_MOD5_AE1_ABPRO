package cl.lte.ae1_abpro

/**
 * Data class para representar un Evento en la agenda.
 * El uso de 'data class' nos genera automáticamente los métodos:
 * toString(), equals(), hashCode() y copy().
 *
 * @property id Identificador único del evento.
 * @property titulo El título del evento.
 * @property fecha La fecha del evento (como String por simplicidad).
 * @property descripcion Una descripción opcional del evento.
 */
data class Evento(
    val id: String,
    val titulo: String,
    val fecha: String,
    val descripcion: String? // Esta propiedad puede ser nula (Null Safety).
) {

    /**
     * El Companion Object es similar a los miembros estáticos en Java.
     * Lo usamos aquí para crear una función "fábrica" que nos ayude
     * a crear instancias de Evento, asignando un ID único automáticamente.
     */
    companion object {
        /**
         * Crea una nueva instancia de Evento con un ID único.
         * Llama a nuestra clase Java `IdGenerator` para demostrar interoperabilidad.
         */
        fun crearEvento(titulo: String, fecha: String, descripcion: String?): Evento {
            val id = IdGenerator.generateId() // Interoperabilidad con Java
            return Evento(id, titulo, fecha, descripcion)
        }
    }
}