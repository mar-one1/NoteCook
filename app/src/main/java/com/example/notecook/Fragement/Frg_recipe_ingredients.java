package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.Basket_list;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Ingredients_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_Rc_Ingredents;
import com.example.notecook.Data.IngredientsDataSource;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.databinding.FragmentFrgRecipeIngredientsBinding;

import java.util.ArrayList;
import java.util.List;

public class Frg_recipe_ingredients extends Fragment {

    private FragmentFrgRecipeIngredientsBinding binding;
    private List<Ingredients> List_ingredient;
    private RecyclerView mRecyclerView;
    private Button btn_plus,btn_moins;
    private TextView txt_cal;



    public Frg_recipe_ingredients() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constants.bindingRcV_Ingredients(mRecyclerView,Ingredients_CurrentRecipe,getContext());
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
        //bindingRcV_Ingredients(mRecyclerView);
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

        Constants.bindingRcV_Ingredients(mRecyclerView,Ingredients_CurrentRecipe,getContext());
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
        mRecyclerView =binding.RcIngred;
        Constants.bindingRcV_Ingredients(mRecyclerView,Ingredients_CurrentRecipe,getContext());
        Toast.makeText(getContext(), "onCreateView", Toast.LENGTH_SHORT).show();

        binding.btnMoins.setOnClickListener(view -> {
            int t = Integer.parseInt(txt_cal.getText().toString());
            if(t<=0)
            {
                btn_moins.setEnabled(false);
            }
            else
            t--;
            txt_cal.setText(""+t);
        });

        binding.btnPlus.setOnClickListener(view -> {
            int t = Integer.parseInt(txt_cal.getText().toString());
            btn_moins.setEnabled(true);
                t++;
            txt_cal.setText("" + t);
        });

        binding.btnAddBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Basket_list.add(CURRENT_RECIPE);
            }
        });

        return binding.getRoot();
    }

    public void bindingRcV_Nutrition(RecyclerView recyclerView)
    {
        List_ingredient = new ArrayList<>();

        IngredientsDataSource ingredientsDataSource1 = new IngredientsDataSource(getContext());
        ingredientsDataSource1.open();
        List_ingredient=ingredientsDataSource1.getAllIngerdeients();
        ingredientsDataSource1.close();

        Adapter_Rc_Ingredents adapter_rc_ingredents = new Adapter_Rc_Ingredents(List_ingredient);
        GridLayoutManager manager = new GridLayoutManager(getContext(),1);
        recyclerView.setHorizontalScrollBarEnabled(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter_rc_ingredents);


    }

}