package anelfdz.paymentapp.repository.networkDataSource;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anelfdz.paymentapp.R;
import anelfdz.paymentapp.api.ApiService;
import anelfdz.paymentapp.data.vo.Installment;
import io.reactivex.Single;
import retrofit2.Response;

@Singleton
public class InstallmentNetworkDataSource {

    private Context context;
    private ApiService apiService;

    @Inject
    public InstallmentNetworkDataSource(Context context, ApiService apiService) {
        this.context = context;
        this.apiService = apiService;
    }

    public Single<Response<List<Installment>>> getInstallments(double amount,
                                                               String paymentMethodId,
                                                               String issuerId) {
        return apiService.getInstallments(
                context.getString(R.string.public_key),
                amount,
                paymentMethodId,
                issuerId
        );
    }
}
