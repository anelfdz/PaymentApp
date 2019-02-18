package anelfdz.paymentapp.ui.installment;

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
import anelfdz.paymentapp.R;
import anelfdz.paymentapp.binding.FragmentDataBindingComponent;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.databinding.FragmentInstallmentsBinding;
import anelfdz.paymentapp.ui.common.BaseFragment;
import anelfdz.paymentapp.ui.pay.PayDialogFragment;
import anelfdz.paymentapp.utils.AutoClearedValue;

public class InstallmentFragment extends BaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private AutoClearedValue<FragmentInstallmentsBinding> binding;

    private AutoClearedValue<InstallmentListAdapter> adapter;

    private InstallmentViewModel viewModel;

    private double amount;

    private PaymentMethod paymentMethod;

    private Bank bank;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentInstallmentsBinding dataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_installments,
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
                .get(InstallmentViewModel.class);
        binding.get().setLifecycleOwner(this);

        InstallmentListAdapter listAdapter = new InstallmentListAdapter(
                dataBindingComponent,
                this::showPayDialogFragment
        );
        adapter = new AutoClearedValue<>(this, listAdapter);

        initToolbar();
        initRecyclerView();

        binding.get().setCallback(() ->
                viewModel.loadInstallments(amount, paymentMethod.getId(), bank.getId()));
        viewModel.loadInstallments(amount, paymentMethod.getId(), bank.getId());
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
                    InstallmentFragmentArgs.fromBundle(getArguments()).getAmount()
            );
            paymentMethod = InstallmentFragmentArgs.fromBundle(getArguments()).getPaymentMethod();
            bank = InstallmentFragmentArgs.fromBundle(getArguments()).getBank();
        }
    }

    private void initRecyclerView() {
        binding.get().installmentList.setAdapter(adapter.get());
        binding.get().setInstallments(viewModel.getInstallments());
        viewModel.getInstallments().observe(this,
                result -> {
                    if (result != null)
                        adapter.get().submitList(result.getData());
                });
    }

    private void showPayDialogFragment(Installment installment) {
        PayDialogFragment
                .newInstance(amount, paymentMethod, bank, installment)
                .show(getChildFragmentManager(), "pay_dialog");
    }
}
