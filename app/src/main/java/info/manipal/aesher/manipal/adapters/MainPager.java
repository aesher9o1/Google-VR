package info.manipal.aesher.manipal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import info.manipal.aesher.manipal.fragments.ByClick;
import info.manipal.aesher.manipal.fragments.BySpeech;

/**
 * Created by aesher on 2/28/2018.
 */

public class MainPager extends FragmentStatePagerAdapter {
    public MainPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 : return new ByClick();
            case 1:  return new BySpeech();
            default: return new ByClick();

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object){
        return android.support.v4.view.PagerAdapter.POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
