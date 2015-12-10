package com.freegeek.android.sheet.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.util.APP;

/**
 * Created by rtugeek on 15-12-10.
 */
public class PickThemeDialog extends BaseDialog implements View.OnClickListener{

    public PickThemeDialog(BaseActivity context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pick_theme,null);
        view.findViewById(R.id.theme_pick_img_1).setOnClickListener(this);
        view.findViewById(R.id.theme_pick_img_2).setOnClickListener(this);
        view.findViewById(R.id.theme_pick_img_3).setOnClickListener(this);
        view.findViewById(R.id.theme_pick_img_4).setOnClickListener(this);
        view.findViewById(R.id.theme_pick_img_5).setOnClickListener(this);
        view.findViewById(R.id.theme_pick_img_6).setOnClickListener(this);
        positiveAction(getString(R.string.confirm));
        positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setContentView(view);
    }

    @Override
    public void onClick(View v) {
        spe.putInt(APP.KEY.THEME,Integer.parseInt(v.getTag().toString())).commit();
        showToast("");
    }
}
