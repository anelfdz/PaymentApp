package anelfdz.paymentapp.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import anelfdz.paymentapp.ui.bank.BankViewModel;
import anelfdz.paymentapp.ui.installment.InstallmentViewModel;
import anelfdz.paymentapp.ui.paymentMethod.PaymentMethodsViewModel;
import anelfdz.paymentapp.viewModel.AppViewModelFactory;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PaymentMethodsViewModel.class)
    public abstract ViewModel bindPaymentMehtodsViewModel(
            PaymentMethodsViewModel paymentMethodsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(BankViewModel.class)
    public abstract ViewModel bindBankViewModel(BankViewModel bankViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(InstallmentViewModel.class)
    public abstract ViewModel bindInstallmentViewModel(InstallmentViewModel installmentViewModel);

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(
            AppViewModelFactory appViewModelFactory);
}
