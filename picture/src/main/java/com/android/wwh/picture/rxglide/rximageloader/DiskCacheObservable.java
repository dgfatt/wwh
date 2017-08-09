package com.android.wwh.picture.rxglide.rximageloader;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DiskCacheObservable extends CacheObservable {

    private static DiskLruCache mCache = null;
    private final static int IMAGE_QUANLITY = 100;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private long mCacheSize = 50 * 1024 * 1024; // 50MB
    private static final String DISK_CACHE_SUBDIR = "bitmap";

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()
                && Utils.checkPermissions(context, "android.permission.WRITE_EXTERNAL_STORAGE")
                && context.getExternalCacheDir() !=null
                && context.getExternalCacheDir().canWrite()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    public DiskCacheObservable(Context context) {
        if (context == null)
            return;
        try {
            if (mCache == null) {
                File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                mCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, mCacheSize);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (mCache != null)
            try {
                mCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Create DiskCacheObservable
     * @param context android application context
     * @param key cache key
     * @param cacheSize cache size, <= 0 for default size, 50MB
     */
    public void create(Context context, final String key, long cacheSize) {

        if (cacheSize > 0)
            mCacheSize = cacheSize;

        this.observable = Observable.create(new ObservableOnSubscribe<Data>() {
            @Override
            public void subscribe(ObservableEmitter<Data> e) throws Exception {
                Bitmap ob = cache(key);
                Data data = new Data(ob, key);

                e.onNext(data);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * save pictures downloaded from net to disk
     * @param data data to be saved
     */
    @Override
    public void putData(final Data data) {

        Observable.create(new ObservableOnSubscribe<Data>() {

            @Override
            public void subscribe(ObservableEmitter<Data> e) throws Exception {
                putDiskCache(data.url, data.bitmap);

                e.onNext(data);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public Bitmap cache(String key) {

        if (mCache == null)
            return null;

        DiskLruCache.Snapshot snapShot = null;
        InputStream is = null;
        try {
            snapShot = mCache.get(toMD5(key));
            if (snapShot != null) {
                is = snapShot.getInputStream(0);
                if (is!=null) {
                    BufferedInputStream buffIn = new BufferedInputStream(is, IO_BUFFER_SIZE);
                    return BitmapFactory.decodeStream(buffIn);
                }
            }
        } catch (IOException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private boolean putDiskCache(String key, Bitmap bitmap) {
        if (bitmap == null)
            return false;

        OutputStream out = null;
        String ekey = toMD5(key);
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mCache.get(ekey);
            if (snapshot == null) {
                DiskLruCache.Editor editor = mCache.edit(ekey);
                if (editor == null)
                    return false;
                out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
                Bitmap.CompressFormat format;
                if (key.endsWith("png") || key.endsWith("PNG")) {
                    format = Bitmap.CompressFormat.PNG;
                } else if(Utils.isICSOrHigher() && key.endsWith("webp")){
                    format = Bitmap.CompressFormat.WEBP;
                } else {
                    format = Bitmap.CompressFormat.JPEG;
                }
                bitmap.compress(format, IMAGE_QUANLITY, out);
                editor.commit();
                mCache.flush();
            } else {
                snapshot.getInputStream(0).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
        return true;
    }

    private String toMD5(String content) {
        MessageDigest md = null;
        String md5 = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(content.getBytes("UTF-8"));
            byte[] digests = md.digest();

            int i;
            StringBuilder sb = new StringBuilder("");
            for (byte b : digests) {
                i = b;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(i));
            }
            md5 = sb.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 清空mCache
     */
    public void clear() {
        try {
            mCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
