package anelfdz.paymentapp.ui.paymentMethod;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.repository.PaymentMethodRepository;
import anelfdz.paymentapp.utils.AbsentLiveData;

public class PaymentMethodsViewModel extends ViewModel {

    private MutableLiveData<Boolean> request = new MutableLiveData<>();

    private final LiveData<Resource<List<PaymentMethod>>> paymentMethods;

    @Inject
    public PaymentMethodsViewModel(PaymentMethodRepository paymentMethodRepository) {
        paymentMethods = Transformations.switchMap(request,
                request -> {
                    if (request == null || !request)
                        return AbsentLiveData.create();
                    else
                        return LiveDataReactiveStreams
                                .fromPublisher(paymentMethodRepository.getActivePaymentMethods());
                });
    }

    public void loadPaymentMethods() {
        request.setValue(true);
    }

    public LiveData<Resource<List<PaymentMethod>>> getPaymentMethods() {
        return paymentMethods;
    }
}
