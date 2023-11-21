package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.user_login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.Adapter_Vp2_recipeProfil;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.MainActivity;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.databinding.FragmentAddEditRecipeBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class Frg_add_edit_recipe extends Fragment {

    FragmentAddEditRecipeBinding binding;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private List<String> titles;
    private Drawable defaultImagelike;
    private Drawable defaultImageRate;
    private Recipe recipe;

    public Frg_add_edit_recipe() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity m = new MainActivity();

        User user = new User();
        user = user_login.getUser();
        binding.iconProfilDetailrecipe.setImageBitmap(m.decod(user.getIcon()));

        ArrayList<Recipe> recipe = new ArrayList<>();
        RecipeDatasource dataSource = new RecipeDatasource(getContext());
        dataSource.open();
        recipe = dataSource.getAllRecipes();
        dataSource.close();
        if (recipe.size() != 0) {
            binding.iconRecipe.setImageBitmap(m.decod(user.getIcon()));
//            binding.iconRecipe.setImageBitmap(m.decod(recipe.get(0).getIcon_recipe()));
        }

        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(getContext());
        detailRecipeDataSource.open();
        List<Detail_Recipe> listdr = detailRecipeDataSource.getAllDR();
        detailRecipeDataSource.close();
        if (listdr != null) {
            for (Detail_Recipe d : listdr
            ) {
                binding.icludeRoxDetailRecipe.vlTime.setText(Integer.toString(d.getTime()));
                binding.icludeRoxDetailRecipe.valCal.setText(Integer.toString(d.getCal()));
                binding.icludeRoxDetailRecipe.valLevel.setText(d.getLevel());
            }
        }
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddEditRecipeBinding.inflate(inflater, container, false);
        viewPager2 = binding.vp2Profil;
        tabLayout = binding.tl;

        tabLayout.addTab(tabLayout.newTab().setText("INGREDIENTS"));
        tabLayout.addTab(tabLayout.newTab().setText("PROCESS"));
        tabLayout.addTab(tabLayout.newTab().setText("REVIEWS"));

        setViewPagerAdapter();
        viewPager2.setUserInputEnabled(true);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.red));
        tabLayout.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(getResources().getColor(R.color.gray), Color.parseColor("#000000"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //   Toast.makeText(getContext(), "unselected" + tab.getText().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        defaultImagelike = getResources().getDrawable(R.drawable.ic_baseline_favorite_24);

        binding.IVFavorit.setOnClickListener(view -> {
            if (!defaultImagelike.getConstantState().equals(binding.IVFavorit.getDrawable().getConstantState())) {
                binding.IVFavorit.setImageDrawable(defaultImagelike);
            } else
                binding.IVFavorit.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        });

        defaultImageRate = getResources().getDrawable(R.drawable.ic_star_black_24dp);
        binding.FavRecipeIcone.setOnClickListener(view -> {
            if (!defaultImageRate.getConstantState().equals(binding.FavRecipeIcone.getDrawable().getConstantState())) {
                binding.FavRecipeIcone.setImageDrawable(defaultImageRate);
            } else
                binding.FavRecipeIcone.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_fill0_wght400_grad0_opsz48));
        });

        binding.btnBackDR.setOnClickListener(view -> {
            viewPager2 = getActivity().findViewById(R.id.vp2);
            viewPager2.setCurrentItem(0);
        });
        binding.btnShareDR.setOnClickListener(view -> {
            try {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "false");

                getContext().startActivity(Intent.createChooser(share, "Share"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        return binding.getRoot();
    }

    public void setViewPagerAdapter() {
        Adapter_Vp2_recipeProfil viewPager2Adapter = new
                Adapter_Vp2_recipeProfil(getActivity());
        //create an ArrayList of Fragments
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Frg_recipe_ingredients());
        fragmentList.add(new Frg_Step_Recipe());
        fragmentList.add(new Frg_recipe_fav());

        viewPager2Adapter.setData(fragmentList);
        //set the data for the adapter
        binding.vp2Profil.setAdapter(viewPager2Adapter);
    }
}