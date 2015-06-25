package com.ankoma88.shoppingdiary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

public class ProductPagerActivity extends FragmentActivity implements ProductFragment.Callbacks {
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ArrayList<Product> products = ProductManager.get(this).getProducts();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return products.size();
            }
            @Override
            public Fragment getItem(int pos) {
                UUID productId =  products.get(pos).getId();
                return ProductFragment.newInstance(productId);
            }
        });

        UUID productId = (UUID)getIntent().getSerializableExtra(ProductFragment.EXTRA_PRODUCT_ID);
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(productId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public void onProductUpdated(Product product) {
    }


}
