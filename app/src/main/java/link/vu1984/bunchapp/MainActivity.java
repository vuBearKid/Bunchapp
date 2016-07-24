package link.vu1984.bunchapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends VUActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*((Button) findViewById(R.id.butt1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    public void buttonOnclick(View view){
        //Toast.makeText(this,view.getId()+"",Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (view.getId()){
            case R.id.butt1:
                intent = new Intent(this,ThemeActivity.class);
                startActivity(intent);
                break;
            case R.id.butt2:
                intent = new Intent(this,QRcodeActivity.class);
                startActivity(intent);
                break;
            case R.id.butt3:
                break;
            case R.id.butt4:
                break;
            case R.id.butt5:
                break;
            case R.id.butt6:
                break;
            case R.id.butt7:
                break;
            case R.id.butt8:
                break;
            case R.id.butt9:
                break;
            case R.id.butt10:
                break;
            case R.id.butt11:
                break;
            case R.id.butt12:
                break;
            case R.id.butt13:
                break;
            case R.id.butt14:
                break;
            case R.id.butt15:
                break;
            case R.id.butt16:
                break;
            case R.id.butt17:
                break;
            case R.id.butt18:
                break;
            case R.id.butt19:
                break;
            case R.id.butt20:
                break;



        }


    }
}
