package com.ankoma88.shoppingdiary;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class ProductManager {
    private static final String TAG = "ProductManager";
    private static final String FILENAME = "products.json";

    private ArrayList<Product> mProducts;
    private ShoppingDiaryJSONSerializer mSerializer;

    private static ProductManager sProductManager;
    private Context mAppContext;

    private ProductManager(Context appContext) {
        mAppContext = appContext;
        mSerializer = new ShoppingDiaryJSONSerializer(mAppContext, FILENAME);

        try {
            mProducts = mSerializer.loadProducts();
        } catch (Exception e) {
            mProducts = new ArrayList<Product>();
            Log.e(TAG, "Error loading products: ", e);
        }
    }

    public static ProductManager get(Context c) {
        if (sProductManager == null) {
            sProductManager = new ProductManager(c.getApplicationContext());
        }
        return sProductManager;
    }

    public Product getProduct(UUID id) {
        for (Product c : mProducts) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public void addProduct(Product p) {
        mProducts.add(p);
        saveProducts();
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    public void deleteCrime(Product p) {
        mProducts.remove(p);
        saveProducts();
    }

    public boolean saveProducts() {
        try {
            mSerializer.saveProducts(mProducts);
            Log.d(TAG, "products saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving products: " + e);
            return false;
        }
    }
}

