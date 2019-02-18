package anelfdz.paymentapp.repository.cacheDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anelfdz.paymentapp.data.vo.PaymentMethod;
import anelfdz.paymentapp.db.PaymentMethodDao;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Singleton
public class PaymentMethodCacheDataSource {
    private PaymentMethodDao dao;

    @Inject
    public PaymentMethodCacheDataSource(PaymentMethodDao dao) {
        this.dao = dao;
    }

    public void insert(List<PaymentMethod> list) {
        dao.insert(list);
    }

    public Single<PaymentMethod> findById(String id) {
        return dao.findById(id);
    }

    public Single<List<PaymentMethod>> findAll() {
        return dao.findAll();
    }

    public Single<List<PaymentMethod>> findAllActive() {
        return dao.findAllActives();
    }
}
