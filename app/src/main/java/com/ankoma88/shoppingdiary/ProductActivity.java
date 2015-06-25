package com.ankoma88.shoppingdiary;

import android.support.v4.app.Fragment;

import java.util.UUID;

public class ProductActivity extends SingleFragmentActivity {

    static final String ACTION_SCAN = "shoppingDiary.SCAN";

	@Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID)getIntent()
            .getSerializableExtra(ProductFragment.EXTRA_PRODUCT_ID);
        return ProductFragment.newInstance(crimeId);
    }
}
