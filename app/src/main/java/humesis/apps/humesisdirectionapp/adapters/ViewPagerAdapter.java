package humesis.apps.humesisdirectionapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by dhanraj on 09/10/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("Position ",""+position);
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
