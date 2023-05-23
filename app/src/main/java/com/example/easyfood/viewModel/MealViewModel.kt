package com.example.easyfood.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.pojo.Meal
import com.example.easyfood.pojo.MealList
import com.example.easyfood.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealViewModel(val mealDatabase: MealDatabase): ViewModel() {

    // LiveData to store the value of Current Meal to show in app
    private var mealDetailsLiveData = MutableLiveData<Meal>()

    // Function to get Meal Data by its id from the API through Retrofit
    fun getMealDetailByID(id: String){
        RetrofitInstance.api.getMealDetailsById(id).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if(response.body() != null){
                    mealDetailsLiveData.value = response.body()!!.meals[0]
                }else {
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("TEST", t.message.toString())
            }
        })
    }

    // function to observe MealDetailsLLiveData from MealActivity
    fun observerMealDetailsLiveData(): LiveData<Meal>{
        return mealDetailsLiveData
    }


    // Room database Functions
    fun insertMeal(meal: Meal){
        // Launching under viewModel scope of coroutine
        viewModelScope.launch {
            mealDatabase.mealDao().upsertMeal(meal)
        }
    }

}