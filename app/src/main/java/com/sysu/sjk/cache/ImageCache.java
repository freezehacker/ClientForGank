package com.sysu.sjk.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.sysu.sjk.utils.AppUtils;
import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.utils.StringUtils;
import com.sysu.sjk.utils.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by sjk on 16-11-2.
 */
public class ImageCache {

    public static final long ALLOCATE_MEMORY_SIZE = Runtime.getRuntime().maxMemory() / 5;
    public static final long ALLOCATE_DISK_SIZE = 1024 * 1024 * 50; // 50M

    private static volatile ImageCache mInstance;   // single instance

    Context mContext;
    LruCache<String, Bitmap> mMemoryCache;
    DiskLruCache mDiskCache;

    /* Get the single instance */
    public static ImageCache getInstance(Context appContext) {
        if (mInstance == null) {
            synchronized (ImageCache.class) {
                if (mInstance == null) {
                    mInstance = new ImageCache(appContext);
                }
            }
        }
        return mInstance;
    }

    public ImageCache(Context context) {
        mContext = context;

        mMemoryCache = new LruCache<String, Bitmap>((int) ALLOCATE_MEMORY_SIZE) {
            @Override
            protected int sizeOf(String name, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

        try {
            mDiskCache = DiskLruCache.open(context.getCacheDir(), AppUtils.getAppVersionCode(), 1, ALLOCATE_DISK_SIZE);
        } catch (IOException e) {
            Logger.log("Fail when initializing an image cache.");
        }
    }


    /* Set a bitmap into an image view. */
    public void setImage(final ImageView imageView, final String bitmapUrl) {
        // The tag set into an image view, is to prevent the wrong picture(Async causes).
        imageView.setTag(bitmapUrl);
        getImage(bitmapUrl, new ImageCallback() {
            @Override
            public void onImageGet(Bitmap bitmap) {
                if (bitmap != null) {
                    String tag = (String) imageView.getTag();
                    if (tag != null && tag.equals(bitmapUrl)) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        });
    }

    /* 'Sum' method */
    public void getImage(final String url, final ImageCallback clientCallback) {
        final String hashKey = StringUtils.getHashByMD5(url);

        // go to memory, level 1
        retrieveFromMemory(url, new ImageCallback() {
            @Override
            public void onImageGet(Bitmap bitmap) {
                if (bitmap != null) {
                    Logger.log("Memory: " + url);
                    clientCallback.onImageGet(bitmap);
                } else {

                    // go to disk, level 2
                    retrieveFromDisk(url, new ImageCallback() {
                        @Override
                        public void onImageGet(Bitmap bitmap) {
                            if (bitmap != null) {
                                Logger.log("Disk: " + url);
                                clientCallback.onImageGet(bitmap);
                            } else {

                                // request the Internet, level 3
                                retrieveFromInternet(url, new ImageCallback() {
                                    @Override
                                    public void onImageGet(Bitmap bitmap) {
                                        clientCallback.onImageGet(bitmap);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void saveToMemory(String url, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        String hashKey = StringUtils.getHashByMD5(url);
        if (mMemoryCache.get(hashKey) == null)
            mMemoryCache.put(hashKey, bitmap);
    }

    private void retrieveFromMemory(String url, ImageCallback cb) {
        String hashKey = StringUtils.getHashByMD5(url);
        cb.onImageGet(mMemoryCache.get(hashKey));
    }

    private void saveToDisk(final String url, final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        String hashKey = StringUtils.getHashByMD5(url);
        try {
            DiskLruCache.Editor editor = mDiskCache.edit(hashKey);
            OutputStream os = editor.newOutputStream(0);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                editor.commit();
                Logger.log("保存到硬盘：" + url);
            } else {
                editor.abort();
            }
            mDiskCache.flush();
        } catch (IOException e) {
            Logger.log("Fail when save an image to disk.");
        }
    }

    private void retrieveFromDisk(final String url, final ImageCallback cb) {
        final String hashKey = StringUtils.getHashByMD5(url);
        Observable.just(hashKey)
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String hashKey) {
                        Bitmap bitmap = null;
                        try {
                            DiskLruCache.Snapshot snapshot = mDiskCache.get(hashKey);
                            if (snapshot != null) {
                                bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                            }
                        } catch (IOException e) {
                            Logger.log("Fail when retrieve an image from disk.(DiskLruCache error)");
                        }

                        // cache in.
                        saveToMemory(hashKey, bitmap);

                        return bitmap;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.log("Fail when retrieve an image from disk.(RxJava error)");
                        Logger.log(e.getMessage());
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        cb.onImageGet(bitmap);
                    }
                });
    }

    private void retrieveFromInternet(final String url, final ImageCallback cb) {
        Observable.just(url)
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String url) {
                        Bitmap ret = null;
                        try {
                            ret = BitmapFactory.decodeStream(new URL(url).openStream());
                        } catch (Exception e) {
                            Logger.log("Exception when downloading an image from Internet.");
                        }

                        Logger.log("下载了图片：" + url);

                        // cache in.
                        saveToMemory(url, ret);
                        saveToDisk(url, ret);

                        return ret;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.log("Fail when retrieve an image from Internet.(RxJava error)");
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        cb.onImageGet(bitmap);
                    }
                });
    }

    public void deleteCache() {
        if (mDiskCache != null) {
            try {
                mDiskCache.delete();
            } catch (IOException ioe) {
                Logger.log("Error when deleting disk cache.");
            }
        }
    }


    /* Universal callback of bitmap operations. */
    public interface ImageCallback {
        void onImageGet(Bitmap bitmap);
    }
}
