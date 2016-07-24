package link.vu1984.bunchapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class QRcodeActivity extends AppCompatActivity {

    private TextView inputText;
    private ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        inputText = (TextView) findViewById(R.id.qrcode_text);
        qrImage = (ImageView) findViewById(R.id.qr_image);
    }

    public void buttonOnclick(View view) {
        //Toast.makeText(this,view.getId()+"",Toast.LENGTH_SHORT).show();
        //Intent intent;
        switch (view.getId()) {
            case R.id.c_qrcode:
                String str = inputText.getText().toString();
                if (VUtil.isStringEmpty(str,false)) return;

                QRCodeWriter qrCodeWriter = new QRCodeWriter();

                int qrWidth=500,qrHeight=500;
                try {
                    Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

                    BitMatrix bitMatrix = qrCodeWriter.encode(str,BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);


                    int[] pixels = new int[qrWidth * qrHeight];
                    for (int y = 0; y < qrHeight; y++  ) {
                        for (int x = 0; x < qrWidth; x++  ) {
                            if (bitMatrix.get(x, y)) {
                                pixels[y * qrWidth +  x] = 0xff000000;
                            } else {
                                pixels[y * qrWidth  + x] = 0xffffffff;
                            }

                        }
                    }
                    Bitmap bitmap = Bitmap.createBitmap(qrWidth, qrHeight,Bitmap.Config.ARGB_8888);
                    bitmap.setPixels(pixels, 0, qrWidth, 0, 0, qrWidth, qrHeight);
                    qrImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;


        }
    }
}
