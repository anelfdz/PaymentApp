package anelfdz.paymentapp.repository.cacheDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.db.BankDao;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Singleton
public class BankCacheDataSource {
    private BankDao dao;

    @Inject
    public BankCacheDataSource(BankDao dao) {
        this.dao = dao;
    }

    public void insert(List<Bank> list) {
        dao.insert(list);
    }

    public Single<Bank> findById(String id) {
        return dao.findById(id);
    }

    public Single<List<Bank>> findAll() {
        return dao.findAll();
    }

    public Single<List<Bank>> findByPaymentMethodId(String paymentMethodId) {
        return dao.findByPaymentMethodId(paymentMethodId);
    }

}
