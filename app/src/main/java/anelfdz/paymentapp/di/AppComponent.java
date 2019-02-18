package anelfdz.paymentapp.di;

import android.app.Application;

import javax.inject.Singleton;

import anelfdz.paymentapp.App;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, AppModule.class, ActivityModule.class})
public interface AppComponent {
    void inject(App app);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application app);

        AppComponent build();
    }
}