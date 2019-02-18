package anelfdz.paymentapp.api;

import java.util.List;

import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    String BASE_URL = "https://api.mercadopago.com/v1/";

    @GET("payment_methods")
    Single<Response<List<PaymentMethod>>> getPaymentMethods(@Query("public_key") String publicKey);


    @GET("payment_methods/card_issuers")
    Single<Response<List<Bank>>> getBanks(@Query("public_key") String publicKey,
                                          @Query("payment_method_id") String paymentMethodId);

    @GET("payment_methods/installments")
    Single<Response<List<Installment>>> getInstallments(@Query("public_key") String publicKey,
                                                        @Query("amount") double amount,
                                                        @Query("payment_method_id") String paymentMethodId,
                                                        @Query("issuer_id") String issuerId);
}
