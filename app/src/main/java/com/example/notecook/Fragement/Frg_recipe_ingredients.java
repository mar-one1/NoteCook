package com.example.notecook.Fragement;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_Nutrition;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Nutrition;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
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
    private SharedRecipeViewModel viewModel;


    public Frg_recipe_ingredients() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constants.bindingRcV_Ingredients(mRecyclerView, viewModel.getIngredientsCurrentRecipe().getValue(), getContext(),viewModel);
        Toast.makeText(getContext(), "onDestroyView", Toast.LENGTH_SHORT).show();
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
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        VMIngredient = new IngredientsViewModel(getContext(), getActivity(),viewModel);
        if(viewModel.getIngredientsCurrentRecipe().getValue() != null
        && !viewModel.getIngredientsCurrentRecipe().getValue().isEmpty()) Constants.bindingRcV_Ingredients(mRecyclerView, viewModel.getIngredientsCurrentRecipe().getValue(), getContext(),viewModel);
        viewModel.getRemoteNutritions().observe(getViewLifecycleOwner(), new Observer<Nutrition>() {
            @Override
            public void onChanged(Nutrition nutrition) {
                bindingRcV_Nutrition(binding.RcvNutrition);
            }
        });
        Toast.makeText(getContext(), "onCreateView", Toast.LENGTH_SHORT).show();
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v =clickPlus(txt_cal, btn_moins);
                Nutrition nutrition = viewModel.getRemoteNutritions().getValue();
                if(nutrition!=null) nutrition.scaleToServing(v,"g");
                viewModel.setRemoteNutritions(nutrition);
                bindingRcV_Nutrition(binding.RcvNutrition);
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
                viewModel.getBasketList().add(viewModel.getCurrentRecipe().getValue());
            }
        });

        viewModel.getIngredientsCurrentRecipe().observe(getViewLifecycleOwner(), new Observer<List<Ingredients>>() {
            @Override
            public void onChanged(List<Ingredients> ingredients) {
                // Update the adapter with new data
                Constants.bindingRcV_Ingredients(mRecyclerView, ingredients, getContext(),viewModel);
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
        nutritions.add(viewModel.getCurrentFullRecipe().getValue().getNutrition());
        Adapter_RC_Nutrition adapter_rc_nutrition = new Adapter_RC_Nutrition(nutritions);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHorizontalScrollBarEnabled(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter_rc_nutrition);


    }

}