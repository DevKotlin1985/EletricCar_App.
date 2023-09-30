package curso_dio.eletriccarapp.presentations

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import curso_dio.eletriccarapp.R
import curso_dio.eletriccarapp.data.CarFactory
import curso_dio.eletriccarapp.data.local.CarRepository
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_BATERIA
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_POTENCIA
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_PRECO
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_RECARGA
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_URL_PHOTO
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.TABLE_NAME
import curso_dio.eletriccarapp.data.local.CarsDbHelper
import curso_dio.eletriccarapp.domain.Carro
import curso_dio.eletriccarapp.presentations.adapter.Car_Adapter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.ResponseCache
import java.net.URL
import javax.security.auth.callback.Callback

// fragmento para retornar o layout que for preciso
class CarFragment : Fragment() {
    lateinit var listaCarros: RecyclerView
    lateinit var progress: ProgressBar
    lateinit var ivEmpityState:ImageView
    lateinit var tv_no_wifi:TextView
    lateinit var carsApi: CarsApi


    var carrosArray: ArrayList<Carro> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.car_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRetrofit()
        setupView(view)
        checkForInternet(context)
        callService()

    }

    override fun onResume() {
        super.onResume()
        if (checkForInternet(context)){
            //callService() //outra forma de chamar serviço
            getAllcars()
        }else{
            empityState()
        }
    }

    fun setupRetrofit(){
        val retrofit = Retrofit.Builder().baseUrl("https://igorbag.github.io/cars-api/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        carsApi = retrofit.create(CarsApi::class.java)

    }

    fun getAllcars(){
        carsApi.getAllCars().enqueue(object : Callback<List<Carro>>{
            override fun onResponse(call: Call<List<Carro>>, response: Response<List<Carro>>) {
                if (response.isSuccessful){
                    progress.isVisible = false
                    listaCarros.isVisible = true
                    ivEmpityState.isVisible = false
                    tv_no_wifi.isVisible = false
                   response.body()?.let{
                       setupList(it)
                   }


                }else{
                    Toast.makeText(context, "Algo deu errado tente mais tarde", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Carro>>, t: Throwable) {
                Toast.makeText(context, "Algo deu errado tente mais tarde", Toast.LENGTH_LONG).show()            }

        })
    }
    fun empityState(){
        progress.isVisible = false
        listaCarros.isVisible = false
        ivEmpityState.isVisible = true
        tv_no_wifi.isVisible = true
    }

    // Função para pegar todas as views do meu layout xml
    fun setupView(view: View){
        listaCarros = view.findViewById(R.id.rv_listaDeCarros)
        progress = view.findViewById(R.id.pb_loader)
        ivEmpityState = view.findViewById(R.id.iv_empityState)
        tv_no_wifi = view.findViewById(R.id.tv_no_wifi)


    }
    //função para chamar a conecção com o arquivo json
    fun callService(){
        MyTask().execute("https://igorbag.github.io/cars-api/cars.json")

    }
    fun setupList(lista: List<Carro>) {
        val carroAdapter = Car_Adapter(lista)
        listaCarros.adapter.apply {
            isVisible = true
            var adapter = carroAdapter
        }

        carroAdapter.carItemLister = { carro ->
            val isSaved = CarRepository(requireContext()).findCarById(1)

        }


        //Quando o usuario clicar na estrela irá salvar no db o carro especifico
        adapter.carItemLister = { carro ->

            val isSaved = CarRepository(requireContext()).saveIfNotExist(carro)
        }

    }
    //metodo para clicks

    // metodo que analisa se tem conecção com a internet ou não
    fun checkForInternet(context: Context?): Boolean{
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false
            val activNetwork = connectivityManager.getNetworkCapabilities(network)?:return false

            return when {
                activNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->true
                else -> false
            }
        } else{
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    // Classe interna para sincronizar com o json que contem os dados da lista, metodo sem abstração
    inner class MyTask : AsyncTask<String, String, String>() {

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            progress.visibility = View.VISIBLE
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg url: String?): String {

            var urlConnection: HttpURLConnection? = null
            try {
                val urlBase = URL(url[0])

                urlConnection = urlBase.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 60000
                urlConnection.readTimeout = 60000
                urlConnection.setRequestProperty(
                    "Accept",
                    "application/jason"
                )

                // validação para analisar se a resposta esta ok
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK){

                    val response = urlConnection.inputStream.bufferedReader().use { it.readText() }
                    publishProgress(response)
                }else{
                    Log.e("erro", "Serviço indisponivel no momento...")
                }

            }catch (ex: Exception){
                Log.e("erro", "Erro ao realizar processamento")
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect()
                }
            }
            return " "
        }

        @Deprecated("Deprecated in Java")
        override fun onProgressUpdate(vararg values: String?) {
            try {
                // Leitura do array de json
                val jsonArray = JSONTokener(values[0]).nextValue() as JSONArray

                for (i in 0 until jsonArray.length()){
                    val id = jsonArray.getJSONObject(i).getString("id")
                    val preco = jsonArray.getJSONObject(i).getString("preco")
                    val bateria = jsonArray.getJSONObject(i).getString("bateria")
                    val potencia = jsonArray.getJSONObject(i).getString("potencia")
                    val recarga = jsonArray.getJSONObject(i).getString("recarga")
                    val urlPhoto = jsonArray.getJSONObject(i).getString("urlPhoto")


                    //Classe para construir o modelo de carro

                    val model = Carro(
                        id = id.toInt(),
                        preco = preco,
                        bateria = bateria,
                        potencia = potencia,
                        recarga = recarga,
                        urlPhoto = urlPhoto,
                        isFavorite = false
                    )
                    // Adicionando cada modelo de carro dentro da lista
                    carrosArray.add(model)
                }
                progress.isVisible = false
                listaCarros.isVisible = true
                ivEmpityState.isVisible = false
                tv_no_wifi.isVisible = false

                //setupList()

            }catch (ex: Exception){
                Log.e("Erro ->", ex.message.toString())
            }
        }


    }



}