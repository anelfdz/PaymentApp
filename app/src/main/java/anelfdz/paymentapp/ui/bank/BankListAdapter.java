package anelfdz.paymentapp.ui.bank;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import anelfdz.paymentapp.R;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.databinding.BankItemBinding;
import anelfdz.paymentapp.ui.common.DataBoundListAdapter;

public class BankListAdapter extends DataBoundListAdapter<Bank, BankItemBinding> {

    private DataBindingComponent dataBindingComponent;
    private BankClickCallback callback;

    public BankListAdapter(DataBindingComponent dataBindingComponent,
                           BankClickCallback callback) {
        super(new DiffUtil.ItemCallback<Bank>() {
            @Override
            public boolean areItemsTheSame(@NonNull Bank oldItem,
                                           @NonNull Bank newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Bank oldItem,
                                              @NonNull Bank newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getPaymentMethodId().equals(newItem.getPaymentMethodId()) &&
                        oldItem.getThumbnailUrl().equals(newItem.getThumbnailUrl());
            }
        });
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }

    @NonNull
    @Override
    protected BankItemBinding createBinding(@NonNull ViewGroup parent) {
        BankItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.bank_item,
                parent,
                false,
                dataBindingComponent);

        binding.item.setOnClickListener(v -> {
            if (callback != null)
                callback.onBankClick(binding.getBank());
        });
        return binding;
    }

    @Override
    protected void bind(@NonNull BankItemBinding dataBinding, Bank item) {
        dataBinding.setBank(item);
    }

    interface BankClickCallback {
        void onBankClick(@NonNull Bank bank);
    }
}
