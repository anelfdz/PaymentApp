package anelfdz.paymentapp.repository.networkDataSource;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anelfdz.paymentapp.R;
import anelfdz.paymentapp.api.ApiService;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import io.reactivex.Single;
import retrofit2.Response;

@Singleton
public class PaymentMethodNetworkDataSource {

    private Context context;
    private ApiService apiService;

    @Inject
    public PaymentMethodNetworkDataSource(Context context, ApiService apiService) {
        this.context = context;
        this.apiService = apiService;
    }

    public Single<Response<List<PaymentMethod>>> getPaymentMethods(){
        return apiService.getPaymentMethods(context.getString(R.string.public_key));
    }
}
