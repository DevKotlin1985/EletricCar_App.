package curso_dio.eletriccarapp.data



interface CarAPI {

    @GET("cars.json")
    fun getAllCars() : Call<List<Car>>

}


