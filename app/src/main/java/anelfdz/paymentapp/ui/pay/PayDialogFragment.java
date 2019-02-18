package anelfdz.paymentapp.ui.pay;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import anelfdz.paymentapp.R;
import anelfdz.paymentapp.binding.FragmentDataBindingComponent;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.databinding.FragmentPayBinding;
import anelfdz.paymentapp.utils.AutoClearedValue;
import dagger.android.support.AndroidSupportInjection;

public class PayDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_PAYMENT_METHOD = "payment_method";
    private static final String ARG_BANK = "bank";
    private static final String ARG_INSTALLMENT = "installment";

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private AutoClearedValue<FragmentPayBinding> binding;

    private double amount;
    private PaymentMethod paymentMethod;
    private Bank bank;
    private Installment installment;

    public static PayDialogFragment newInstance(double amount, PaymentMethod paymentMethod,
                                                Bank bank, Installment installment) {
        final PayDialogFragment fragment = new PayDialogFragment();

        final Bundle args = new Bundle();
        args.putDouble(ARG_AMOUNT, amount);
        args.putParcelable(ARG_PAYMENT_METHOD, paymentMethod);
        args.putParcelable(ARG_BANK, bank);
        args.putParcelable(ARG_INSTALLMENT, installment);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentPayBinding dataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_pay,
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

        bind();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    private void getNavArguments() {
        if (getArguments() != null) {
            amount = getArguments().getDouble(ARG_AMOUNT);
            paymentMethod = getArguments().getParcelable(ARG_PAYMENT_METHOD);
            bank = getArguments().getParcelable(ARG_BANK);
            installment = getArguments().getParcelable(ARG_INSTALLMENT);
        }
    }

    private void bind() {
        binding.get().setAmount(amount);
        binding.get().setPaymentMethod(paymentMethod);
        binding.get().setBank(bank);
        binding.get().setInstallment(installment);
        binding.get().executePendingBindings();
    }

}
