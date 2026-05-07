package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.Type_User;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.getUserInput;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Adapter.Adapter_Vp2_recipeProfil;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgProfilBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class frg_Profil extends Fragment {

    private FragmentFrgProfilBinding bindingProfil;

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private SharedRecipeViewModel viewModel;

    private FloatingActionButton fab;

    private static final String TAG = "Profil";

    public frg_Profil() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bindingProfil = FragmentFrgProfilBinding.inflate(inflater, container, false);

        viewPager2 = bindingProfil.vp2Profil;
        tabLayout = bindingProfil.tl;

        viewModel = new ViewModelProvider(requireActivity())
                .get(SharedRecipeViewModel.class);

        recipeVM = new RecipeViewModel(requireContext(), requireActivity(), viewModel);
        userVM = new UserViewModel(requireContext(), requireActivity(), viewModel);

        setupFab();
        setupTabs();
        setupViewPager();
        setupAdapter();

        getUserInfo();

        bindingProfil.ImgVSetting.setOnClickListener(view -> {

            if (Type_User.equals(Constants.TAG_MODE_INVITE)) {

                Toast.makeText(getContext(),
                        Constants.TAG_MODE_INVITE,
                        Toast.LENGTH_SHORT).show();

            } else {

                FragmentTransaction ft =
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();

                ft.replace(R.id.ly_vp_con, new Frg_EditProfil());
                ft.addToBackStack("edit_profile");
                ft.commit();
            }
        });

        return bindingProfil.getRoot();
    }

    // ================= FAB =================
    private void setupFab() {

        if (getActivity() != null) {
            fab = getActivity().findViewById(R.id.floating_action_button);
            if (fab != null) fab.show();
        }
    }

    // ================= TABS =================
    private void setupTabs() {

        tabLayout.addTab(tabLayout.newTab().setText("MY RECIPES"));
        tabLayout.addTab(tabLayout.newTab().setText("MY BONUSES"));

        tabLayout.setSelectedTabIndicatorColor(
                getResources().getColor(R.color.red)
        );

        tabLayout.setSelectedTabIndicatorHeight(
                (int) (3 * getResources().getDisplayMetrics().density)
        );

        tabLayout.setTabTextColors(
                getResources().getColor(R.color.gray),
                Color.BLACK
        );

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // ================= VIEWPAGER =================
    private void setupViewPager() {

        viewPager2.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {

                    @Override
                    public void onPageSelected(int position) {
                        tabLayout.selectTab(tabLayout.getTabAt(position));
                    }
                }
        );
    }

    // ================= ADAPTER =================
    private void setupAdapter() {

        Adapter_Vp2_recipeProfil adapter =
                new Adapter_Vp2_recipeProfil(getActivity());

        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new Frg_Recipe_Profil());
        fragments.add(new Frg_recipe_fav());

        adapter.setData(fragments);

        viewPager2.setAdapter(adapter);
    }

    // ================= USER INFO =================
    private void getUserInfo() {

        if (Type_User.equals(TAG_MODE_INVITE)) return;

        Constants.loading_ui(getContext(), getActivity(), "Loading...");

        String userId = getUserInput(getContext());

        Observer<User> observer = user -> {

            if (user != null) {
                extracted();
            }

            Constants.dismissLoadingDialog();
        };

        if (Boolean.TRUE.equals(viewModel.getModeOnline().getValue())) {

            userVM.getUser(userId)
                    .observe(getViewLifecycleOwner(), observer);

        } else {

            userVM.getUserLocal(userId, "")
                    .observe(getViewLifecycleOwner(), observer);
        }
    }

    // ================= UI UPDATE =================
    private void extracted() {

        if (Type_User.equals(TAG_MODE_INVITE)) return;

        if (viewModel.getUserLogin().getValue() == null) return;

        User user = viewModel.getUserLogin().getValue().getUser();

        if (user == null) return;

        bindingProfil.txtUsername.setText(user.getUsername());

        bindingProfil.txtGradeStatus.setText(
                user.getGrade() + " " + user.getStatus()
        );

        MainActivity.showImageUsers(user, bindingProfil.iconProfil);

        Log.d(TAG, "User loaded");
    }

//    private void updateEmptyState(List<?> list) {
//
//        if (list == null || list.isEmpty()) {
//
//            binding.txtEmpty.setVisibility(View.VISIBLE);
//            binding.RcRecipeSearch.setVisibility(View.GONE);
//
//        } else {
//
//            binding.txtEmpty.setVisibility(View.GONE);
//            binding.RcRecipeSearch.setVisibility(View.VISIBLE);
//        }
//    }
}