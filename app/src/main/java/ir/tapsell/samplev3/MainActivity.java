package ir.tapsell.samplev3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellConfiguration;
import ir.tapsell.sdk.TapsellShowOptions;

public class MainActivity extends Activity {

    private static final String appKey = "kilkhmaqckffopkpfnacjkobgrgnidkphkcbtmbcdhiokqetigljpnnrbfbnpnhmeikjbq";

    Button requestAdBtn, showAddBtn;
    TapsellAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TapsellConfiguration config = new TapsellConfiguration();
        config.setDebugMode(true);

        Tapsell.initialize(this, config, appKey);

        requestAdBtn = (Button) findViewById(R.id.btnRequestAd);

        requestAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(null);
            }
        });

        showAddBtn = (Button) findViewById(R.id.btnShowAd);

        showAddBtn.setEnabled(false);

        showAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ad!=null && ad.isValid())
                {
                    showAddBtn.setEnabled(false);
                    TapsellShowOptions showOptions = new TapsellShowOptions();
                    showOptions.setBackDisabled(false);
                    showOptions.setImmersiveMode(true);
                    showOptions.setRotationMode(TapsellShowOptions.ROTATION_UNLOCKED);
                    ad.show(MainActivity.this, showOptions, new TapsellAdShowListener() {
                        @Override
                        public void onFinished(TapsellAd ad, boolean completed, boolean rewarded) {
                            Log.d("Test","Video Finished!, Completed? : "+completed+", rewarded? "+rewarded);
                            if(completed && rewarded)
                            {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Reward Obtained!")
                                        .setMessage("Congratulations! You gained some coins for viewing the video ad.")
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                }
            }
        });
    }


    private void loadAd(String zoneId) {

        Tapsell.requestAd(MainActivity.this, zoneId, new TapsellAdRequestListener() {
            @Override
            public void onError(String error) {
                Log.d("Tapsell Sample","Error: "+error);
            }

            @Override
            public void onAdAvailable(TapsellAd ad) {

                MainActivity.this.ad = ad;
                showAddBtn.setEnabled(true);
                Log.d("Tapsell Sample","Ad is available");
            }

            @Override
            public void onNoAdAvailable() {
                Log.d("Tapsell Sample","No ad available");
            }

            @Override
            public void onNoNetwork() {
                Log.d("Tapsell Sample","No network");
            }

            @Override
            public void onExpiring(TapsellAd ad) {
                showAddBtn.setEnabled(false);
                loadAd(null);
            }
        });

    }

}
