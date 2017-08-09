package com.android.wwh.picture.rxglide.rximageloader;

import android.graphics.Bitmap;

import io.reactivex.Observable;

/**
 * Created by Tony Shen on 15/11/13.
 */
public abstract class CacheObservable {

    public Observable<Data> observable;

    public abstract void putData(Data data);

    public abstract Bitmap cache(String info);
}
