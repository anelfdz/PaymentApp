package anelfdz.paymentapp.di;

import anelfdz.paymentapp.ui.amount.AmountFragment;
import anelfdz.paymentapp.ui.bank.BankFragment;
import anelfdz.paymentapp.ui.installment.InstallmentFragment;
import anelfdz.paymentapp.ui.pay.PayDialogFragment;
import anelfdz.paymentapp.ui.paymentMethod.PaymentMethodFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    public abstract AmountFragment contributeAmountFragment();

    @ContributesAndroidInjector
    public abstract PaymentMethodFragment contributePaymentMethodsFragment();

    @ContributesAndroidInjector
    public abstract InstallmentFragment contributeInstallmentFragment();

    @ContributesAndroidInjector
    public abstract PayDialogFragment contributePayDialogFragment();

    @ContributesAndroidInjector
    public abstract BankFragment contributeBankFragment();
}
