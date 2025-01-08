package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.Type_User;
import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.RemotelistByIdUser_recipe;
import static com.example.notecook.Utils.Constants.RemotelistFullRecipe;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgRecipeProfilBinding;

import java.util.List;


public class Frg_Recipe_Profil extends Fragment {
    private Recipe mRecipe;
    private FragmentFrgRecipeProfilBinding binding;
    private RecyclerView recyclerView;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private Adapter_RC_RecipeDt adapter_rc_recipeDt;

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
        recipeVM = new RecipeViewModel(requireContext(), requireActivity());
        userVM = new UserViewModel(requireContext(), requireActivity());
        //recipeVM = new ViewModelProvider(requireActivity(), recipeVM).get(RecipeViewModel.class);
//        list_recipe.observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
//            @Override
//            public void onChanged(List<Recipe> recipes) {
//                bindingRcV_recipes(binding.RcRecipeProfil, list_recipe.getValue());
//            }
//        });
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void bindingRcV_recipes(RecyclerView recyclerView, List<Recipe> recipes) {

        adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), recipes, TAG_LOCAL);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        adapter_rc_recipeDt.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_recipeDt);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindingRcV_recipes(binding.RcRecipeProfil, list_recipe.getValue());
    }


}