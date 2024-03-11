package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.list_recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Model.Recipe;
import com.example.notecook.databinding.FragmentFrgRecipeProfilBinding;

import java.util.ArrayList;
import java.util.List;


public class Frg_Recipe_Profil extends Fragment {
    Recipe mRecipe;
    FragmentFrgRecipeProfilBinding binding;
    private RecyclerView recyclerView;

    public Frg_Recipe_Profil() {
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
        binding = FragmentFrgRecipeProfilBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        bindingRcV_recipes(binding.RcRecipeProfil);
        return binding.getRoot();
    }

    public void bindingRcV_recipes(RecyclerView recyclerView) {

        Adapter_RC_RecipeDt adapter_rc_recipeDt;
        adapter_rc_recipeDt = new Adapter_RC_RecipeDt(list_recipe, TAG_LOCAL);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        adapter_rc_recipeDt.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_recipeDt);
    }
}