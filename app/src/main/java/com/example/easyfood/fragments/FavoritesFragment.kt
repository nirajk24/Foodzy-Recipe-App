package com.example.easyfood.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfood.R
import com.example.easyfood.activities.MainActivity
import com.example.easyfood.activities.MealActivity
import com.example.easyfood.adapters.FavoritesAdapter
import com.example.easyfood.databinding.FragmentFavoritesBinding
import com.example.easyfood.viewModel.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: HomeViewModel

    // Adapter
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        observeFavorites()  // -> Set and update data for recycler view

        // For implementing the Delete on Swipe
        // works in Recycler view only (I think)
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,  // Direction in which item moves (ignore)
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT  // Direction to take action
        ) {
            // No action taken on move
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            // Deleting on swipe
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Getting the position of current item
                val position = viewHolder.adapterPosition
                // Saving an instance of current meal in case of undo
                val mealUndo = favoritesAdapter.differ.currentList[position]
                // Deleting meal from database
                viewModel.deleteMeal(favoritesAdapter.differ.currentList[position])

                // Showing a SnackBar with undo option
                Snackbar.make(requireView(), "Meal Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", View.OnClickListener {
                        viewModel.insertMeal(mealUndo)
                    }).show()
            }
        }

        // Attaching itemTouchHelper to Recycler View
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvFavorites)

        onCategoryMealItemClick()  // -> setting up onClick method
    }


    private fun prepareRecyclerView() {
        favoritesAdapter = FavoritesAdapter()
        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            adapter = favoritesAdapter
        }
    }

    private fun observeFavorites() {
        viewModel.observeFavoritesMealsLiveData().observe(viewLifecycleOwner, Observer { meals ->
            favoritesAdapter.differ.submitList(meals)  // -> this is how data is passed through differ
        })
    }


    // Function to navigate to MealActivity from favorite meal item list onClick
    private fun onCategoryMealItemClick() {
        favoritesAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.apply {
                putExtra(HomeFragment.MEAL_ID, meal.idMeal)
                putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
                putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)

                startActivity(intent)
            }
        }
    }

}