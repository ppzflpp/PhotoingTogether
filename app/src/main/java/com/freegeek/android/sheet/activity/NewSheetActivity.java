package com.freegeek.android.sheet.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.freegeek.android.sheet.R;

public class NewSheetActivity extends AppCompatActivity {

    private TextInputLayout mThoughtTextInputLayout;
    private ImageView mSheetImgImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sheet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mThoughtTextInputLayout = (TextInputLayout) findViewById(R.id.input_thought);
        mSheetImgImageView = (ImageView) findViewById(R.id.sheet_img);
    }

}
