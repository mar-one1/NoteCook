package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.Basket_list;
import static com.example.notecook.Utils.Constants.CURRENT_FULL_RECIPE;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Ingredients_CurrentRecipe;
import static com.example.notecook.Utils.Constants.clickMoins;
import static com.example.notecook.Utils.Constants.clickPlus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_Nutrition;
import com.example.notecook.Adapter.Adapter_Rc_Ingredents;
import com.example.notecook.Data.IngredientsDataSource;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Nutrition;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.IngredientsViewModel;
import com.example.notecook.databinding.FragmentFrgRecipeIngredientsBinding;

import java.util.ArrayList;
import java.util.List;

public class Frg_recipe_ingredients extends Fragment {

    public static FragmentFrgRecipeIngredientsBinding binding;
    private List<Ingredients> List_ingredient;
    private RecyclerView mRecyclerView;
    private Button btn_plus, btn_moins;
    private TextView txt_cal;
    private IngredientsViewModel VMIngredient;


    public Frg_recipe_ingredients() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constants.bindingRcV_Ingredients(mRecyclerView, Ingredients_CurrentRecipe.getValue(), getContext());
        Toast.makeText(getContext(), "onDestroyView", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getContext(), "onPause Frg_ingredient", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(), "onStart Frg_ingredient", Toast.LENGTH_SHORT).show();
        //bindingRcV_Ingredients(mRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "onResume Frg_ingredient", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFrgRecipeIngredientsBinding.inflate(inflater, container, false);
        btn_plus = binding.btnPlus;
        btn_moins = binding.btnMoins;
        txt_cal = binding.txtTot;
        // Inflate the layout for this fragment
        mRecyclerView = binding.RcIngred;
        VMIngredient = new IngredientsViewModel(getContext(), getActivity());
        Constants.bindingRcV_Ingredients(mRecyclerView, Ingredients_CurrentRecipe.getValue(), getContext());
        bindingRcV_Nutrition(binding.RcvNutrition);
        Toast.makeText(getContext(), "onCreateView", Toast.LENGTH_SHORT).show();
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPlus(txt_cal, btn_moins);
            }
        });
        btn_moins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickMoins(txt_cal, btn_moins);
            }
        });

        binding.btnAddBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Basket_list.add(CURRENT_RECIPE);
            }
        });

        Ingredients_CurrentRecipe.observe(getViewLifecycleOwner(), new Observer<List<Ingredients>>() {
            @Override
            public void onChanged(List<Ingredients> ingredients) {
                // Update the adapter with new data
                Constants.bindingRcV_Ingredients(mRecyclerView, ingredients, getContext());
            }
        });


        return binding.getRoot();

    }

    public void bindingRcV_Nutrition(RecyclerView recyclerView) {
        //List_ingredient = new ArrayList<>();

//        IngredientsDataSource ingredientsDataSource1 = new IngredientsDataSource(getContext());
//        ingredientsDataSource1.open();
//        List_ingredient = ingredientsDataSource1.getAllIngredeients();
//        ingredientsDataSource1.close();

        List<Nutrition> nutritions = new ArrayList<>();
        nutritions.add(CURRENT_FULL_RECIPE.getNutrition());
        Adapter_RC_Nutrition adapter_rc_nutrition = new Adapter_RC_Nutrition(nutritions, getContext());
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHorizontalScrollBarEnabled(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter_rc_nutrition);


    }

}