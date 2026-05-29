package com.isslam.husonkids;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    public final static int PAGES = 10;
    public final static int LOOPS = 10;
    public final static int FIRST_PAGE = 1;
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.8f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    ImageView img_header_bg;
    public MyPagerAdapter adapter;
    public ViewPager pager;
    RelativeLayout ly_main_bg;
    View main_layout;
    View header;
    Drawable backgrounds[] = new Drawable[2];
    Context context;

    private void playSounds(int position) {
        MediaManager mediaManager = MediaManager.getInstance(this);
        mediaManager.setContext(this);
        mediaManager.playSounds(position, 0);
    }

    public float convertFromDp(float input) {
        final float scale = getResources().getDisplayMetrics().density;
        return ((input - 0.5f) / scale);
    }

    Handler handler;
    Runnable runnable;
    Animation anim;

    public void onPageChanged() {
        Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_from_top);
        header.startAnimation(anim2);
    }

    public void OnpageRemains() {
        Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.grow_from_top);
        header.startAnimation(anim2);
    }

    public void OnpageSelected(final int position, final int lastPage) {
        Log.e("athkar", "athkar");
        String athkar_titles = getResources().getStringArray(R.array.athkar_titles)[position];
        txtTitle.setText(getResources().getString(R.string.athkar_pre) + " " + athkar_titles);
        Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.grow_from_top);
        header.startAnimation(anim2);
        if (handler == null) handler = new Handler();
        if (runnable != null) handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                double num = ((Math.random() * 1000) % 360) - 180;
                img_header_bg.setColorFilter(ColorFilterGenerator.adjustHue((float) num));
                OnpageSelectedNow(position, lastPage);
                playSounds(position);
            }
        };
        handler.postDelayed(runnable, 600);
    }

    @SuppressLint("NewApi")
    public void OnpageSelectedNow(int position, int lastPage) {
        String image = "img_bg_" + position;
        Context context = ly_main_bg.getContext();
        int id = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
        backgrounds[0] = ly_main_bg.getBackground();
        backgrounds[1] = ContextCompat.getDrawable(this, id);
        main_layout.setBackground(backgrounds[0]);
        ly_main_bg.setBackground(backgrounds[1]);
        anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
        ly_main_bg.startAnimation(anim);
    }

    TextView txtTitle;

    protected void exitByBackKey() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.app_exit))
                .setPositiveButton(getResources().getString(R.string.app_exit_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.app_exit_cancle),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        }).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        ly_main_bg = (RelativeLayout) findViewById(R.id.ly_main_bg);
        header = findViewById(R.id.header);
        img_header_bg = (ImageView) findViewById(R.id.img_header_bg);
        main_layout = findViewById(R.id.main_layout);

        pager = (ViewPager) findViewById(R.id.myviewpager);
        adapter = new MyPagerAdapter(this, this.getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(adapter);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setTextSize(convertFromDp(txtTitle.getTextSize()));
        String fontPath = "fonts/neckar.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        txtTitle.setTypeface(tf);

        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(3);
        pager.setPageMargin(Integer.parseInt(getString(R.string.pagermargin)));

        final FloatingActionButton btn_mute = (FloatingActionButton) findViewById(R.id.btn_mute);
        btn_mute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
                sharedPreferencesManager.savePreferences(SharedPreferencesManager._sound, 0);
            }
        });

        final FloatingActionButton btn_boy = (FloatingActionButton) findViewById(R.id.btn_boy);
        btn_boy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
                sharedPreferencesManager.savePreferences(SharedPreferencesManager._sound, 1);
                playSounds(MyPagerAdapter.lastPage);
            }
        });

        final FloatingActionButton btn_girl = (FloatingActionButton) findViewById(R.id.btn_girl);
        btn_girl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
                sharedPreferencesManager.savePreferences(SharedPreferencesManager._sound, 2);
                playSounds(MyPagerAdapter.lastPage);
            }
        });

        final FloatingActionButton btn_share = (FloatingActionButton) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.share_body) + "\n"
                        + "http://play.google.com/store/apps/details?id=" + context.getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_title));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_title)));
            }
        });

        final FloatingActionButton btn_exit = (FloatingActionButton) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exitByBackKey();
            }
        });
    }
}
