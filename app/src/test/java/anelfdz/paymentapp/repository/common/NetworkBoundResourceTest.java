package anelfdz.paymentapp.repository.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicReference;

import androidx.room.EmptyResultSetException;
import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.utils.SchedulerProvider;
import io.reactivex.Single;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class NetworkBoundResourceTest {

    private final Foo cacheResponse = new Foo(0);
    private final String stringCacheResponse = "Bar";
    private final Foo networkResponse = new Foo(1);
    private final ResponseBody errorBody = ResponseBody
            .create(MediaType.parse("text/plain"), "error");

    private TestScheduler testScheduler;

    @Mock
    SchedulerProvider schedulerProvider;

    private Single<Foo> dbData;
    private Single<String> stringDbData;
    private Single<Foo> noDbData;
    private Single<Foo> dbError;
    private Single<Response<Foo>> successNetworkCall;
    private Single<Response<Foo>> serverErrorNetworkCall;
    private Single<Response<Foo>> networkErrorNetworkCall;

    private AtomicReference<Foo> shouldFetchArg;
    private AtomicReference<Foo> saveCallResultArg;

    @Before
    public void setUp() {
        testScheduler = new TestScheduler();
        when(schedulerProvider.io()).thenReturn(testScheduler);

        dbData = Single.just(cacheResponse);
        stringDbData = Single.just(stringCacheResponse);
        noDbData = Single.error(new EmptyResultSetException("no data"));
        dbError = Single.error(new Exception());
        successNetworkCall = Single.just(Response.success(networkResponse));
        serverErrorNetworkCall = Single.just(Response.error(500, errorBody));
        networkErrorNetworkCall = Single.error(new Exception());

        shouldFetchArg = new AtomicReference<>();
        saveCallResultArg = new AtomicReference<>();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchTrueAndSuccessNetworkCall_receiveLoadingWithNoDataAndLoadingWithDbDataAndSuccessWithNetData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbData, true, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.loading(cacheResponse),
                        Resource.success(networkResponse)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchTrueAndServerErrorNetworkCall_receiveLoadingWithNoDataAndLoadingWithDbDataAndErrorWithDbData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbData, true, serverErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.loading(cacheResponse),
                        Resource.error("Response.error()", cacheResponse)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchTrueAndSuccessNetworkCall_shouldFetchReceiveDbData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbData, true, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        assertThat(shouldFetchArg.get(), is(cacheResponse));
        subscriber
                .assertNotTerminated()
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchTrueAndServerErrorNetworkCall_shouldFetchReceiveDbData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbData, true, serverErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        assertThat(shouldFetchArg.get(), is(cacheResponse));
        subscriber
                .assertNotTerminated()
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchFalseAndSuccessNetworkCall_neverCallNetwork() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbData, false, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        assertNull(saveCallResultArg.get());
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.success(cacheResponse)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchFalseAndServerErrorNetworkCall_neverCallNetwork() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbData, false, serverErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        assertNull(saveCallResultArg.get());
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.success(cacheResponse)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_noDbDataAndShouldFetchTrueAndSuccessNetworkCall_receiveLoadingWithNoDataAndSuccessWithNetData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                noDbData, true, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.success(networkResponse)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_noDbDataAndShouldFetchTrueAndServerErrorNetworkCall_receiveLoadingWithNoDataAndErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                noDbData, true, serverErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error("Response.error()", null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbErrorAndShouldFetchTrueAndSuccessNetworkCall_receiveErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbError, true, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error(null, null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbErrorAndShouldFetchTrueAndServerErrorNetworkCall_receiveErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbError, true, serverErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error(null, null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbErrorAndShouldFetchFalseAndSuccessNetworkCall_receiveErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbError, false, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error(null, null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchTrueAndSuccessNetworkCall_receiveErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbError, false, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error(null, null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbErrorAndShouldFetchFalseAndServerErrorNetworkCall_receiveErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbError, false, serverErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error(null, null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_noDbDataAndShouldFetchFalseAndSuccessNetworkCall_receiveLoadingWithNoDataAndSuccessWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                noDbData, false, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.success(null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_noDbDataAndShouldFetchTrueAndNetworkErrorNetworkCall_receiveLoadingAndErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                noDbData, true, networkErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error(null, null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbDataAndShouldFetchTrueAndNetworkErrorNetworkCall_receiveLoadingAndErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbData, true, networkErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.loading(cacheResponse),
                        Resource.error(null, cacheResponse)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_dbErrorAndShouldFetchTrueAndNetworkErrorNetworkCall_receiveLoadingAndErrorWithNoData() {
        // Arrange
        NetworkBoundResource<Foo, Foo> SUT = getNetworkBoundResource(
                dbError, true, networkErrorNetworkCall
        );

        // Act
        TestSubscriber<Resource<Foo>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.error(null, null)
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    @Test
    public void asFlowable_requestTypeNotEqualsToResponseTypeAndDbDataAndShouldFetchTrueAndSuccessNetworkCall_receiveLoadingAndMapError() {
        // Arrange
        NetworkBoundResource<String, Foo> SUT = getNetworkBoundResourceWithDifferentTypes(
                stringDbData, successNetworkCall
        );

        // Act
        TestSubscriber<Resource<String>> subscriber = SUT.asFlowable().test();
        testScheduler.triggerActions();

        // Assert
        subscriber
                .assertNotTerminated()
                .assertValues(
                        Resource.loading(null),
                        Resource.loading(stringCacheResponse),
                        Resource.error(
                                NetworkBoundResource.ILLEGAL_ARGUMENT_EXCEPCTION_MESSAGE,
                                stringCacheResponse
                        )
                )
                .assertNoErrors();

        subscriber.dispose();
    }

    private NetworkBoundResource<Foo, Foo> getNetworkBoundResource(
            Single<Foo> dbData, boolean shouldFetch, Single<Response<Foo>> networkCall) {
        return new NetworkBoundResource<Foo, Foo>(schedulerProvider) {
            @Override
            protected void saveCallResult(Foo result) {
                saveCallResultArg.set(result);
            }

            @Override
            protected Single<Foo> loadFromDb() {
                return dbData;
            }

            @Override
            protected Single<Response<Foo>> createCall() {
                return networkCall;
            }

            @Override
            protected boolean shouldFetch(Foo data) {
                shouldFetchArg.set(data);
                return shouldFetch;
            }
        };
    }

    private NetworkBoundResource<String, Foo> getNetworkBoundResourceWithDifferentTypes(
            Single<String> dbData, Single<Response<Foo>> networkCall) {
        return new NetworkBoundResource<String, Foo>(schedulerProvider) {
            @Override
            protected void saveCallResult(Foo result) {
                saveCallResultArg.set(result);
            }

            @Override
            protected Single<String> loadFromDb() {
                return dbData;
            }

            @Override
            protected Single<Response<Foo>> createCall() {
                return networkCall;
            }

            @Override
            protected boolean shouldFetch(String data) {
                return true;
            }
        };
    }

    static class Foo {

        int value;

        Foo(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Foo foo = (Foo) o;

            return value == foo.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }
}
