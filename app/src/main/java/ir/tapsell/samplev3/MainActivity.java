package ir.tapsell.samplev3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ir.tapsell.samplev3.utils.TapsellConstants;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellConfiguration;
import ir.tapsell.sdk.TapsellRewardListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class MainActivity extends Activity {

    private boolean showCompleteDialog = false;
    private boolean rewarded = false;
    private boolean completed = false;

    Button requestAdBtn, showAddBtn;
    TapsellAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TapsellConfiguration config = new TapsellConfiguration();
        config.setDebugMode(true);
        config.setPermissionHandlerMode(TapsellConfiguration.PERMISSION_HANDLER_AUTO);

        Tapsell.initialize(this, config, TapsellConstants.appKey);

        Tapsell.setRewardListener(new TapsellRewardListener() {
            @Override
            public void onAdShowFinished(TapsellAd ad, boolean completed) {
                Log.e("MainActivity","isCompleted? "+completed+ ", ad was rewarded?" + (ad!=null && ad.isRewardedAd()) );
                showCompleteDialog = true;
                MainActivity.this.completed=completed;
                MainActivity.this.rewarded=(ad!=null && ad.isRewardedAd());
                // store user reward in local database
            }
        });

        requestAdBtn = (Button) findViewById(R.id.btnRequestAd);

        requestAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(TapsellConstants.rewardedZoneId);
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
                    showOptions.setShowDialog(true);
                    ad.show(MainActivity.this, showOptions, new TapsellAdShowListener() {
                        @Override
                        public void onOpened(TapsellAd tapsellAd) {
                            Log.e("tapsell","ad opened");
                        }

                        @Override
                        public void onClosed(TapsellAd tapsellAd) {
                            Log.e("tapsell","ad closed");
                        }
                    });
                }
                else if( ad==null ){
                    Log.e("tapsell","null ad");
                }
                else {
                    Log.e("tapsell","ad file removed? "+ad.isFileRemoved());
                    Log.e("tapsell","invalid ad, id="+ad.getId());
                }
            }
        });


        (findViewById(R.id.btnPrerollNexage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PrerollActivity.class));
            }
        });

        (findViewById(R.id.btnPrerollExoPlayer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
                {
                    startActivity(new Intent(MainActivity.this, ExoplayerVastSample.class));
                }
                else
                {
                    Toast.makeText(MainActivity.this, "ExoPlayer is only available in android JellyBean and above.", Toast.LENGTH_LONG).show();
                }
            }
        });

        (findViewById(R.id.btnNativeBanner)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeBannerActivity.class));
            }
        });

        (findViewById(R.id.btnNativeBannerInList)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeBannerListActivity.class));
            }
        });

        (findViewById(R.id.btnNativeVideo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeVideoActivity.class));
            }
        });

    }

    private void loadAd(String zoneId) {

        TapsellAdRequestOptions options = new TapsellAdRequestOptions(TapsellAdRequestOptions.CACHE_TYPE_CACHED);

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
                loadAd(TapsellConstants.rewardedZoneId);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(showCompleteDialog)
        {
            showCompleteDialog=false;
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Tapsell Ad")
                    .setMessage("Showing ad finished, completed? "+completed+", rewarded? "+rewarded)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

}
