  package com.example.notecook.Fragement;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Pay.CheckoutActivity;
import com.example.notecook.R;
import com.example.notecook.Utils.SharedRecipeViewModel;
import com.example.notecook.databinding.FragmentFrgBasketBinding;

  public class Frg_Basket extends Fragment {

    private FragmentFrgBasketBinding binding;
      private SharedRecipeViewModel viewModel;

    public Frg_Basket() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

      @Override
      public void onResume() {
          super.onResume();
          if(!viewModel.getBasketList().isEmpty())
          {
              binding.txtLstEmpty.setVisibility(View.GONE);
              bindingRcV_Baskets(binding.RcIngredBasket);
          }else
              binding.txtLstEmpty.setVisibility(View.VISIBLE);

      }

      @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFrgBasketBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        bindingRcV_Baskets(binding.RcIngredBasket);
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CheckoutActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }

      public void bindingRcV_Baskets(RecyclerView recyclerView) {
          // Create and set adapter for RecyclerView
          Adapter_RC_RecipeDt adapter = new Adapter_RC_RecipeDt(getContext(),getActivity(),viewModel,viewModel.getBasketList(),"remote");
          LinearLayoutManager manager = new LinearLayoutManager(getContext());
          manager.setOrientation(HORIZONTAL);
          recyclerView.setLayoutManager(manager);
          recyclerView.setAdapter(adapter);
      }
}