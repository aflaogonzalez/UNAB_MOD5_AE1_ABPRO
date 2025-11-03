package cl.lte.ae1_abpro

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cl.lte.ae1_abpro.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var listaEventos = mutableListOf<Evento>()

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
        actualizarListaEventos() // Mostrar estado inicial
    }

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

        binding.sortButton.setOnClickListener {
            val eventosOrdenados = listaEventos.sortedBy { it.fecha }
            actualizarListaEventos(eventosOrdenados)
        }

        binding.clearButton.setOnClickListener {
            binding.filterEditText.text.clear()
            actualizarListaEventos() // Muestra la lista original
        }

        binding.closeAppButton.setOnClickListener {
            finish() // Cierra la actividad actual y, por tanto, la aplicación.
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // El mes se basa en 0, por lo que sumamos 1
                val formattedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.dateEditText.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    /**
     * Actualiza el TextView que muestra la lista de eventos.
     * @param eventos La lista de eventos a mostrar. Por defecto, muestra la lista principal.
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

    private fun limpiarCampos() {
        binding.apply {
            titleEditText.text.clear()
            dateEditText.text.clear()
            descriptionEditText.text.clear()
        }
    }
}
