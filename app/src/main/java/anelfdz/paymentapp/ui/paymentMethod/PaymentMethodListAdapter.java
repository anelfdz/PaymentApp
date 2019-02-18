package anelfdz.paymentapp.ui.paymentMethod;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import anelfdz.paymentapp.R;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.databinding.PaymentMethodItemBinding;
import anelfdz.paymentapp.ui.common.DataBoundListAdapter;

public class PaymentMethodListAdapter extends
        DataBoundListAdapter<PaymentMethod, PaymentMethodItemBinding> {

    private DataBindingComponent dataBindingComponent;
    private PaymentMethodClickCallback callback;

    public PaymentMethodListAdapter(DataBindingComponent dataBindingComponent,
                                    PaymentMethodClickCallback callback) {
        super(new DiffUtil.ItemCallback<PaymentMethod>() {
            @Override
            public boolean areItemsTheSame(@NonNull PaymentMethod oldItem,
                                           @NonNull PaymentMethod newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull PaymentMethod oldItem,
                                              @NonNull PaymentMethod newItem) {
                return oldItem.getPaymentType().equals(newItem.getPaymentType()) &&
                        oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getStatus().equals(newItem.getStatus()) &&
                        oldItem.getThumbnailUrl().equals(newItem.getThumbnailUrl());
            }
        });
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }

    @NonNull
    @Override
    protected PaymentMethodItemBinding createBinding(@NonNull ViewGroup parent) {
        PaymentMethodItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.payment_method_item,
                parent,
                false,
                dataBindingComponent);

        binding.item.setOnClickListener(v -> {
            if (callback != null)
                callback.onPaymentMethodClick(binding.getPaymentMethod());
        });
        return binding;
    }

    @Override
    protected void bind(@NonNull PaymentMethodItemBinding dataBinding, PaymentMethod item) {
        dataBinding.setPaymentMethod(item);
    }

    interface PaymentMethodClickCallback {
        void onPaymentMethodClick(@NonNull PaymentMethod paymentMethod);
    }
}
