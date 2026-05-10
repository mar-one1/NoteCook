package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.TAG_LOCAL;
import static com.example.notecook.Activity.MainActivity.Type_User;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.getUserSynch;
import static com.example.notecook.Utils.Constants.saveUserSynch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgRecipeProfilBinding;

import java.util.List;


public class Frg_Recipe_Profil extends Fragment {
    private FragmentFrgRecipeProfilBinding binding;
    private Adapter_RC_RecipeDt adapter_rc_recipeDt;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private SharedRecipeViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFrgRecipeProfilBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        recipeVM = new ViewModelProvider(requireActivity(), new RecipeViewModel(requireContext(), requireActivity(),viewModel))
                .get(RecipeViewModel.class);
        userVM = new ViewModelProvider(requireActivity(), new UserViewModel(requireContext(), requireActivity(),viewModel))
                .get(UserViewModel.class);

        // Observe local recipes
        viewModel.getListRecipe().observe(getViewLifecycleOwner(), recipes ->
                bindRecipesToRecycler(binding.RcRecipeProfil, recipes));

        fetchUserAndRecipes();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindRecipesToRecycler(binding.RcRecipeProfil, viewModel.getListRecipe().getValue());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void bindRecipesToRecycler(RecyclerView recyclerView, List<Recipe> recipes) {
        adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(),viewModel, recipes, TAG_LOCAL);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter_rc_recipeDt.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_recipeDt);
    }

    private void fetchUserAndRecipes() {
        if (Type_User.equals(TAG_MODE_INVITE)) return;

        User localUser = viewModel.getUserLoginLocal().getValue().getUser();
        if (localUser != null && localUser.getId_User() != 0) {
            loadRecipesForUser(localUser.getId_User(), localUser.getUsername());
        } else {
            userVM.getUserLocal(getUserInput(requireContext()), "success").observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    loadRecipesForUser(user.getId_User(), user.getUsername());
                }
            });
        }
    }

    private void loadRecipesForUser(int userId, String username) {
        recipeVM.getRecipesLocal(userId).observe(getViewLifecycleOwner(), localRecipes -> {
            if (localRecipes != null) {
                viewModel.setListRecipe(localRecipes);
                if (Boolean.TRUE.equals(viewModel.getModeOnline().getValue())) {
                    recipeVM.getFullRecipesByUsername(username).observe(getViewLifecycleOwner(), remoteRecipes -> {
                        if (remoteRecipes != null) {
                            viewModel.setRemoteListFullRecipe(remoteRecipes);
                            if (!getUserSynch(viewModel.getUserLogin().getValue().getUser().getUsername(), requireContext())) {
                                synchronizeIfNeeded(localRecipes, remoteRecipes, username, userId);
                            }
                        }
                    });
                }
            }
        });
    }

    private void synchronizeIfNeeded(List<Recipe> localRecipes, List<RecipeResponse> remoteRecipes, String username, int userId) {
        recipeVM.synchronisationRecipes(localRecipes, remoteRecipes, username).observe(getViewLifecycleOwner(), result -> {
            saveUserSynch(username, result, requireContext());
            if (result) {
                recipeVM.getRecipesLocal(userId).observe(getViewLifecycleOwner(), updatedLocal -> {
                    if (updatedLocal != null) {
                        viewModel.setListRecipe(updatedLocal);
                        bindRecipesToRecycler(binding.RcRecipeProfil, updatedLocal);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // CRITICAL: This prevents the memory leak
        binding = null;
    }
}


