package anelfdz.paymentapp.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import anelfdz.paymentapp.data.vo.Bank;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface BankDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bank... banks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Bank> list);

    @Query("SELECT * FROM bank WHERE id = :id")
    Single<Bank> findById(String id);

    @Query("SELECT * FROM bank WHERE paymentMethodId = :paymentMethodId")
    Single<List<Bank>> findByPaymentMethodId(String paymentMethodId);

    @Query("SELECT * FROM bank")
    Single<List<Bank>> findAll();
}
