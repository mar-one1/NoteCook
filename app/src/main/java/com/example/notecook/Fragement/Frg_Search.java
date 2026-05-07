package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.TAG_REMOTE;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.BuildConfig;
import com.example.notecook.Dto.RecipesResponce;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.databinding.FragmentFrgSearchBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Frg_Search extends Fragment {

    private FragmentFrgSearchBinding binding;

    private Drawable defaultImagelike;

    private RecipeViewModel recipeVM;

    private FragmentActivity fragmentActivity;

    private SharedRecipeViewModel viewModel;

    private Adapter_RC_RecipeDt adapter_rc_recipeDt;

    private int currentPage = 1;

    private boolean isLoading = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private Runnable searchRunnable;

    public Frg_Search() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFrgSearchBinding.inflate(inflater, container, false);

        fragmentActivity = (FragmentActivity) getContext();

        viewModel =
                new ViewModelProvider(requireActivity())
                        .get(SharedRecipeViewModel.class);

        recipeVM =
                new RecipeViewModel(
                        getContext(),
                        getActivity(),
                        viewModel
                );

        defaultImagelike =
                ContextCompat.getDrawable(
                        fragmentActivity,
                        R.drawable.ic_baseline_favorite_24
                );

        setupRecycler();

        setupPagination();

        Constants.level(binding.levelRecipeSearch, getContext());

        binding.filtreSearch.setOnClickListener(v -> {

            binding.llFiltre.setVisibility(
                    binding.llFiltre.getVisibility() == View.VISIBLE
                            ? View.GONE
                            : View.VISIBLE
            );
        });

        binding.filterButton.setOnClickListener(v -> {

            binding.llFiltre.setVisibility(View.GONE);

            binding.filtreSearch.setImageDrawable(
                    ContextCompat.getDrawable(
                            v.getContext(),
                            R.drawable.active_filtre_24
                    )
            );

            currentPage = 1;

            String searchText =
                    binding.txtRecherche.getText().toString().trim();

            search(searchText, currentPage);

            binding.filtreClear.setVisibility(View.VISIBLE);
        });

        binding.filtreClear.setOnClickListener(v -> {

            binding.filtreClear.setVisibility(View.GONE);

            binding.txtRecherche.setText("");

            binding.llFiltre.setVisibility(View.GONE);

            binding.levelRecipeSearch.setSelection(0);

            binding.filtreSearch.setImageDrawable(
                    ContextCompat.getDrawable(
                            v.getContext(),
                            R.drawable.filtre_search_24
                    )
            );

            currentPage = 1;

            search("", currentPage);
        });

        binding.txtRecherche.requestFocus();

        binding.txtRecherche.addTextChangedListener(new TextWatcher() {

            Set<String> historyList = new HashSet<>();

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {

                String txt = s.toString().trim();

                if (!txt.isEmpty()) {
                    historyList.add(txt);
                }
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                handler.removeCallbacks(searchRunnable);

                searchRunnable = () -> {

                    currentPage = 1;

                    search(s, currentPage);
                };

                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {

                binding.filtreClear.setVisibility(View.VISIBLE);

                List<String> suggestions = new ArrayList<>();

                for (String item : historyList) {

                    if (item.contains(s)
                            && !suggestions.contains(item)) {

                        suggestions.add(item);
                    }
                }

                ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<>(
                                getContext(),
                                androidx.appcompat.R.layout
                                        .support_simple_spinner_dropdown_item,
                                suggestions
                        );

                binding.txtRecherche.setAdapter(arrayAdapter);

                binding.txtRecherche.setThreshold(2);
            }
        });

        return binding.getRoot();
    }

    private void setupRecycler() {

        adapter_rc_recipeDt =
                new Adapter_RC_RecipeDt(
                        getContext(),
                        getActivity(),
                        viewModel,
                        new ArrayList<>(),
                        TAG_REMOTE
                );

        GridLayoutManager manager =
                new GridLayoutManager(getContext(), 2);

        binding.RcRecipeSearch.setLayoutManager(manager);

        binding.RcRecipeSearch.setAdapter(adapter_rc_recipeDt);
    }

    private void setupPagination() {

        binding.RcRecipeSearch.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(
                            @NonNull RecyclerView rv,
                            int dx,
                            int dy
                    ) {

                        super.onScrolled(rv, dx, dy);

                        if (!rv.canScrollHorizontally(1)
                                && !isLoading) {

                            RecipesResponce response =
                                    viewModel
                                            .getRemoteSearchRecipesByPages()
                                            .getValue();

                            if (response != null
                                    && currentPage
                                    < response.getTotalPages()) {

                                currentPage++;

                                String searchText =
                                        binding.txtRecherche
                                                .getText()
                                                .toString()
                                                .trim();

                                search(searchText, currentPage);
                            }
                        }
                    }
                });
    }

    private void search(CharSequence s, int page) {

        if (isLoading) return;

        isLoading = true;

        Map<String, String> condition = new HashMap<>();

        condition.put("searchText", s.toString());

        String level =
                binding.levelRecipeSearch
                        .getSelectedItem()
                        .toString()
                        .trim();

        if (!level.equals("autre")) {

            condition.put("Level_recipe", level);
        }

        recipeVM.SearchRecipeByConditionApi(condition, page, 10).observe(getViewLifecycleOwner(), recipes -> {
                    isLoading = false;
                    if (recipes == null
                            || recipes.getRecipes() == null) {
                        return;
                    }
                    if (page == 1) {

                        adapter_rc_recipeDt.setRecipes(recipes.getRecipes());
                    } else {
                        adapter_rc_recipeDt
                                .addRecipes(recipes.getRecipes());
                    }
                    viewModel.setRemoteSearchRecipesByPages(recipes);
                });
    }

    private void openSettings() {

        Intent intent =
                new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                );

        Uri uri =
                Uri.fromParts(
                        "package",
                        BuildConfig.APPLICATION_ID,
                        null
                );

        intent.setData(uri);

        startActivityForResult(intent, 101);
    }
}