package com.example.qrscancode;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //view Object
    private Button buttonScanning;
    private TextView textviewName, textviewClass, textviewId;

    //qrcode scanner object
    private IntentIntegrator qrscan;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view Object
        buttonScanning = (Button) findViewById(R.id.buttonScan);
        textviewName = (TextView) findViewById(R.id.textViewNama);
        textviewClass = (TextView) findViewById(R.id.textViewKelas);
        textviewId = (TextView) findViewById(R.id.textViewNIM);

        //inisialisasi scan object
        qrscan = new IntentIntegrator(this);

        //implementasi onclik Listener
        buttonScanning.setOnClickListener(this);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            //jika hasil scanner tidak sama sekali
            if (result.getContents() == null){
                Toast.makeText(this, "Hasil Scan tidak ada", Toast.LENGTH_LONG).show();
            }
            //webview
            else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }
            // geolokasi
            else if (result.getContents().startsWith("geo:")) {
                String geoUri = result.getContents();
                Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(geoIntent);
            }
            else {
                // JSON
                try {

                    JSONObject jsonObject = new JSONObject(result.getContents());
                    textviewName.setText(jsonObject.getString("nama"));
                    textviewClass.setText(jsonObject.getString("kelas"));
                    textviewId.setText(jsonObject.getString("nim"));

                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
                // DIAL UP, NOMOR TELEPON
                try {
                    Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse(result.getContents()));
                    startActivity(intent2);
                } catch (Exception e){
                    Toast.makeText(this, "Not Scanned", Toast.LENGTH_LONG).show();
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View v) {
        qrscan.initiateScan();
    }
}
