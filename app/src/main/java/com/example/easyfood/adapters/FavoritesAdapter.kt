package com.example.easyfood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easyfood.databinding.MealItem2Binding
import com.example.easyfood.databinding.MealItemBinding
import com.example.easyfood.pojo.Meal
import com.example.easyfood.pojo.dummyData
import com.example.easyfood.pojo.numArray
import kotlin.random.Random

class FavoritesAdapter: RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    lateinit var onItemClick : ((Meal) -> Unit)

    // ViewHolder
    inner class FavoritesViewHolder(val binding: MealItem2Binding)
        : RecyclerView.ViewHolder(binding.root)

    // diffUtil to improve the performance of Recycler View
    // diffUtil helps us manage the rv efficiently. In case an item is updated or deleted
    // diffUtil updates or delete that item only without disturbing the other items.
    private val diffUtil = object: DiffUtil.ItemCallback<Meal>(){  // Created an object of DiffUtil ItemCallBack class
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            // checks if the items are same
            return oldItem.idMeal == newItem.idMeal  // returns true if item id is same
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            // check if the contents of item same
            return oldItem == newItem  // returns true if the items are same
        }
    }
    // does something
    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(
            MealItem2Binding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val meal = differ.currentList[position]  // returns current meal

        Glide.with(holder.itemView)
            .load(meal.strMealThumb)
            .into(holder.binding.imgMeal)

        val rand = Random.nextInt( 6)

        holder.binding.apply {
            tvMealName.text = meal.strMeal
            tvMealRating.text = dummyData[position].rating +" (" + numArray[rand].toString() + "+)"
            tvMealCategory.text = "Category: " + meal.strCategory
            tvMealArea.text = "Area: " + meal.strArea
            tvMealTime.text = dummyData[position].time
        }


        holder.itemView.setOnClickListener {
            onItemClick.invoke(meal)
        }
    }
}