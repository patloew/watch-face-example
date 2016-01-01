package com.patloew.watchfaceexample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.patloew.commons.IWatchFaceConfig;
import com.patloew.commons.WatchFaceDrawer;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements IWatchFaceConfig {

    LinearLayout mWatchfaceLayout;
    ImageView mWatchfaceImage;
    ImageView mWatchfaceImageSquare;
    WatchFaceDrawer mWatchfaceDrawer;
    Calendar mCalendar;
    Runnable mUpdateWatchfaceImageRunnable = new Runnable() {
        @Override
        public void run() {
            updateWatchfaceImage();
        }
    };
    boolean mIsAmbient = false;
    boolean mIsRound = false;
    int mSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWatchfaceLayout = (LinearLayout) findViewById(R.id.ll_watchface);
        mWatchfaceImage = (ImageView) findViewById(R.id.iv_watchface);
        mWatchfaceImageSquare = (ImageView) findViewById(R.id.iv_watchface_square);
        mWatchfaceDrawer = new WatchFaceDrawer(this);
        mCalendar = new GregorianCalendar();

        mWatchfaceDrawer.setMobilePreview(this, true);

        mSize = Math.round(320/1.5f*getResources().getDisplayMetrics().density);

        if(Build.VERSION.SDK_INT >= 21) {
            mWatchfaceLayout.setElevation(getResources().getDimension(R.dimen.watchface_preview_elevation));
            mWatchfaceLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWatchfaceImage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWatchfaceImage.removeCallbacks(mUpdateWatchfaceImageRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.watchfaceconfig, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_action_toggle_ambient: {
                mIsAmbient = !mIsAmbient;
                item.setIcon(mIsAmbient ? R.drawable.ic_ambient_on : R.drawable.ic_ambient_off);
                mWatchfaceDrawer.onAmbientModeChanged(getApplicationContext(), this);
                mWatchfaceImage.removeCallbacks(mUpdateWatchfaceImageRunnable);
                updateWatchfaceImage();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateWatchfaceImage() {
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        Bitmap bmp = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        mIsRound = true;
        mWatchfaceDrawer.onDraw(getApplicationContext(), this, canvas, canvas.getClipBounds());
        mWatchfaceImage.setImageBitmap(bmp);

        bmp = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bmp);
        mIsRound = false;
        mWatchfaceDrawer.onDraw(getApplicationContext(), this, canvas, canvas.getClipBounds());
        mWatchfaceImageSquare.setImageBitmap(bmp);

        mWatchfaceImage.postDelayed(mUpdateWatchfaceImageRunnable, 1000);
    }


    // IWatchFaceConfig

    @Override
    public Calendar getCalendar() {
        return mCalendar;
    }

    @Override
    public boolean isAmbient() {
        return mIsAmbient;
    }

    @Override
    public boolean isLowBitAmbient() {
        return false;
    }

    @Override
    public boolean isRound() {
        return mIsRound;
    }

    @Override
    public boolean isLightTheme() {
        return true;
    }
}
