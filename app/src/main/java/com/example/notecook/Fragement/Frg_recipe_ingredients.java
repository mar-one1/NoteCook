package com.example.notecook.Fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_Rc_Ingredents;
import com.example.notecook.Data.IngredientsDataSource;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.databinding.FragmentFrgRecipeIngredientsBinding;

import java.util.ArrayList;
import java.util.List;

public class Frg_recipe_ingredients extends Fragment {

    FragmentFrgRecipeIngredientsBinding binding;
    List<Ingredients> List_ingredient;
    RecyclerView mRecyclerView;
    Button btn_plus,btn_moins;
    TextView txt_cal;
    //String Url = "https://www.jamieoliver.com/recipes/vegetables-recipes/superfood-salad/";


    public Frg_recipe_ingredients() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        bindingRcV_Incredients(mRecyclerView);


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
        return binding.getRoot();
    }

    public void bindingRcV_Incredients(RecyclerView recyclerView)
    {
        List_ingredient = new ArrayList<>();

        IngredientsDataSource ingredientsDataSource1 = new IngredientsDataSource(getContext());
        ingredientsDataSource1.open();
        List_ingredient=ingredientsDataSource1.getAllIngerdeients();
        ingredientsDataSource1.close();


//        for (int i = 0; i < 8; i++) {
//            ingerdeients = new Ingerdeients("Sucre", 116);
//            List_ingredient.add(ingerdeients);
//        }

        Adapter_Rc_Ingredents adapter_rc_ingredents = new Adapter_Rc_Ingredents(List_ingredient);
        GridLayoutManager manager = new GridLayoutManager(getContext(),1);
        recyclerView.setHorizontalScrollBarEnabled(true);
        recyclerView.setLayoutManager(manager);

        adapter_rc_ingredents.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_ingredents);


    }

    public void bindingRcV_Nutrition(RecyclerView recyclerView)
    {
        List_ingredient = new ArrayList<>();

        IngredientsDataSource ingredientsDataSource1 = new IngredientsDataSource(getContext());
        ingredientsDataSource1.open();
        List_ingredient=ingredientsDataSource1.getAllIngerdeients();
        ingredientsDataSource1.close();


//        for (int i = 0; i < 8; i++) {
//            ingerdeients = new Ingerdeients("Sucre", 116);
//            List_ingredient.add(ingerdeients);
//        }

        Adapter_Rc_Ingredents adapter_rc_ingredents = new Adapter_Rc_Ingredents(List_ingredient);
        GridLayoutManager manager = new GridLayoutManager(getContext(),1);
        recyclerView.setHorizontalScrollBarEnabled(true);
        recyclerView.setLayoutManager(manager);

        adapter_rc_ingredents.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_ingredents);


    }
}