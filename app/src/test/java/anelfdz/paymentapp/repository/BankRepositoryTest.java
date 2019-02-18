package anelfdz.paymentapp.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.data.vo.Bank;
import anelfdz.paymentapp.repository.cacheDataSource.BankCacheDataSource;
import anelfdz.paymentapp.repository.networkDataSource.BankNetworkDataSource;
import anelfdz.paymentapp.utils.RateLimiter;
import anelfdz.paymentapp.utils.SchedulerProvider;
import io.reactivex.Single;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankRepositoryTest {

    private final String paymentMethodId = "visa";

    @Mock
    SchedulerProvider schedulerProvider;

    @Mock
    BankCacheDataSource bankCacheDataSourceMock;

    @Mock
    BankNetworkDataSource bankNetworkDataSourceMock;

    @Mock
    RateLimiter<Integer> rateLimiter;

    @Captor
    ArgumentCaptor<List<Bank>> banksCaptor;

    @Captor
    ArgumentCaptor<String> paymentMethodIdCaptor;

    private BankRepository SUT;

    private TestScheduler testScheduler;

    @Before
    public void setUp() {
        testScheduler = new TestScheduler();
        when(schedulerProvider.io()).thenReturn(testScheduler);

        SUT = new BankRepository(schedulerProvider, bankCacheDataSourceMock,
                bankNetworkDataSourceMock, rateLimiter);
    }

    @Test
    public void getBanks_dbData_findByPaymentMethodIdCalledWithPaymentMethodId() {
        // Arrange

        // Act
        TestSubscriber<Resource<List<Bank>>> test = SUT.getBanks(paymentMethodId).test();
        testScheduler.triggerActions();

        // Assert
        verify(bankCacheDataSourceMock).findByPaymentMethodId(paymentMethodIdCaptor.capture());
        verifyNoMoreInteractions(bankCacheDataSourceMock);
        assertThat(paymentMethodId, is(paymentMethodIdCaptor.getValue()));

        test.dispose();
    }

    @Test
    public void getBanks_dbDataAndNetDataAndRateLimiterTrue_getBanksCalledWithPaymentMethodId() {
        // Arrange
        when(bankCacheDataSourceMock.findByPaymentMethodId(paymentMethodId))
                .thenReturn(Single.just(Collections.singletonList(new Bank())));
        when(bankNetworkDataSourceMock.getBanks(paymentMethodId))
                .thenReturn(Single.just(Response.success(Collections.singletonList(new Bank()))));
        when(rateLimiter.shouldFetch(BankRepository.LOAD_BANKS))
                .thenReturn(true);
        // Act
        TestSubscriber<Resource<List<Bank>>> test = SUT.getBanks(paymentMethodId).test();
        testScheduler.triggerActions();

        // Assert
        verify(bankNetworkDataSourceMock).getBanks(paymentMethodId);
        verifyNoMoreInteractions(bankNetworkDataSourceMock);

        test.dispose();
    }

    @Test
    public void getBanks_dbDataAndRateLimiterFalse_getBanksNeverCalled() {
        // Arrange
        when(bankCacheDataSourceMock.findByPaymentMethodId(paymentMethodId))
                .thenReturn(Single.just(Collections.singletonList(new Bank())));
        when(rateLimiter.shouldFetch(BankRepository.LOAD_BANKS))
                .thenReturn(false);
        // Act
        TestSubscriber<Resource<List<Bank>>> test = SUT.getBanks(paymentMethodId).test();
        testScheduler.triggerActions();

        // Assert
        verifyZeroInteractions(bankNetworkDataSourceMock);

        test.dispose();
    }

    @Test
    public void getBanks_dbDataAndNetDataAndRateLimiterTrue_insertCalledWithNetData() {
        // Arrange
        List<Bank> dbData = Collections.singletonList(
                new Bank("1", "name", "url", "payId")
        );
        List<Bank> netData = Collections.singletonList(
                new Bank("2", "name", "url", "payId")
        );
        when(bankCacheDataSourceMock.findByPaymentMethodId(paymentMethodId))
                .thenReturn(Single.just(dbData));
        when(bankNetworkDataSourceMock.getBanks(paymentMethodId))
                .thenReturn(Single.just(Response.success(netData)));
        when(rateLimiter.shouldFetch(BankRepository.LOAD_BANKS))
                .thenReturn(true);
        // Act
        TestSubscriber<Resource<List<Bank>>> test = SUT.getBanks(paymentMethodId).test();
        testScheduler.triggerActions();

        // Assert
        verify(bankCacheDataSourceMock).insert(banksCaptor.capture());
        assertThat(netData, is(banksCaptor.getValue()));

        test.dispose();
    }
}
