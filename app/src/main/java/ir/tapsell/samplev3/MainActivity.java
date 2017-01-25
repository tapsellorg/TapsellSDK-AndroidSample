package ir.tapsell.samplev3;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellConfiguration;
import ir.tapsell.sdk.TapsellRewardListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class MainActivity extends Activity {

    private static final String appKey = "kilkhmaqckffopkpfnacjkobgrgnidkphkcbtmbcdhiokqetigljpnnrbfbnpnhmeikjbq";

    private static final String myAppMainZoneId = "586f52d9bc5c284db9445beb";

    // Request code for checking whether the user has granted required permissions
    private static final int permissionsRequestCode = 123;

    Button requestAdBtn, showAddBtn;
    TapsellAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int resultReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (resultReadPhoneState == PackageManager.PERMISSION_GRANTED)
            {
                onPermissionsGranted();
            }
            else {
                if( ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE) )
                {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.READ_PHONE_STATE}, permissionsRequestCode);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    onPermissionsDenied();
                                    break;
                            }
                        }
                    };
                    new AlertDialog.Builder(this)
                            .setMessage("Tapsell requires permission to read your device Id showing video ads.")
                            .setPositiveButton("OK", listener)
                            .setNegativeButton("Cancel", listener)
                            .create()
                            .show();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, permissionsRequestCode);
                }
            }
        } else {
            onPermissionsGranted();
        }

        requestAdBtn = (Button) findViewById(R.id.btnRequestAd);

        requestAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(myAppMainZoneId);
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
                    ad.show(MainActivity.this, showOptions);
                }
            }
        });
    }

    private void loadAd(String zoneId) {

        TapsellAdRequestOptions options = new TapsellAdRequestOptions(TapsellAdRequestOptions.CACHE_TYPE_STREAMED);

        Tapsell.requestAd(MainActivity.this, zoneId, options, new TapsellAdRequestListener() {
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
                loadAd(myAppMainZoneId);
            }
        });

    }

    private void onPermissionsGranted()
    {
        TapsellConfiguration config = new TapsellConfiguration();
        config.setDebugMode(true);

        Tapsell.initialize(this, config, appKey);

        Tapsell.setRewardListener(new TapsellRewardListener() {
            @Override
            public void onAdShowFinished(TapsellAd ad, boolean completed) {
                Log.e("MainActivity","isCompleted? "+completed);
                // store user
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Info")
                        .setMessage("Video view finished")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void onPermissionsDenied()
    {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        AlertDialog finishDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Tapsell requires permission to read your device Id and location for showing video ads. You can grant this permissions in your phone settings.")
                .setPositiveButton("OK", listener)
                .create();
        finishDialog.setCancelable(false);
        finishDialog.setCanceledOnTouchOutside(false);
        finishDialog.show();

    }

}
