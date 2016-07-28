package link.vu1984.bunchapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class QRcodeActivity extends AppCompatActivity {

    private TextView inputText;
    private ImageView qrImage;

    public static final int QRCODE_REQUEST_CODE = 9874;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        inputText = (TextView) findViewById(R.id.qrcode_text);
        qrImage = (ImageView) findViewById(R.id.qr_image);
    }

    public void buttonOnclick(View view) {
        //Toast.makeText(this,view.getId()+"",Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (view.getId()) {
            case R.id.c_qrcode:
                String str = inputText.getText().toString();
                if (VUtil.isStringEmpty(str, false)) return;

                QRCodeWriter qrCodeWriter = new QRCodeWriter();

                int qrWidth = 500, qrHeight = 500;
                try {
                    Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

                    BitMatrix bitMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);


                    int[] pixels = new int[qrWidth * qrHeight];
                    for (int y = 0; y < qrHeight; y++) {
                        for (int x = 0; x < qrWidth; x++) {
                            if (bitMatrix.get(x, y)) {
                                pixels[y * qrWidth + x] = 0xff000000;
                            } else {
                                pixels[y * qrWidth + x] = 0xffffffff;
                            }

                        }
                    }
                    Bitmap bitmap = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888);
                    bitmap.setPixels(pixels, 0, qrWidth, 0, 0, qrWidth, qrHeight);
                    qrImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();

                }
                break;
            case R.id.c_qrcode_select:
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                this.startActivityForResult(wrapperIntent, QRCODE_REQUEST_CODE);
                break;
            case R.id.c_qrcode_scan:
                intent = new Intent(this,QRcodeScanerActivity.class);
                startActivity(intent);
                break;


        }
    }

    /**
     * 扫描二维码图片的方法
     */
    /*public Result scanningImage(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static Bitmap createQRCode(String url) throws WriterException {

        if (url == null || url.equals("")) {
            return null;
        }

        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, 300, 300);

        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }else{
                    pixels[y * width + x] = 0xffffffff;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case QRCODE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String imagePath = VUApplication.getImagePathFromResult(data);
                    Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
