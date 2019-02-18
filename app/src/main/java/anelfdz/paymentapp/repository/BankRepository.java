package anelfdz.paymentapp.repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.VisibleForTesting;
import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.repository.cacheDataSource.BankCacheDataSource;
import anelfdz.paymentapp.repository.common.NetworkBoundResource;
import anelfdz.paymentapp.repository.networkDataSource.BankNetworkDataSource;
import anelfdz.paymentapp.utils.RateLimiter;
import anelfdz.paymentapp.utils.SchedulerProvider;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Response;

@Singleton
public class BankRepository {
    private final SchedulerProvider schedulerProvider;
    private final BankCacheDataSource cacheDataSource;
    private final BankNetworkDataSource networkDataSource;

    @VisibleForTesting
    static final int LOAD_BANKS = 1;

    private RateLimiter<Integer> banksRateLimit;

    @Inject
    BankRepository(SchedulerProvider schedulerProvider, BankCacheDataSource cacheDataSource,
                   BankNetworkDataSource networkDataSource) {
        this.schedulerProvider = schedulerProvider;
        this.cacheDataSource = cacheDataSource;
        this.networkDataSource = networkDataSource;
        this.banksRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);
    }

    @VisibleForTesting
    BankRepository(SchedulerProvider schedulerProvider, BankCacheDataSource cacheDataSource,
                   BankNetworkDataSource networkDataSource, RateLimiter<Integer> rateLimiter) {
        this.schedulerProvider = schedulerProvider;
        this.cacheDataSource = cacheDataSource;
        this.networkDataSource = networkDataSource;
        this.banksRateLimit = rateLimiter;
    }

    public Flowable<Resource<List<Bank>>> getBanks(String paymentMethodId) {
        return new NetworkBoundResource<List<Bank>, List<Bank>>(schedulerProvider) {

            @Override
            protected void saveCallResult(List<Bank> result) {
                for (Bank bank : result)
                    bank.setPaymentMethodId(paymentMethodId);

                cacheDataSource.insert(result);
            }

            @Override
            protected Single<List<Bank>> loadFromDb() {
                return cacheDataSource.findByPaymentMethodId(paymentMethodId);
            }

            @Override
            protected Single<Response<List<Bank>>> createCall() {
                return networkDataSource.getBanks(paymentMethodId);
            }

            @Override
            protected boolean shouldFetch(List<Bank> data) {
                return data == null || data.isEmpty() ||
                        banksRateLimit.shouldFetch(LOAD_BANKS);
            }
        }.asFlowable();
    }
}