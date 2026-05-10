package com.example.notecook.Fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.AdapterFragment;
import com.example.notecook.Activity.MainActivity;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.databinding.FragmentMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainFragment extends Fragment  {

    FragmentMainBinding binding;
    public static ViewPager2 viewPager2;
    public static FloatingActionButton flBtn;
    private SharedRecipeViewModel viewModel;

    public MainFragment() {/* Required empty public constructor*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        viewPager2 = binding.vp2;
        //viewPager2.setOffscreenPageLimit(1);
        flBtn = getActivity().findViewById(R.id.floating_action_button);
        flBtn.show();

        flBtn.setOnClickListener(v -> {
                if(!MainActivity.Type_User.equals(Constants.TAG_MODE_INVITE)) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.ly_vp_con, new add_recipe());
                    fragmentTransaction.commitNow();

                    //viewPager2.setCurrentItem(1,false);
                    flBtn.setImageDrawable(ContextCompat.getDrawable(v.getContext(),R.drawable.ic_playlist_add_check_black_24dp));
                    flBtn.hide();
                }else Constants.showSnackPar(this.getView(),"Veuillez Connecter !!");
        });

        setViewPagerAdapter();
        viewPager2.setUserInputEnabled(false);
        navigation(binding.bottomNav,viewPager2);
        viewPager2.offsetLeftAndRight(1);
        return binding.getRoot();
    }

    public void navigation(BottomNavigationView menu,ViewPager2 viewPager2)
    {
        menu.setOnNavigationItemSelectedListener(
                item -> {
                    int i = 0;
                    switch (item.getItemId()) {
                        case R.id.tips:
                            i = 0;
                            break;
                        case R.id.fav:
                            i = 1;
                            break;
                        case R.id.search:
                            i = 2;
                            break;
                        case R.id.cart:
                            i = 3;
                            break;
                        case R.id.parson:
                            i = 4;
                            break;
                    }
                    if (MainActivity.Type_User.equals(Constants.TAG_MODE_INVITE) &&  i == 1)  {
                        Constants.DisplayErrorMessage((AppCompatActivity) getActivity(),"Veuillez Connecter !!");
                    }
                    else if(viewModel.getCurrentRecipe()==null &&  i == 1)
                    {
                        Constants.DisplayErrorMessage((AppCompatActivity) getActivity(),"No Recipe yet selected !!");
                    }
                    else
                    {
                        viewPager2.setCurrentItem(i, false);
                    }

                    return false;
                });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                flBtn.setImageDrawable(ContextCompat.getDrawable(viewPager2.getContext(),R.drawable.ic_baseline_plus_one_24));
                flBtn.show();
            }
            @Override
            public void onPageSelected(int position) {

                binding.bottomNav.getMenu().getItem(0).setChecked(false);
                binding.bottomNav.getMenu().getItem(position).setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
        });
    }


    public void setViewPagerAdapter() {
        AdapterFragment viewPager2Adapter = new AdapterFragment(requireActivity());
        //create an ArrayList of Fragments
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Acceuill_Frg());
        fragmentList.add(new Frg_detail_recipe());
        fragmentList.add(new Frg_Search());
        fragmentList.add(new Frg_Basket());
        fragmentList.add(new frg_Profil());
        AdapterFragment.setData(fragmentList);
        //set the data for the adapter
        viewPager2.setAdapter(viewPager2Adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // CRITICAL: This prevents the memory leak
        binding = null;
    }







}