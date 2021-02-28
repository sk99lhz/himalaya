package com.lhz.sk.himalaya.utils;

import com.lhz.sk.himalaya.bases.BaseFragment;
import com.lhz.sk.himalaya.fragments.FindFragment;
import com.lhz.sk.himalaya.fragments.HistoryFragment;
import com.lhz.sk.himalaya.fragments.RecommendFragment;
import com.lhz.sk.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by song
 */
public class FragmentCreator {
    private static Map<Integer, BaseFragment> sCache = new HashMap<>();
    public static int PAGE_COUNT = 4;

    public static BaseFragment getFragment(int index) {
        BaseFragment baseFragment = sCache.get(index);
        if (baseFragment != null) {
            return baseFragment;
        }
        if (index == 0) {
            baseFragment = new RecommendFragment();
        } else if (index == 1) {
            baseFragment = new FindFragment();
        } else if (index == 2) {
            baseFragment = new SubscriptionFragment();
        } else if (index == 3) {
            baseFragment = new HistoryFragment();
        }
        sCache.put(index, baseFragment);
        return baseFragment;
    }
}
