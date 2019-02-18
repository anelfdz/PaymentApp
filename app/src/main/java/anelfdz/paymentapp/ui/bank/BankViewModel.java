package anelfdz.paymentapp.ui.bank;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.repository.BankRepository;
import anelfdz.paymentapp.utils.AbsentLiveData;

public class BankViewModel extends ViewModel {

    private MutableLiveData<String> paymentMethodId = new MutableLiveData<>();

    private final LiveData<Resource<List<Bank>>> banks;

    @Inject
    public BankViewModel(BankRepository bankRepository) {
        banks = Transformations.switchMap(paymentMethodId,
                paymentMethodId -> {
                    if (paymentMethodId == null)
                        return AbsentLiveData.create();
                    else
                        return LiveDataReactiveStreams
                                .fromPublisher(bankRepository.getBanks(paymentMethodId));
                });
    }

    public void loadBanks(String paymentMethodId) {
        if (this.paymentMethodId.getValue() == null ||
                !this.paymentMethodId.getValue().equals(paymentMethodId)) {
            this.paymentMethodId.setValue(paymentMethodId);
        }
    }

    public LiveData<Resource<List<Bank>>> getBanks() {
        return banks;
    }
}
