package com.jurajkusnier.bitcoinwalletbalance.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;

public class Transformations {

    public static <T>ObservableTransformer<T,T> doAfterFirst(Consumer<? super T> consumer) {
        return f -> Observable.defer(() -> {
            final AtomicBoolean first = new AtomicBoolean(true);
            return f.doAfterNext(t -> {
                if (first.compareAndSet(true, false)) {
                    consumer.accept(t);
                }
            });
        });
    }
}
