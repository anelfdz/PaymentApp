package anelfdz.paymentapp.ui.amount;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import anelfdz.paymentapp.R;
import anelfdz.paymentapp.binding.FragmentDataBindingComponent;
import anelfdz.paymentapp.databinding.FragmentAmountBinding;
import anelfdz.paymentapp.ui.common.BaseFragment;
import anelfdz.paymentapp.utils.AutoClearedValue;

public class AmountFragment extends BaseFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private AutoClearedValue<FragmentAmountBinding> binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentAmountBinding dataBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_amount,
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
        binding.get().submitButton.setOnClickListener(v -> onSubmit());

        initAmountEditText();
    }

    private void initAmountEditText() {
        final EditText editText = binding.get().inputAmountView.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(s)) {
                        editText.removeTextChangedListener(this);
                        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
                        nf.setMaximumFractionDigits(0);

                        double number = getAmount(s);

                        String formatted = nf.format(number);
                        editText.setText(formatted);
                        editText.setSelection(formatted.length());

                        editText.addTextChangedListener(this);
                    }
                }
            });

            editText.setOnEditorActionListener((v, action, event) -> {
                if (action == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(v);
                    onSubmit();
                }

                return false;
            });
        }
    }

    private Double getAmount(@NonNull Editable s) {
        String cleanString = s.toString().replaceAll("[$,.]", "");
        return Double.parseDouble(cleanString);
    }

    private void hideKeyboard(View v) {
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void onSubmit() {
        final EditText editText = binding.get().inputAmountView.getEditText();

        if (editText != null) {
            Editable s = editText.getText();

            if (s != null) {
                double amount = getAmount(s);
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(
                        AmountFragmentDirections.showPaymentMethods(String.valueOf(amount))
                );
            }
        }
    }
}
