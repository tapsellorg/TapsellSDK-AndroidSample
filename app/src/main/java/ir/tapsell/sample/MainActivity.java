package ir.tapsell.sample;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.List;
import java.util.Locale;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellRewardListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class MainActivity extends AppCompatActivity {

    // Request code for checking whether the user has granted required permissions
//    private static final int permissionsRequestCode = 123;
    Button requestCatchedVideoAdBtn, requestStreamVideoAdBtn, requestBannerAdButton, showAddBtn, secondActivityBtn;
    TapsellAd ad;
    ProgressDialog progressDialog;

    private static final int TAPSELL_REQUEST_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        String android_id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("Tapsell", "android id:" + android_id);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info idInfo = AdvertisingIdClient.getAdvertisingIdInfo(MainActivity.this);
                    Log.e("Tapsell", "android advertising id:" + idInfo.getId());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();

        onPermissionsGranted();
        getCity();

        requestCatchedVideoAdBtn = (Button) findViewById(R.id.btnRequestVideoAd);
        requestStreamVideoAdBtn = (Button) findViewById(R.id.btnRequestStreamVideoAd);
        requestBannerAdButton = (Button) findViewById(R.id.btnRequestBannerAd);

        requestCatchedVideoAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(G.videoZoneId, TapsellAdRequestOptions.CACHE_TYPE_CACHED);
            }
        });

        requestStreamVideoAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(G.videoZoneId, TapsellAdRequestOptions.CACHE_TYPE_STREAMED);
            }
        });

        requestBannerAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(G.interstitialBannerZoneId, TapsellAdRequestOptions.CACHE_TYPE_STREAMED);
            }
        });

        secondActivityBtn = (Button) findViewById(R.id.btnSecondActivity);

        secondActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        showAddBtn = (Button) findViewById(R.id.btnShowAd);

        showAddBtn.setEnabled(false);

        showAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ad != null) {
                    showAddBtn.setEnabled(false);
                    TapsellShowOptions showOptions = new TapsellShowOptions();
                    showOptions.setBackDisabled(false);
                    showOptions.setImmersiveMode(true);
                    showOptions.setRotationMode(TapsellShowOptions.ROTATION_UNLOCKED);
                    showOptions.setShowDialog(true);

                    showOptions.setWarnBackPressedDialogMessage("سلام دوست من بک نزن");
                    showOptions.setWarnBackPressedDialogMessageTextColor(Color.RED);
                    showOptions.setWarnBackPressedDialogAssetTypefaceFileName("IranNastaliq.ttf");
                    showOptions.setWarnBackPressedDialogPositiveButtonText("ادامه بده");
                    showOptions.setWarnBackPressedDialogNegativeButtonText("ولم کن بزن بیرون");
                    showOptions.setWarnBackPressedDialogPositiveButtonBackgroundResId(R.drawable.button_background);
                    showOptions.setWarnBackPressedDialogNegativeButtonBackgroundResId(R.drawable.button_background);
                    showOptions.setWarnBackPressedDialogPositiveButtonTextColor(Color.RED);
                    showOptions.setWarnBackPressedDialogNegativeButtonTextColor(Color.GREEN);
                    showOptions.setWarnBackPressedDialogBackgroundResId(R.drawable.dialog_background);
//                    ad.show(MainActivity.this, showOptions);
                    ad.show(MainActivity.this, showOptions, new TapsellAdShowListener() {
                        @Override
                        public void onOpened(TapsellAd ad) {
                            Log.e("MainActivity", "on ad opened");
                        }

                        @Override
                        public void onClosed(TapsellAd ad) {
                            Log.e("MainActivity", "on ad closed");
                        }
                    });
                    MainActivity.this.ad = null;
                }
            }
        });

        Button nativeBanner = (Button) findViewById(R.id.btnNativeBanner);
        nativeBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NativeBannerActivity.class);
                startActivity(intent);
            }
        });

        Button nativeBannerList = (Button) findViewById(R.id.btnNativeBannerInList);
        nativeBannerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NativeBannerInList.class);
                startActivity(intent);
            }
        });

        Button nativeVideo = (Button) findViewById(R.id.btnNativeVideo);
        nativeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeVideoActivity.class);
                startActivity(intent);
            }
        });

        Button nativeVideoList = (Button) findViewById(R.id.btnNativeVideoInList);
        nativeVideoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NativeVideoInList.class);
                startActivity(intent);
            }
        });

        Button nativeWebBanner = (Button) findViewById(R.id.btnWebBanner);
        nativeWebBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebBannerActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadAd(final String zoneId, final int catchType) {

        if (ad == null) {
            TapsellAdRequestOptions options = new TapsellAdRequestOptions(catchType);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading ...");
            progressDialog.show();
            Tapsell.requestAd(MainActivity.this, zoneId, options, new TapsellAdRequestListener() {
                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, "ERROR:\n" + error, Toast.LENGTH_SHORT).show();
                    Log.e("Tapsell", error);
                    progressDialog.dismiss();
                }

                @Override
                public void onAdAvailable(TapsellAd ad) {

                    MainActivity.this.ad = ad;
                    showAddBtn.setEnabled(true);
                    Log.e("Tapsell", "adId: " + (ad == null ? "NULL" : ad.getId()) + " available, zoneId: " + (ad == null ? "NULL" : ad.getZoneId()));
                    progressDialog.dismiss();
//                new AlertDialog.Builder(MainActivity.this).setTitle("Title").setMessage("Message").show();
                }

                @Override
                public void onNoAdAvailable() {
                    Toast.makeText(MainActivity.this, "No Ad Available", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Log.e("Tapsell", "No Ad Available");
                }

                @Override
                public void onNoNetwork() {
                    Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Log.e("Tapsell", "No Network Available");
                }

                @Override
                public void onExpiring(TapsellAd ad) {
                    showAddBtn.setEnabled(false);
                    MainActivity.this.ad = null;
                    loadAd(zoneId, catchType);
                }
            });
        }
    }

    private void onPermissionsGranted() {
        Tapsell.setRewardListener(new TapsellRewardListener() {
            @Override
            public void onAdShowFinished(final TapsellAd ad, final boolean completed) {
                Log.e("MainActivity", "isCompleted? " + completed + ", adId: " + (ad == null ? "NULL" : ad.getId()) + ", zoneId: " + (ad == null ? "NULL" : ad.getZoneId()));
                // store user
                MainActivity.this.ad = null;
                showCompleteDialog = true;
                MainActivity.this.completed = completed;
                if (ad != null) {
                    MainActivity.this.rewarded = ad.isRewardedAd();
                }
                if (!completed)
                    Toast.makeText(MainActivity.this, "Ad Closed or No Ad Available", Toast.LENGTH_SHORT).show();
            }
        });

        Log.e("Tapsell", "sdk version: " + Tapsell.getVersion());

    }

    private boolean showCompleteDialog = false;
    private boolean rewarded = false;
    private boolean completed = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (showCompleteDialog) {
            showCompleteDialog = false;
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("View was...")
                    .setMessage("DONE!, completed? " + completed + ", rewarded? " + rewarded)
                    .setNeutralButton("Nothing", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void onPermissionsDenied() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
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

    private void getCity() {
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                //noinspection StatementWithEmptyBody
                if (addresses.size() > 0) {
                    Log.e("MainMenu", addresses.get(0).getLocality());
                } else {
                    // do your staff
                }
            }
        } catch (Throwable te) {
            te.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (TAPSELL_REQUEST_CODE): {
                if (resultCode == Activity.RESULT_OK) {
                    String adId = data.getStringExtra("adId");
                    String zoneId = data.getStringExtra("zoneId");
                    boolean completed = data.getBooleanExtra("completed", false);
                    boolean rewarded = data.getBooleanExtra("rewarded", false);
                    Log.e("MainActivity", "Activity Result isCompleted? " + completed + ", adId: " + adId + ", zoneId: " + zoneId);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("View for ad Id: " + adId + " in zone: " + zoneId + " was...")
                            .setMessage("DONE!, completed? " + completed + ", rewarded? " + rewarded)
                            .setNeutralButton("Nothing", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                break;
            }
        }
    }
}
