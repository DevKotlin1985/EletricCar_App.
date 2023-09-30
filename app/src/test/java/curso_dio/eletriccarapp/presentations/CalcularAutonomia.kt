package curso_dio.eletriccarapp.presentations

import android.content.Context
import android.content.Intent
import android.inputmethodservice.Keyboard.Row
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import curso_dio.eletriccarapp.R
import java.lang.AssertionError
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class CalcularAutonomiaActivity : AppCompatActivity() {

    lateinit var preco: EditText
    lateinit var btnCalcular: Button
    lateinit var kmPercorrido: EditText
    lateinit var calcAutonomia: TextView
    lateinit var btnClose: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calcular_autonomia)
        setupView()
        setupListeners()
        getSharedPref()
    }

    fun setupView() {
        preco = findViewById(R.id.et_Preço_Kwh)
        kmPercorrido = findViewById(R.id.et_Km_Percorrido)
        btnCalcular = findViewById(R.id.btn_calcular)
        calcAutonomia = findViewById(R.id.tv_autonomia_value)
        btnCalcular = findViewById(R.id.btn_calcular)
        btnClose = findViewById(R.id.iv_close)
        setupCachedResult()
    }

    private fun setupCachedResult() {
        val valorCalculado = getSharedPref()
        calcAutonomia.text = valorCalculado.toString()
    }

    fun setupListeners() {
        btnCalcular.setOnClickListener {
            calcular()
        }
        btnClose.setOnClickListener {
            finish()
        }
    }

    fun calcular() {
        val preco = preco.text.toString().toFloat()
        val km = kmPercorrido.text.toString().toFloat()
        val result = preco / km

        calcAutonomia.text = result.toString()
        saveSharedPref(result)

    }

    fun saveSharedPref(resultado: Float){
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putFloat(getString(R.string.saved_calc), resultado)
            apply()
        }
    }

    fun getSharedPref(): Float {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getFloat(getString(R.string.saved_calc),0.0f)
    }
}


