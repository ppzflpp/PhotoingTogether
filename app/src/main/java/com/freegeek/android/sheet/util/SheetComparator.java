package com.freegeek.android.sheet.util;

import com.freegeek.android.sheet.bean.Sheet;

import java.util.Comparator;

/**
 * Created by rtugeek on 15-12-9.
 */
public class SheetComparator implements Comparator<Sheet>{
    @Override
    public int compare(Sheet u1, Sheet u2) {
        if (u1.equals(u2)){
            return 0;
        } else if (u1.getLiker().size() < u2.getLiker().size()){
            return 1;
        } else if (u1.getLiker().size()  == u2.getLiker().size() ){
            return 0;
        }else {
            return -1;
        }
    }
}
