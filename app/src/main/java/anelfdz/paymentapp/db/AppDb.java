package anelfdz.paymentapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.data.vo.PaymentMethod;

@Database(
        entities = {PaymentMethod.class, Bank.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDb extends RoomDatabase {

    public abstract PaymentMethodDao paymentMethodDao();

    public abstract BankDao bankDao();
}
