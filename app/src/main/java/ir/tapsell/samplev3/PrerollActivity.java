package ir.tapsell.samplev3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.nexage.sourcekit.vast.VASTPlayer;

import ir.tapsell.samplev3.utils.TapsellConstants;
import ir.tapsell.sdk.vast.TapsellVast;

public class PrerollActivity extends Activity implements VASTPlayer.VASTPlayerListener{

    private VASTPlayer vastPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preroll);

        vastPlayer = new VASTPlayer(this,this);
        String vastUrl = TapsellVast.getVastUrl(this, TapsellConstants.prerollZoneId,
                TapsellVast.PREROLL_TYPE_LONG,TapsellVast.VAST_VERSION_3);
        vastPlayer.loadVideoWithUrl(vastUrl);
    }

    @Override
    public void vastReady() {
        Log.e("VAST","vastReady");
        vastPlayer.play();
    }

    @Override
    public void vastError(int error) {
        Log.e("VAST","vastError: "+error);
    }

    @Override
    public void vastClick() {
        Log.e("VAST","vastClick");
    }

    @Override
    public void vastComplete() {
        Log.e("VAST","vastComplete");
    }

    @Override
    public void vastDismiss() {
        Log.e("VAST","vastDismiss");
    }

}
