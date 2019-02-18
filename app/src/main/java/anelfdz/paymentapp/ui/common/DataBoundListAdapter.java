package anelfdz.paymentapp.ui.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public abstract class DataBoundListAdapter<T, V extends ViewDataBinding> extends
        ListAdapter<T, DataBoundViewHolder<V>> {

    public DataBoundListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(new AsyncDifferConfig.Builder<>(diffCallback).build());
    }

    @NonNull
    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        V binding = this.createBinding(parent);
        return new DataBoundViewHolder<>(binding);
    }

    @NonNull
    protected abstract V createBinding(@NonNull ViewGroup parent);

    public void onBindViewHolder(@NonNull DataBoundViewHolder<V> holder, int position) {
        bind(holder.getBinding(), getItem(position));
        holder.getBinding().executePendingBindings();
    }

    protected abstract void bind(@NonNull V dataBinding, T item);
}