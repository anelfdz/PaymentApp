package anelfdz.paymentapp.di;

import anelfdz.paymentapp.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    public abstract MainActivity contributeMainActivity();
}

