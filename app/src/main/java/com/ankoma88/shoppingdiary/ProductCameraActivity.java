package com.ankoma88.shoppingdiary;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.Window;
import android.view.WindowManager;

public class ProductCameraActivity extends SingleFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // hide window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new ProductCameraFragment();
    }
}

