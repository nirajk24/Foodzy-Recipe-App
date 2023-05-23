package com.example.easyfood.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easyfood.pojo.Meal
import com.example.easyfood.pojo.MealList
import com.example.easyfood.pojo.MealsByCategory
import com.example.easyfood.pojo.MealsByCategoryList
import com.example.easyfood.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryMealsViewModel() : ViewModel() {

    // Live data to store meal list by category
    val mealsLiveData = MutableLiveData<List<MealsByCategory>>()

    lateinit var currentMeal : Meal

    // function to return immutable mealsLiveData to observe from CategoryMealsActivity
    fun observeMealsLiveData() : LiveData<List<MealsByCategory>>{
        return mealsLiveData
    }

    // function to get meals data by category by retrofit
    fun getMealsByCategory(category: String) {
        RetrofitInstance.api.getMealsByCategory(category)
            .enqueue(object : Callback<MealsByCategoryList> {

                // If the response is received
                override fun onResponse(
                    call: Call<MealsByCategoryList>,
                    response: Response<MealsByCategoryList>
                ) {
                    // let method to check for non null and then save data
                    response.body()?.let { mealsList ->
                        mealsLiveData.postValue(mealsList.meals)
                    }
                }

                // On failure
                override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                    Log.e("TEST", t.message.toString())
                }

            })
    }

    // Function to get meal details by its id for Bottom Sheet
    fun getMealById(id: String) {
        RetrofitInstance.api.getMealDetailsById(id).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val meal = response.body()?.meals?.first()
                meal?.let {
                    currentMeal = response.body()!!.meals[0]
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("TEST", t.message.toString())
            }

        })
    }
}