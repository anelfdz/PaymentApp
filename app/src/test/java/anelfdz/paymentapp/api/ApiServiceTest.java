package anelfdz.paymentapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import anelfdz.paymentapp.data.deserializers.InstallmentDeserializer;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ApiServiceTest {

    private static final String PUBLIC_KEY = "public_key";
    private static final String PAYMENT_METHOD_ID = "visa";
    public static final double AMOUNT = 20000.0;
    public static final String ISSUER_ID = "288";
    private MockWebServer mockWebServer;

    private ApiService SUT;

    @Before
    public void setUp() {
        mockWebServer = new MockWebServer();

        Type installmentListType = new TypeToken<List<Installment>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(installmentListType, new InstallmentDeserializer())
                .create();

        SUT = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void getPaymentMethods_paymentMethodsReturned() throws IOException, InterruptedException {
        // Arrange
        enqueueResponse("payment_methods.json");

        // Act
        TestObserver<Response<List<PaymentMethod>>> observer = SUT
                .getPaymentMethods(PUBLIC_KEY)
                .test();
        RecordedRequest request = mockWebServer.takeRequest();

        // Assert
        assertThat(request.getPath(), is("/payment_methods?public_key=" + PUBLIC_KEY));
        observer.assertNoErrors();
        observer.assertComplete();

        List<PaymentMethod> paymentMethods = Arrays.asList(
                new PaymentMethod("visa", "Visa", "visa.gif", "active", "credit_card"),
                new PaymentMethod("master", "Mastercard", "master.gif", "active", "credit_card"),
                new PaymentMethod("maestro", "Maestro", "maestro.gif", "active", "debit_card")
        );

        assertThat(paymentMethods, is(observer.values().get(0).body()));
    }

    @Test
    public void getBanks_banksReturned() throws IOException, InterruptedException {
        // Arrange
        enqueueResponse("banks.json");

        // Act
        TestObserver<Response<List<Bank>>> observer = SUT
                .getBanks(PUBLIC_KEY, PAYMENT_METHOD_ID)
                .test();
        RecordedRequest request = mockWebServer.takeRequest();

        // Assert
        assertThat(request.getPath(), is("/payment_methods/card_issuers?public_key="
                + PUBLIC_KEY + "&payment_method_id=" + PAYMENT_METHOD_ID));
        observer.assertNoErrors();
        observer.assertComplete();

        List<Bank> banks = Arrays.asList(
                new Bank("288", "Tarjeta Shopping", "288.gif", null),
                new Bank("1005", "Provencred", "1005.gif", null),
                new Bank("272", "Banco Comafi", "272.gif", null)
        );

        assertThat(banks, is(observer.values().get(0).body()));
    }

    @Test
    public void getInstallments_installmentsReturned() throws IOException, InterruptedException {
        // Arrange
        enqueueResponse("installments.json");

        // Act
        TestObserver<Response<List<Installment>>> observer = SUT
                .getInstallments(PUBLIC_KEY, AMOUNT, PAYMENT_METHOD_ID, ISSUER_ID)
                .test();
        RecordedRequest request = mockWebServer.takeRequest();

        // Assert
        assertThat(request.getPath(), is("/payment_methods/installments?public_key="
                + PUBLIC_KEY + "&amount=" + AMOUNT + "&payment_method_id=" + PAYMENT_METHOD_ID
                + "&issuer_id=" + ISSUER_ID));
        observer.assertNoErrors();
        observer.assertComplete();

        List<Installment> installments = Arrays.asList(
                new Installment(1, "1 cuota de $ 20.000,00 ($ 20.000,00)", 20000, 20000),
                new Installment(3, "3 cuotas de $ 7.981,33 ($ 23.944,00)", 7981.33, 23944),
                new Installment(6, "6 cuotas de $ 4.483,00 ($ 26.898,00)", 4483, 26898)
        );

        assertThat(installments, is(observer.values().get(0).body()));
    }

    private void enqueueResponse(String fileName) throws IOException {
        enqueueResponse(fileName, Collections.emptyMap());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        ClassLoader loader = getClass().getClassLoader();

        if (loader != null) {
            InputStream inputStream = loader.getResourceAsStream("api-response/" + fileName);
            BufferedSource source = Okio.buffer(Okio.source(inputStream));
            MockResponse mockResponse = new MockResponse();

            for (Map.Entry<String, String> header : headers.entrySet()) {
                mockResponse.addHeader(header.getKey(), header.getValue());
            }

            mockWebServer.enqueue(mockResponse
                    .setBody(source.readString(StandardCharsets.UTF_8)));
        }
    }

}
