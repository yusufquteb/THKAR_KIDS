package com.isslam.husonkids;

import android.animation.ObjectAnimator;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MyPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private boolean swipedLeft = false;
    public static int lastPage = 9;
    private MyLinearLayout cur = null;
    private MyLinearLayout next = null;
    private MyLinearLayout prev = null;
    private MyLinearLayout nextnext = null;
    private MainActivity context;
    private FragmentManager fm;
    private float scale;
    private boolean IsBlured;
    private static float minAlpha = 0.6f;
    private static float maxAlpha = 1f;
    private static float minDegree = 60.0f;

    public static float getMinDegree() { return minDegree; }
    public static float getMinAlpha() { return minAlpha; }
    public static float getMaxAlpha() { return maxAlpha; }

    public MyPagerAdapter(MainActivity context, FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fm = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == MainActivity.FIRST_PAGE)
            scale = MainActivity.BIG_SCALE;
        else {
            scale = MainActivity.SMALL_SCALE;
            IsBlured = true;
        }
        Log.d("position", String.valueOf(position));
        curFragment = MyFragment.newInstance(context, position, scale, IsBlured);
        cur = getRootView(position);
        next = getRootView(position + 1);
        prev = getRootView(position - 1);
        return curFragment;
    }

    Fragment curFragment;

    @Override
    public int getCount() { return 10; }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            positionOffset = positionOffset * positionOffset;
            cur = getRootView(position);
            next = getRootView(position + 1);
            prev = getRootView(position - 1);
            nextnext = getRootView(position + 2);

            if (cur != null) cur.setAlpha(maxAlpha - 0.5f * positionOffset);
            if (next != null) next.setAlpha(minAlpha + 0.5f * positionOffset);
            if (prev != null) prev.setAlpha(minAlpha + 0.5f * positionOffset);

            if (nextnext != null) {
                nextnext.setAlpha(minAlpha);
                nextnext.setRotationY(-minDegree);
            }
            if (cur != null) {
                cur.setScaleBoth(MainActivity.BIG_SCALE - MainActivity.DIFF_SCALE * positionOffset);
                cur.setRotationY(0 + minDegree * positionOffset);
            }
            if (next != null) {
                next.setScaleBoth(MainActivity.SMALL_SCALE + MainActivity.DIFF_SCALE * positionOffset);
                next.setRotationY(-minDegree + minDegree * positionOffset);
            }
            if (prev != null) {
                prev.setRotationY(minDegree);
            }
        }
        if (positionOffset >= 1f) {
            if (cur != null) cur.setAlpha(maxAlpha);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.e("selected", "selected");
        if (lastPage <= position) swipedLeft = true;
        else if (lastPage > position) swipedLeft = false;
        if (context != null) ((MainActivity) context).OnpageSelected(position, lastPage);
        if (getFragment(lastPage) != null) getFragment(lastPage).RotateBgImage(false);
        if (getFragment(position) != null) getFragment(position).RotateBgImage(true);
        Log.e("positions", position + " : " + lastPage);
        lastPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 1) {
            Log.e("changed", "changed");
            if (context != null) ((MainActivity) context).onPageChanged();
        }
        if (state == 0) {
            if (context != null) ((MainActivity) context).OnpageRemains();
        }
        Log.e("state", state + "");
    }

    private MyFragment getFragment(int position) {
        try {
            return (MyFragment) fm.findFragmentByTag(this.getFragmentTag(position));
        } catch (Exception e) {
            return null;
        }
    }

    private MyLinearLayout getRootView(int position) {
        try {
            return (MyLinearLayout) fm.findFragmentByTag(this.getFragmentTag(position))
                    .getView().findViewById(R.id.root);
        } catch (Exception e) {
            return null;
        }
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }
}
