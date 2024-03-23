  package com.example.notecook.Fragement;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.example.notecook.Utils.Constants.Basket_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.R;
import com.example.notecook.databinding.FragmentFrgBasketBinding;

  public class Frg_Basket extends Fragment {

    private FragmentFrgBasketBinding binding;

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
          if(Basket_list.size()!=0)
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
        bindingRcV_Baskets(binding.RcIngredBasket);
        return binding.getRoot();
    }

      public void bindingRcV_Baskets(RecyclerView recyclerView) {
          // Create and set adapter for RecyclerView
          Adapter_RC_RecipeDt adapter = new Adapter_RC_RecipeDt(getContext(),getActivity(),Basket_list,"remote");
          LinearLayoutManager manager = new LinearLayoutManager(getContext());
          manager.setOrientation(HORIZONTAL);
          recyclerView.setLayoutManager(manager);
          recyclerView.setAdapter(adapter);
      }
}