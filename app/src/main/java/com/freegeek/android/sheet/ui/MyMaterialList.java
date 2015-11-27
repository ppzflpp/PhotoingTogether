package com.freegeek.android.sheet.ui;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.dexafree.materialList.view.MaterialListView;

/**
 * Created by rtugeek on 15-11-27.
 */
public class MyMaterialList extends MaterialListView {

    public MyMaterialList(Context context) {
        super(context);
    }

    public MyMaterialList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMaterialList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            super.onRestoreInstanceState(state);
        }catch (Exception e) {}
        state=null;
    }
}
