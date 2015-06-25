package com.ankoma88.shoppingdiary;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ProductListActivity extends SingleFragmentActivity
    implements ProductListFragment.Callbacks, ProductFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new ProductListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    public void onProductSelected(Product product) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            // start an instance of ProductPagerActivity
            Intent i = new Intent(this, ProductPagerActivity.class);
            i.putExtra(ProductFragment.EXTRA_PRODUCT_ID, product.getId());
            startActivityForResult(i, 0);

            //adding animation as required in task
            overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = ProductFragment.newInstance(product.getId());

            if (oldDetail != null) {
                ft.remove(oldDetail);
            }

            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }
    }





    public void onProductUpdated(Product product) {
        FragmentManager fm = getSupportFragmentManager();
        ProductListFragment listFragment = (ProductListFragment)
                fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }






}
