package anelfdz.paymentapp.repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.repository.cacheDataSource.PaymentMethodCacheDataSource;
import anelfdz.paymentapp.repository.common.NetworkBoundResource;
import anelfdz.paymentapp.repository.networkDataSource.PaymentMethodNetworkDataSource;
import anelfdz.paymentapp.utils.RateLimiter;
import anelfdz.paymentapp.utils.SchedulerProvider;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;

@Singleton
public class PaymentMethodRepository {

    private final SchedulerProvider schedulerProvider;
    private final PaymentMethodCacheDataSource cacheDataSource;
    private final PaymentMethodNetworkDataSource networkDataSource;

    private final int LOAD_PAYMENT_METHODS = 1;
    private RateLimiter<Integer> paymentMethodsRateLimit =
            new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    PaymentMethodRepository(SchedulerProvider schedulerProvider,
                            PaymentMethodCacheDataSource cacheDataSource,
                            PaymentMethodNetworkDataSource networkDataSource) {
        this.schedulerProvider = schedulerProvider;
        this.cacheDataSource = cacheDataSource;
        this.networkDataSource = networkDataSource;
    }

    public Flowable<Resource<List<PaymentMethod>>> getActivePaymentMethods() {
        return new NetworkBoundResource<List<PaymentMethod>, List<PaymentMethod>>(schedulerProvider) {

            @Override
            protected void saveCallResult(List<PaymentMethod> result) {
                cacheDataSource.insert(result);
            }

            @Override
            protected Single<List<PaymentMethod>> loadFromDb() {
                return cacheDataSource.findAllActive();
            }

            @Override
            protected Single<Response<List<PaymentMethod>>> createCall() {
                return networkDataSource.getPaymentMethods();
            }

            @Override
            protected boolean shouldFetch(List<PaymentMethod> data) {
                return data == null || data.isEmpty() ||
                        paymentMethodsRateLimit.shouldFetch(LOAD_PAYMENT_METHODS);
            }

            @Override
            protected Observable<Response<List<PaymentMethod>>> filter(
                    Observable<Response<List<PaymentMethod>>> response) {
                return response
                        .map(Response::body)
                        .flatMapIterable(it -> it)
                        .filter(paymentMethod ->
                                paymentMethod.getPaymentType()
                                        .equals(PaymentMethod.CREDIT_CARD_TYPE))
                        .toList()
                        .map(Response::success)
                        .toObservable();
            }
        }.asFlowable();
    }
}