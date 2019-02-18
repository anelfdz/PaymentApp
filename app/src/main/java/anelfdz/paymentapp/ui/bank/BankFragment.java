package anelfdz.paymentapp.ui.bank;

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
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.databinding.FragmentBanksBinding;
import anelfdz.paymentapp.ui.common.BaseFragment;
import anelfdz.paymentapp.utils.AutoClearedValue;

public class BankFragment extends BaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private AutoClearedValue<FragmentBanksBinding> binding;

    private AutoClearedValue<BankListAdapter> adapter;

    private BankViewModel viewModel;

    private double amount;

    private PaymentMethod paymentMethod;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentBanksBinding dataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_banks,
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
                .get(BankViewModel.class);
        binding.get().setLifecycleOwner(this);

        BankListAdapter listAdapter = new BankListAdapter(
                dataBindingComponent,
                this::navigateToInstallmentFragment
        );
        adapter = new AutoClearedValue<>(this, listAdapter);

        initToolbar();
        initRecyclerView();

        binding.get().setCallback(() -> viewModel.loadBanks(paymentMethod.getId()));
        viewModel.loadBanks(paymentMethod.getId());
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
                    BankFragmentArgs.fromBundle(getArguments()).getAmount()
            );
            paymentMethod = BankFragmentArgs.fromBundle(getArguments()).getPaymentMethod();
        }
    }

    private void initRecyclerView() {
        binding.get().bankList.setAdapter(adapter.get());
        binding.get().setBanks(viewModel.getBanks());
        viewModel.getBanks().observe(this,
                result -> {
                    if (result != null)
                        adapter.get().submitList(result.getData());
                });
    }

    private void navigateToInstallmentFragment(Bank bank) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(
                BankFragmentDirections
                        .showInstallments(String.valueOf(amount), paymentMethod, bank)
        );
    }
}
