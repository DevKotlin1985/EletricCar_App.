package curso_dio.eletriccarapp.data.local

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_BATERIA
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_POTENCIA
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_PRECO
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_RECARGA
import curso_dio.eletriccarapp.data.local.CarrosContract.CarEntry.COLUMN_NAME_URL_PHOTO
import curso_dio.eletriccarapp.domain.Carro

class CarRepository(private val context: Context) {

    fun save (carro: Carro): Boolean {
        var isSaved = false
        try {
            val dbHelper = CarsDbHelper(context,)
            val db = dbHelper.writableDatabase

            val values = ContentValues().apply {
                put(CarrosContract.CarEntry.COLUMN_NAME_PRECO, carro.preco)
                put(CarrosContract.CarEntry.COLUMN_NAME_BATERIA, carro.bateria)
                put(CarrosContract.CarEntry.COLUMN_NAME_POTENCIA, carro.potencia)
                put(CarrosContract.CarEntry.COLUMN_NAME_RECARGA, carro.recarga)
                put(CarrosContract.CarEntry.COLUMN_NAME_URL_PHOTO, carro.urlPhoto)
            }

            val inserted = db?.insert(CarrosContract.CarEntry.TABLE_NAME, null, values)

            if (inserted != null){
                isSaved = true
            }

        } catch (ex: Exception){
            ex.message?.let {
                Log.e("Erro ao inserir os dados", it)
            }
        }

        return isSaved
    }

    fun findCarById(id: Int){
        val dbHelper = CarsDbHelper(context = )
        val db = dbHelper.readableDatabase

        val columns = arrayOf(BaseColumns._ID,
            COLUMN_NAME_PRECO,
            COLUMN_NAME_BATERIA,
            COLUMN_NAME_POTENCIA,
            COLUMN_NAME_RECARGA,
            COLUMN_NAME_URL_PHOTO
        )

        val filter = "${BaseColumns._ID} = ?"
        val filterValues = arrayOf(id.toString())
        val cursor = db.query(
            CarrosContract.CarEntry.TABLE_NAME,
            columns,
            filter,
            filterValues,
            null,
            null,
            null
        )

        val itemCar = mutableListOf<Carro>()
        with (cursor){
            while (moveToNext()){
                    val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    Log.d("ID -> ", itemId.toString())
            }

        }
        cursor.close()
    }

    }

