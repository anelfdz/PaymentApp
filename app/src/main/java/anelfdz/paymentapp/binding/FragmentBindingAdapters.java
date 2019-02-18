package anelfdz.paymentapp.binding;

import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

public final class FragmentBindingAdapters {
    private final Fragment fragment;

    @BindingAdapter({"imageUrl"})
    public final void bindImage(SimpleDraweeView imageView, @Nullable String url) {
        imageView.setImageURI(url);
    }

    @BindingAdapter({"currencyAmount"})
    public final void bindCurrencyAmount(TextView textView, @Nullable Double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        nf.setMaximumFractionDigits(0);

        textView.setText(nf.format(amount));
    }

    public final Fragment getFragment() {
        return this.fragment;
    }

    @Inject
    public FragmentBindingAdapters(Fragment fragment) {
        this.fragment = fragment;
    }
}
