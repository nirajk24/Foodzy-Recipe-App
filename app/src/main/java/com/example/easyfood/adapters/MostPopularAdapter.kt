package com.example.easyfood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easyfood.databinding.PopularItemsBinding
import com.example.easyfood.pojo.MealsByCategory
import com.example.easyfood.pojo.dummyData
import kotlin.math.min

// Adapter for Most Popular Food Section
class MostPopularAdapter() : RecyclerView.Adapter<MostPopularAdapter.PopularMealViewHolder>() {

    lateinit var onItemClick: ((MealsByCategory) -> Unit)
    var onLongItemClick: ((MealsByCategory) -> Unit)? = null

    private var mealList = ArrayList<MealsByCategory>()

    fun setMeals(mealsList: ArrayList<MealsByCategory>) {
        this.mealList = mealsList
        notifyDataSetChanged()
    }

    // View Holder class for Popular Meals Adapter
    inner class PopularMealViewHolder(val binding: PopularItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMealViewHolder {
        return PopularMealViewHolder(
            PopularItemsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return min(mealList.size, 10)
    }

    override fun onBindViewHolder(holder: PopularMealViewHolder, position: Int) {
        // Setting up images of the food in recycler view
        Glide.with(holder.itemView)
            .load(mealList[position].strMealThumb.toString())
            .centerCrop()
            .into(holder.binding.imgPopularMealItem)

        holder.binding.tvPopularMealName.text = mealList[position].strMeal
        holder.binding.tvPopularMealRating.text = " " +
            dummyData[position].rating + " • " + dummyData[position].time

        holder.itemView.setOnClickListener {
            onItemClick.invoke(mealList[position])
        }

        holder.itemView.setOnLongClickListener {
            onLongItemClick?.invoke(mealList[position])
            true
        }
    }
}