package com.freegeek.android.sheet.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.bean.Event;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by rtugeek@gmail.com on 2015/11/8.
 */
public class BaseFragment extends Fragment {
    protected boolean showing = false;
    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        showing = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        showing = false;
    }

    protected void showLoading(){
        ((BaseActivity)getActivity()).showLoading();
    }

    protected void dismissLoading(){
        ((BaseActivity)getActivity()).dismissLoading();
    }

    public void onEvent(Event event){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public BaseFragment() {
    }

    public static BaseFragment getShowingFragement(FragmentManager fragmentManager){
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment: fragments){
            BaseFragment fragmentBase = (BaseFragment)fragment;
            if( fragmentBase != null)
                if(fragmentBase.showing ) return fragmentBase;

        }
        return null;
    }
}
