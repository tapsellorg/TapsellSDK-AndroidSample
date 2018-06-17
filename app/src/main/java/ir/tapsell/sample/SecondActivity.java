package ir.tapsell.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.nexage.sourcekit.vast.VASTPlayer;

import ir.tapsell.sdk.vast.TapsellVast;

public class SecondActivity extends AppCompatActivity implements VASTPlayer.VASTPlayerListener {

    VASTPlayer vastPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        vastPlayer = new VASTPlayer(this, this);
        String url = TapsellVast.getVastUrl(this, BuildConfig.tapsellVastZoneId, TapsellVast.PREROLL_TYPE_LONG, TapsellVast.VAST_VERSION_3);
        Log.e("vast", "vast url: " + url);
        vastPlayer.loadVideoWithUrl(url);

    }

    @Override
    public void vastReady() {
        Log.e("VAST", "vastReady");
        vastPlayer.play();
    }

    @Override
    public void vastError(int error) {
        Log.e("VAST", "vastError: " + error);
    }

    @Override
    public void vastClick() {
        Log.e("VAST", "vastClick");
    }

    @Override
    public void vastComplete() {
        Log.e("VAST", "vastComplete");
    }

    @Override
    public void vastDismiss() {
        Log.e("VAST", "vastDismiss");
    }

}
