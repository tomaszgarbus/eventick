package hackaton.waw.eventnotifier;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import hackaton.waw.eventnotifier.event.Event;
import hackaton.waw.eventnotifier.event.EventManager;

/**
 * Created by tomek on 10/31/16.
 */

public class BitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {


    private static class DiskBitmapCache {
        private static String MD5(String md5) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] array = md.digest(md5.getBytes());
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < array.length; ++i) {
                    sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
                }
                return sb.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
            }
            return null;
        }

        private static String SUB_DIR = "/eventick";

        public static boolean fileExists(String url) {
            return new File(Environment.getExternalStorageDirectory().toString() + SUB_DIR + "/" + MD5(url) + ".jpg").exists();
        }

        public static void saveToDisk(String url, Bitmap bitmap) {
            String path = Environment.getExternalStorageDirectory().toString() + SUB_DIR + "/";
            String filename = MD5(url) + ".jpg";
            File file = new File(path, filename);
            if (file.exists()) {
                file.delete();
            } else {
                new File(path).mkdirs();
            }
            try {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static Bitmap readFromDisk(String url) {
            String path = Environment.getExternalStorageDirectory().toString() + SUB_DIR + "/";
            String filename = MD5(url) + ".jpg";
            File file = new File(path, filename);
            if (!file.exists()) {
                return null;
            }
            try {
                FileInputStream in = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    @Override
    public void resize(int maxSize) {
        super.resize(maxSize);
    }

    @Override
    public void trimToSize(int maxSize) {
        super.trimToSize(maxSize);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    @Override
    protected Bitmap create(String key) {
        if (key.indexOf("http") < 0) {
            return null;
        }
        String url = key.substring(key.indexOf("http"));

        if (DiskBitmapCache.fileExists(url)) {
            Bitmap bitmap = DiskBitmapCache.readFromDisk(url);
            if (bitmap != null) {
                return bitmap;
            }
        }
        Bitmap bitmap = EventManager.FacebookEventFetcher.bitmapFromCoverSource(url);
        DiskBitmapCache.saveToDisk(url, bitmap);
        //return super.create(key);
        put(key, bitmap);
        return bitmap;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return super.sizeOf(key, value);
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapCache(int maxSize) {
        super(maxSize);
    }

    public BitmapCache() {
        super(getDefaultLruCacheSize());
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (url == null || bitmap == null) {
            return;
        }
        if (bitmap != null && !DiskBitmapCache.fileExists(url)) {
            DiskBitmapCache.saveToDisk(url, bitmap);
        }
        put(url, bitmap);
    }
}
