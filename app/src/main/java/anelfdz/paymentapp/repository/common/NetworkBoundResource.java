package anelfdz.paymentapp.repository.common;

import android.annotation.SuppressLint;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.room.EmptyResultSetException;
import anelfdz.paymentapp.data.Resource;
import anelfdz.paymentapp.utils.SchedulerProvider;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Response;

public abstract class NetworkBoundResource<ResultType, RequestType> {

    final static String ILLEGAL_ARGUMENT_EXCEPCTION_MESSAGE = "when request type and result type " +
            "are different you need to override mapResponse implementation";

    private PublishSubject<Resource<ResultType>> result = PublishSubject.create();

    private final SchedulerProvider schedulerProvider;

    protected NetworkBoundResource(SchedulerProvider schedulerProvider) {
        this.schedulerProvider = schedulerProvider;
    }

    private void init() {
        loadFromDb()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.io())
                .subscribe(new SingleObserver<ResultType>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResultType data) {
                        if (shouldFetch(data)) {
                            result.onNext(Resource.loading(data));
                            fetchFromNetwork(data);
                        } else {
                            result.onNext(Resource.success(data));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof EmptyResultSetException) {
                            if (shouldFetch(null)) {
                                fetchFromNetwork(null);
                            } else {
                                result.onNext(Resource.success(null));
                            }
                        } else {
                            result.onNext(Resource.error(e.getMessage(), null));
                        }
                    }
                });
    }

    public Flowable<Resource<ResultType>> asFlowable() {
        return result.hide()
                .toFlowable(BackpressureStrategy.BUFFER)
                .doOnSubscribe(subscription -> init())
                .startWith(Resource.loading(null));
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void fetchFromNetwork(ResultType dbData) {
        Observable<Response<RequestType>> observable = createCall()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.io())
                .toObservable();

        filter(observable)
                .subscribe(
                        response -> onNetworkSuccess(response, dbData),
                        error -> onError(error, dbData)
                );
    }

    protected Observable<Response<RequestType>> filter(Observable<Response<RequestType>> response) {
        return response;
    }

    private ResultType mapResponse(RequestType netData) {
        try {
            //noinspection unchecked
            Class<ResultType> clazz = getResultTypeClass();
            return clazz.cast(netData);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPCTION_MESSAGE);
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected void onFetchFailed() {

    }

    protected abstract void saveCallResult(RequestType result);

    protected abstract Single<ResultType> loadFromDb();

    protected abstract Single<Response<RequestType>> createCall();

    protected abstract boolean shouldFetch(ResultType data);

    private RequestType processResponse(Response<RequestType> response) {
        return response.body();
    }

    private void onNetworkSuccess(Response<RequestType> response, ResultType dbData) {
        if (response.isSuccessful()) {
            RequestType netData = processResponse(response);
            saveCallResult(netData);

            result.onNext(Resource.success(mapResponse(netData)));
        } else {
            result.onNext(Resource.error(response.message(), dbData));
        }
    }

    private void onError(Throwable error, ResultType dbData) {
        result.onNext(Resource.error(error.getMessage(), dbData));
        onFetchFailed();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private Class<ResultType> getResultTypeClass() {
        Type actualTypeArgument = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];

        if (actualTypeArgument instanceof ParameterizedType){
            return (Class<ResultType>) ((ParameterizedType) actualTypeArgument).getRawType();
        } else {
            return (Class<ResultType>) actualTypeArgument;
        }
    }
}