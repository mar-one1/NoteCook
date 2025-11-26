package com.example.notecook.Fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.notecook.Adapter.Adapter_Rc_Review;
import com.example.notecook.Model.Review;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.databinding.FragmentFrgFavBinding;

import java.util.ArrayList;
import java.util.List;

public class Frg_Fav extends Fragment {

    private List<Review> listReview= new ArrayList<>();
    private FragmentFrgFavBinding binding;
    private SharedRecipeViewModel viewModel;

    public Frg_Fav() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewPagerAdapter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFrgFavBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        return binding.getRoot();
    }

    public void setViewPagerAdapter () {
        Adapter_Rc_Review adapter_rc_review = new Adapter_Rc_Review(viewModel.getReviewCurrentRecipe(),getContext());
        GridLayoutManager manager = new GridLayoutManager(getContext(),1);
        binding.rcReview.setLayoutManager(manager);

        adapter_rc_review.notifyDataSetChanged();
        binding.rcReview.setAdapter(adapter_rc_review);
    }
}