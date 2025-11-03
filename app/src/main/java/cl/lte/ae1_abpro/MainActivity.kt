package cl.lte.ae1_abpro

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cl.lte.ae1_abpro.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var listaEventos = mutableListOf<Evento>()

    /**
     * Función principal que se ejecuta al crear la pantalla.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        actualizarListaEventos()
    }

    /**
     * Agrupa la configuración de todos los listeners (eventos de clic) de la app.
     */
    private fun setupListeners() {
        binding.dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveEventButton.setOnClickListener {
            val titulo = binding.titleEditText.text.toString()
            val fecha = binding.dateEditText.text.toString()
            val descripcion = binding.descriptionEditText.text.toString()

            if (titulo.isNotEmpty() && fecha.isNotEmpty()) {
                val nuevoEvento = Evento.crearEvento(titulo, fecha, descripcion.takeIf { it.isNotEmpty() })
                listaEventos.add(nuevoEvento)
                actualizarListaEventos()
                limpiarCampos()
            }
        }

        binding.filterButton.setOnClickListener {
            val filtro = binding.filterEditText.text.toString()
            val eventosFiltrados = listaEventos.filter { evento ->
                evento.titulo.contains(filtro, ignoreCase = true)
            }
            actualizarListaEventos(eventosFiltrados)
        }

        // Al hacer clic en Ordenar, ahora se muestra un diálogo de selección.
        binding.sortButton.setOnClickListener {
            showSortDialog()
        }

        binding.clearButton.setOnClickListener {
            binding.filterEditText.text.clear()
            actualizarListaEventos()
        }

        binding.closeAppButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Muestra un diálogo para que el usuario elija cómo ordenar la lista.
     */
    private fun showSortDialog() {
        val options = arrayOf("Por Fecha", "Por Título (A-Z)")

        AlertDialog.Builder(this)
            .setTitle("Ordenar eventos por:")
            .setItems(options) { _, which ->
                val sortedList = when (which) {
                    0 -> listaEventos.sortedBy { it.fecha }      // Opción 0: Ordenar por fecha
                    1 -> listaEventos.sortedBy { it.titulo }      // Opción 1: Ordenar por título
                    else -> listaEventos // Por si acaso, devuelve la lista sin cambios
                }
                actualizarListaEventos(sortedList)
            }
            .show()
    }

    /**
     * Muestra un diálogo con un calendario para seleccionar una fecha.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.dateEditText.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    /**
     * Actualiza el TextView en la pantalla para mostrar la lista de eventos actual.
     * @param eventos La lista que se va a mostrar. Por defecto, usa la lista principal.
     */
    private fun actualizarListaEventos(eventos: List<Evento> = listaEventos) {
        binding.eventsListTextView.run {
            if (eventos.isEmpty()) {
                text = "No hay eventos que coincidan."
            } else {
                val textoEventos = eventos.joinToString(separator = "\n\n") { evento ->
                    "Título: ${evento.titulo}\nFecha: ${evento.fecha}\nDescripción: ${evento.descripcion ?: "Sin descripción"}"
                }
                text = textoEventos
            }
        }
    }

    /**
     * Limpia los campos de texto del formulario.
     */
    private fun limpiarCampos() {
        binding.apply {
            titleEditText.text.clear()
            dateEditText.text.clear()
            descriptionEditText.text.clear()
        }
    }
}
