package anelfdz.paymentapp.di;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Singleton;

import androidx.room.Room;
import anelfdz.paymentapp.BuildConfig;
import anelfdz.paymentapp.api.ApiService;
import anelfdz.paymentapp.data.deserializers.InstallmentDeserializer;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.db.AppDb;
import anelfdz.paymentapp.db.BankDao;
import anelfdz.paymentapp.db.PaymentMethodDao;
import anelfdz.paymentapp.utils.SchedulerProvider;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Provides
    Context provideContext(Application app) {
        return app.getApplicationContext();
    }

    @Singleton
    @Provides
    Gson provideGson() {
        Type installmentListType = new TypeToken<List<Installment>>() {
        }.getType();
        return new GsonBuilder()
                .registerTypeAdapter(installmentListType, new InstallmentDeserializer())
                .create();
    }

    @Singleton
    @Provides
    ApiService provideApiService(Gson gson) {
        HttpLoggingInterceptor loggin = new HttpLoggingInterceptor();
        loggin.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG)
            httpClient.addInterceptor(loggin);

        return new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @Singleton
    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider() {

            @Override
            public Scheduler io() {
                return Schedulers.io();
            }

            @Override
            public Scheduler ui() {
                return AndroidSchedulers.mainThread();
            }
        };
    }

    @Singleton
    @Provides
    AppDb provideDb(Application app) {
        return Room
                .databaseBuilder(app, AppDb.class, "app.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    PaymentMethodDao providePaymentMethodDao(AppDb db) {
        return db.paymentMethodDao();
    }

    @Singleton
    @Provides
    BankDao provideBankDao(AppDb db) {
        return db.bankDao();
    }

}
