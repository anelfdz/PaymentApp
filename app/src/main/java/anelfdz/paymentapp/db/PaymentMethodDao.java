package anelfdz.paymentapp.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import anelfdz.paymentapp.data.vo.PaymentMethod;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface PaymentMethodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PaymentMethod... paymentMethod);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<PaymentMethod> list);

    @Query("SELECT * FROM paymentmethod WHERE id = :id")
    Single<PaymentMethod> findById(String id);

    @Query("SELECT * FROM paymentmethod")
    Single<List<PaymentMethod>> findAll();

    @Query("SELECT * FROM paymentmethod WHERE status='active'")
    Single<List<PaymentMethod>> findAllActives();
}
