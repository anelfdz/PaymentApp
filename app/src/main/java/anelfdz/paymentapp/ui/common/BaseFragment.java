package anelfdz.paymentapp.ui.common;

import android.content.Context;

import androidx.fragment.app.Fragment;
import dagger.android.support.AndroidSupportInjection;

public class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
