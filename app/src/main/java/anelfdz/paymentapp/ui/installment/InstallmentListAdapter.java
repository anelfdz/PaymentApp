package anelfdz.paymentapp.ui.installment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import anelfdz.paymentapp.R;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.databinding.InstallmentItemBinding;
import anelfdz.paymentapp.ui.common.DataBoundListAdapter;

public class InstallmentListAdapter extends
        DataBoundListAdapter<Installment, InstallmentItemBinding> {

    private DataBindingComponent dataBindingComponent;
    private InstallmentClickCallback callback;

    public InstallmentListAdapter(DataBindingComponent dataBindingComponent,
                                  InstallmentClickCallback callback) {
        super(new DiffUtil.ItemCallback<Installment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Installment oldItem,
                                           @NonNull Installment newItem) {
                return oldItem.getInstallments() == newItem.getInstallments();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Installment oldItem,
                                              @NonNull Installment newItem) {
                return oldItem.getInstallments() == newItem.getInstallments() &&
                        oldItem.getRecommendedMessage().equals(newItem.getRecommendedMessage()) &&
                        oldItem.getInstallmentAmount() == newItem.getInstallmentAmount() &&
                        oldItem.getTotalAmount() == newItem.getTotalAmount();
            }
        });
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }

    @NonNull
    @Override
    protected InstallmentItemBinding createBinding(@NonNull ViewGroup parent) {
        InstallmentItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.installment_item,
                parent,
                false,
                dataBindingComponent);

        binding.item.setOnClickListener(v -> {
            if (callback != null)
                callback.onInstallmentClick(binding.getInstallment());
        });
        return binding;
    }

    @Override
    protected void bind(@NonNull InstallmentItemBinding dataBinding, Installment item) {
        dataBinding.setInstallment(item);
    }

    interface InstallmentClickCallback {
        void onInstallmentClick(@NonNull Installment Installment);
    }
}
