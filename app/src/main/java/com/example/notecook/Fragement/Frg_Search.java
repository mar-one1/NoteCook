package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.TAG_REMOTE;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.BuildConfig;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Dto.RecipesResponce;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.databinding.FragmentFrgSearchBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Frg_Search extends Fragment {

    private FragmentFrgSearchBinding binding;
    private Drawable defaultImagelike;
    private RecipeViewModel recipeVM;
    private FragmentActivity fragmentActivity;
    private SharedRecipeViewModel viewModel;
    private Adapter_RC_RecipeDt adapter_rc_recipeDt;


    public Frg_Search() {
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
        binding = FragmentFrgSearchBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        fragmentActivity = (FragmentActivity) getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        recipeVM = new RecipeViewModel(getContext(), getActivity(),viewModel);
        bindingRcV_recipes(binding.RcRecipeSearch, null, "default");
        //defaultImagelike=binding.HeartImgeclk;
        defaultImagelike = ContextCompat.getDrawable(fragmentActivity,R.drawable.ic_baseline_favorite_24);
        binding.filtreSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openSettings();
                // Construct filter parameters
                binding.llFiltre.setVisibility(binding.llFiltre.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = !(binding.txtRecherche.getText().equals("")) ? binding.txtRecherche.getText().toString().trim() : binding.txtRecherche.getText().toString();
                //double minPrice = Double.parseDouble(binding.minPriceEditText.getText().toString().trim());
                //double maxPrice = Double.parseDouble(binding.maxPriceEditText.getText().toString().trim());
                binding.llFiltre.setVisibility(View.GONE);
                binding.filtreSearch.setImageDrawable(ContextCompat.getDrawable(v.getContext(),R.drawable.active_filtre_24));
                search(searchText,1);
                binding.filtreClear.setVisibility(View.VISIBLE);
            }
        });
        binding.RcRecipeSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                RecipesResponce recipesResponce=viewModel.getRemoteSearchRecipesByPages().getValue();
                if (!rv.canScrollVertically(1) &&  Objects.requireNonNull(recipesResponce).getPage()< recipesResponce.getTotalPages()) {
                    recipesResponce.setPage(recipesResponce.getPage()+1);
                    //viewModel.getRemoteSearchRecipesByPages().setValue(recipesResponce);
                    String searchText = !(binding.txtRecherche.getText().equals("")) ? binding.txtRecherche.getText().toString().trim() : binding.txtRecherche.getText().toString();
                    search(searchText,recipesResponce.getPage());
                }
            }
        });
        binding.filtreClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.filtreClear.setVisibility(View.GONE);
                binding.txtRecherche.setText("");
                binding.llFiltre.setVisibility(View.GONE);
                binding.levelRecipeSearch.setSelection(0);
                binding.filterButton.callOnClick();
                binding.filtreSearch.setImageDrawable(ContextCompat.getDrawable(v.getContext(),R.drawable.filtre_search_24));
            }
        });

        binding.txtRecherche.requestFocus();
        binding.txtRecherche.addTextChangedListener(new TextWatcher() {
            Set<String> list = new HashSet<>();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Search_list.clear();
                // binding.txtRecherche.setText(s + " \n");
                String txt = s.toString().trim();
                if (!list.contains(txt) && !txt.isEmpty()) {
                    list.add(txt);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //searchRecipes(String.valueOf(s));
                search(s,1);
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.filtreClear.setVisibility(View.VISIBLE);
                List<String> listcurrent = new ArrayList<>();
                for (String item : list) {
                    if (item.contains(s) && !listcurrent.contains(s)) listcurrent.add(s.toString());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listcurrent);
                binding.txtRecherche.setAdapter(arrayAdapter);
                binding.txtRecherche.setThreshold(2);
            }
        });
        Constants.level(binding.levelRecipeSearch,getContext());

        return binding.getRoot();
    }

    private void search(CharSequence s,int page) {
        Map<String, String> condition = new HashMap<>();
        condition.put("searchText", s.toString());
        String level = binding.levelRecipeSearch.getSelectedItem().toString().trim();
        if (!level.equals("autre"))
            condition.put("Level_recipe", level);
        //condition.put("userId", "1");
        recipeVM.SearchRecipeByConditionApi(condition,page,10).observe(requireActivity(), new Observer<RecipesResponce>() {
            @Override
            public void onChanged(RecipesResponce recipes) {
                if (recipes != null && !recipes.getRecipes().isEmpty()) {
                    viewModel.getRemoteSearchRecipesByPages().getValue().getRecipes().addAll(recipes.getRecipes());
                    //bindingRcV_recipes(binding.RcRecipeSearch, recipes.getRecipes(), "search");
                    adapter_rc_recipeDt.addRecipes(recipes.getRecipes());
                }
            }
        });
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);

        startActivityForResult(intent, 101);
    }

    public void bindingRcV_recipes(RecyclerView recyclerView, List<Recipe> searchList, String tag) {
        if (!tag.equals("search") && viewModel.getRemoteRecipesByPages().getValue() != null)
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(), viewModel,viewModel.getRemoteRecipesByPages().getValue().getRecipes(), TAG_REMOTE);
        else {
            if (searchList == null) searchList = new ArrayList<>();
            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(getContext(), getActivity(),viewModel, searchList, TAG_REMOTE);
        }
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        adapter_rc_recipeDt.notifyDataSetChanged();
        recyclerView.setAdapter(adapter_rc_recipeDt);
    }
}