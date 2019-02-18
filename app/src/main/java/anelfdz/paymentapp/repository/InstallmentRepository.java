package anelfdz.paymentapp.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.room.EmptyResultSetException;
import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.repository.common.NetworkBoundResource;
import anelfdz.paymentapp.repository.networkDataSource.InstallmentNetworkDataSource;
import anelfdz.paymentapp.utils.SchedulerProvider;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Response;

@Singleton
public class InstallmentRepository {

    private final SchedulerProvider schedulerProvider;
    private final InstallmentNetworkDataSource networkDataSource;

    @Inject
    InstallmentRepository(SchedulerProvider schedulerProvider,
                          InstallmentNetworkDataSource networkDataSource) {
        this.schedulerProvider = schedulerProvider;
        this.networkDataSource = networkDataSource;
    }

    public Flowable<Resource<List<Installment>>> getInstallments(double amount,
                                                                 String paymentMethodId,
                                                                 String issuerId) {
        return new NetworkBoundResource<List<Installment>, List<Installment>>(schedulerProvider) {

            @Override
            protected void saveCallResult(List<Installment> result) {

            }

            @Override
            protected Single<List<Installment>> loadFromDb() {
                return Single.error(new EmptyResultSetException("no data"));
            }

            @Override
            protected Single<Response<List<Installment>>> createCall() {
                return networkDataSource.getInstallments(amount, paymentMethodId, issuerId);
            }

            @Override
            protected boolean shouldFetch(List<Installment> data) {
                return true;
            }
        }.asFlowable();
    }
}