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
    private enum class SortCriteria { DATE, TITLE }

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
            // Lee los datos de los campos de texto y quita espacios en blanco.
            val tituloRaw = binding.titleEditText.text.toString().trim()
            val fecha = binding.dateEditText.text.toString()
            val descripcion = binding.descriptionEditText.text.toString().trim()

            if (tituloRaw.isNotEmpty() && fecha.isNotEmpty()) {
                // **Corrección para el ordenamiento alfabético**
                // Se formatea el título a "Tipo Capital" (ej: "Hola mundo") para asegurar
                // que el ordenamiento funcione correctamente sin importar cómo se escribió.
                val titulo = tituloRaw.lowercase().replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

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

        binding.sortButton.setOnClickListener {
            showSortCriteriaDialog()
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
     * Diálogo 1: Pregunta al usuario QUÉ quiere ordenar.
     */
    private fun showSortCriteriaDialog() {
        val options = arrayOf("Por Fecha", "Por Título (A-Z)")

        AlertDialog.Builder(this)
            .setTitle("Ordenar por:")
            .setItems(options) { _, which ->
                val criteria = if (which == 0) SortCriteria.DATE else SortCriteria.TITLE
                showSortOrderDialog(criteria)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Diálogo 2: Pregunta al usuario CÓMO quiere ordenar (ascendente o descendente).
     * @param criteria El criterio (Fecha o Título) seleccionado en el diálogo anterior.
     */
    private fun showSortOrderDialog(criteria: SortCriteria) {
        val orderOptions = arrayOf("Ascendente", "Descendente")

        AlertDialog.Builder(this)
            .setTitle("Seleccionar orden:")
            .setItems(orderOptions) { _, which ->
                val isAscending = which == 0

                val sortedList = when (criteria) {
                    SortCriteria.DATE -> {
                        if (isAscending) listaEventos.sortedBy { it.fecha }
                        else listaEventos.sortedByDescending { it.fecha }
                    }
                    SortCriteria.TITLE -> {
                        if (isAscending) listaEventos.sortedBy { it.titulo }
                        else listaEventos.sortedByDescending { it.titulo }
                    }
                }
                actualizarListaEventos(sortedList)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

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

    private fun limpiarCampos() {
        binding.apply {
            titleEditText.text.clear()
            dateEditText.text.clear()
            descriptionEditText.text.clear()
        }
    }
}
