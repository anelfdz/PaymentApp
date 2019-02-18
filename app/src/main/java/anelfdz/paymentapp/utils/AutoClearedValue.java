package anelfdz.paymentapp.utils;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class AutoClearedValue<T> {
    private T value;

    public AutoClearedValue(Fragment fragment, T value) {
        fragment.getLifecycle().addObserver(new LifecycleObserver() {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                AutoClearedValue.this.value = null;
            }
        });
        this.value = value;
    }

    public T get() {
        return value;
    }
}
