package anelfdz.paymentapp.ui.installment;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.data.vo.Installment;
import anelfdz.paymentapp.repository.InstallmentRepository;
import anelfdz.paymentapp.utils.AbsentLiveData;

public class InstallmentViewModel extends ViewModel {

    private MutableLiveData<InstallmentRequest> installmentRequest = new MutableLiveData<>();

    private final LiveData<Resource<List<Installment>>> installments;

    @Inject
    public InstallmentViewModel(InstallmentRepository installmentRepository) {
        installments = Transformations.switchMap(installmentRequest,
                request -> {
                    if (request == null)
                        return AbsentLiveData.create();
                    else
                        return LiveDataReactiveStreams
                                .fromPublisher(
                                        installmentRepository.getInstallments(
                                                request.amount,
                                                request.paymentMethodId,
                                                request.issuerId
                                        )
                                );
                });
    }

    public void loadInstallments(double amount, String paymentMethodId, String issuerId) {
        InstallmentRequest request = new InstallmentRequest(amount, paymentMethodId, issuerId);
        if (this.installmentRequest.getValue() == null ||
                !this.installmentRequest.getValue().equals(request)) {
            this.installmentRequest.setValue(request);
        }
    }

    public LiveData<Resource<List<Installment>>> getInstallments() {
        return installments;
    }

    private class InstallmentRequest {
        double amount;
        String paymentMethodId;
        String issuerId;

        InstallmentRequest(double amount, String paymentMethodId, String issuerId) {
            this.amount = amount;
            this.paymentMethodId = paymentMethodId;
            this.issuerId = issuerId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InstallmentRequest that = (InstallmentRequest) o;

            if (Double.compare(that.amount, amount) != 0) return false;
            if (!paymentMethodId.equals(that.paymentMethodId)) return false;
            return issuerId.equals(that.issuerId);
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(amount);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + paymentMethodId.hashCode();
            result = 31 * result + issuerId.hashCode();
            return result;
        }
    }
}
