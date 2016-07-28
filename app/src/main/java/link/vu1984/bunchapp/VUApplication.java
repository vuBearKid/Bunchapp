package link.vu1984.bunchapp;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * TODO 这个类会在app打开时就初始化，可以在这里定义些全局变量，需要在AndroidManifest.xml注册
 * <application
 * android:name="full.package.name.VUApplication">
 * ......
 * ......
 * </application>
 */
public class VUApplication extends Application {
    private static final String VERSION = "1";
    public static final String TAG = "VUApplication";
    private static Context mContext;
    private static AssetManager mAssetManager;
    private static Charset mCharset = Charset.defaultCharset();

    private static boolean mNetWorkState = false;
    private NetworkChangeReceiver networkChangeReceiver;

    public static final String DIR_SEPARATOR = File.separator;
    public static final String BASE_DIR = "vuapp";//all your app should be placed in this DIR

    public static String appDir = null; //specify app's DIR,should be named with packageName
    public static String extStoragePath = null; //shared data to place in
    public static String PrivateExtStoragePath = null; //private data to place in
    public static String tempDir = "temp";
    public static String tempDirPath = null; //temp shared data to place in
    //public static WindowManager windowManager;
    //public static Display display;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //windowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        //display = windowManager.getDefaultDisplay();


        appDir = mContext.getPackageName();
        extStoragePath = Environment.getExternalStorageDirectory().getPath();
        tempDirPath = extStoragePath + DIR_SEPARATOR + BASE_DIR + DIR_SEPARATOR + appDir + DIR_SEPARATOR + tempDir;
        PrivateExtStoragePath = mContext.getExternalFilesDir(null).getPath();

        //Build.VERSION.SDK_INT  //当前运行环境api版本
        //Build.VERSION_CODES.LOLLIPOP //api19 android 4.4

        //监控网络情况
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(networkChangeReceiver);
    }

    public static Context getContext() {
        if (mContext != null) {
            return mContext;
        } else {
            return null;
        }
    }

    public static AssetManager getAssetManager() {
        if (mAssetManager == null) mAssetManager = mContext.getAssets();
        return mAssetManager;
    }

    public static void setCharset(String charsetName) {
        mCharset = Charset.forName(charsetName);
    }

    /**
     * 用第三方APP打文件
     *
     * @param file
     */
    public static void openFile(File file) {
        //VULog.e(TAG,VUFile.getMimeType(file.getName()));
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), VUFile.getMimeType(file.getName()));
        mContext.startActivity(intent);
    }

    /**
     * 注意这文件的换行符会被替换
     *
     * @param filePath 相对assets的路径名
     * @return
     */
    public static String getAssetsTxt(String filePath) {
        StringBuilder returnStr = new StringBuilder();
        getAssetManager();
        try {
            InputStream is = mAssetManager.open(filePath);
            InputStreamReader isr = new InputStreamReader(is, mCharset);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                returnStr.append(line);
                returnStr.append('\n');
            }
            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            VULog.e(TAG, e.getMessage());
        } finally {
        }
        returnStr.deleteCharAt(returnStr.length() - 1);//删除最后一个换行
        return returnStr.toString();
    }

    public static Point getWindowSize() {
        //DisplayMetrics metrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);//用上面这个好像更好一些
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);//getRealSize会准确些，getSize会减去ACTIONBAR后的尺寸。
        return point;
    }


    public static void setNetWorkState(boolean state) {
        mNetWorkState = state;
    }

    public static boolean getNetWorkState() {
        return mNetWorkState;
    }


    /**
     * 需要权限 TODO <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                setNetWorkState(true);
            } else {
                setNetWorkState(false);
            }
            VULog.e(TAG, "is network avaiable? " + getNetWorkState());
        }
    }


    @TargetApi(19)
    /**
     * TODO: <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * 当通过第三方APP获取图片时，用于onActivityResult返回结果的图片地址获取
     * @param data onActivityResult的Intent data
     * @return 图片地址String，要是找不到或有问题 null
     */
    public static String getImagePathFromResult(Intent data) {
        Uri uri = data.getData();//VULog.e("VUApplication.getImagePathFromResult",uri.toString());
        String imagePath = null;//返回的结果
        String selection = null;
        Uri queryUri = null;
        if (VUtil.getSDKLevel() >= 19) {
            if (DocumentsContract.isDocumentUri(mContext, uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1];
                    selection = MediaStore.Images.Media._ID + "=" + id;
                    queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    queryUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                }
            } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                queryUri = uri;
            } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
                return imagePath;
            } else {
                VULog.e("VUApplication.getImagePathFromResult", "unknowed Uri format,updata the function");
            }
        } else {
            if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
                return imagePath;
            } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                queryUri = uri;
            } else {
                queryUri = uri;
            }
        }
        //图片路径查询
        //VULog.e("VUApplication.getImagePathFromResult", selection + "!");
        //VULog.e("VUApplication.getImagePathFromResult", queryUri.toString() + "!");
        Cursor cursor = mContext.getContentResolver().query(queryUri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return imagePath;

    }
}
