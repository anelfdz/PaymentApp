package anelfdz.paymentapp.ui.paymentMethod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import anelfdz.paymentapp.R;
import anelfdz.paymentapp.binding.FragmentDataBindingComponent;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.databinding.FragmentPaymentMethodsBinding;
import anelfdz.paymentapp.ui.common.BaseFragment;
import anelfdz.paymentapp.utils.AutoClearedValue;

public class PaymentMethodFragment extends BaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private AutoClearedValue<FragmentPaymentMethodsBinding> binding;

    private AutoClearedValue<PaymentMethodListAdapter> adapter;

    private PaymentMethodsViewModel viewModel;

    private double amount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPaymentMethodsBinding dataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_payment_methods,
                container,
                false,
                dataBindingComponent
        );
        binding = new AutoClearedValue<>(this, dataBinding);
        return binding.get().getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavArguments();

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PaymentMethodsViewModel.class);
        binding.get().setLifecycleOwner(this);

        PaymentMethodListAdapter listAdapter = new PaymentMethodListAdapter(
                dataBindingComponent,
                this::navigateToBankFragment
        );
        adapter = new AutoClearedValue<>(this, listAdapter);

        initToolbar();
        initRecyclerView();

        binding.get().setCallback(() -> viewModel.loadPaymentMethods());
        viewModel.loadPaymentMethods();
    }

    private void navigateToBankFragment(PaymentMethod paymentMethod) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(
                PaymentMethodFragmentDirections
                        .showBanks(String.valueOf(amount), paymentMethod)
        );
    }

    private void initToolbar() {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getNavArguments() {
        if (getArguments() != null) {
            amount = Double.parseDouble(
                    PaymentMethodFragmentArgs.fromBundle(getArguments()).getAmount()
            );
        }
    }

    private void initRecyclerView() {
        binding.get().paymentMethodList.setAdapter(adapter.get());
        binding.get().setPaymentMethods(viewModel.getPaymentMethods());
        viewModel.getPaymentMethods().observe(this,
                result -> {
                    if (result != null)
                        adapter.get().submitList(result.getData());
                });
    }
}
