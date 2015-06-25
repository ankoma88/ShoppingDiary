package com.ankoma88.shoppingdiary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductListFragment extends ListFragment {
    private ArrayList<Product> mProducts;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onProductSelected(Product product);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void updateUI() {
        ((ProductAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.products_title);
        mProducts = ProductManager.get(getActivity()).getProducts();
        ProductAdapter adapter = new ProductAdapter(mProducts);
        setListAdapter(adapter);
        setRetainInstance(true);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        ListView listView = (ListView)v.findViewById(android.R.id.list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.product_list_item_context, menu);
                    return true;
                }

                public void onItemCheckedStateChanged(ActionMode mode, int position,
                        long id, boolean checked) {
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_product:
                            ProductAdapter adapter = (ProductAdapter)getListAdapter();
                            ProductManager productManager = ProductManager.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    productManager.deleteCrime(adapter.getItem(i));
                                }
                            }
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {

                }
            });

        }

        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // get Product from the adapter
        Product c = ((ProductAdapter)getListAdapter()).getItem(position);
        mCallbacks.onProductSelected(c);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((ProductAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_product_list, menu);
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_product:
                Product product = new Product();
                ProductManager.get(getActivity()).addProduct(product);
                ((ProductAdapter)getListAdapter()).notifyDataSetChanged();
                mCallbacks.onProductSelected(product);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.product_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        ProductAdapter adapter = (ProductAdapter)getListAdapter();
        Product product = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_product:
                ProductManager.get(getActivity()).deleteCrime(product);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private class ProductAdapter extends ArrayAdapter<Product> {
        public ProductAdapter(ArrayList<Product> products) {
            super(getActivity(), android.R.layout.simple_list_item_1, products);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if no view - inflate it
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                    .inflate(R.layout.list_item_product, null);
            }

            // configure the view for this Product
            Product c = getItem(position);

            TextView productNameTextView =
                (TextView)convertView.findViewById(R.id.product_list_item_nameTextView);
            productNameTextView.setText(c.getProductName());
            TextView dateTextView =
                (TextView)convertView.findViewById(R.id.product_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());
            CheckBox purchasedCheckBox =
                (CheckBox)convertView.findViewById(R.id.product_list_item_purchasedCheckBox);
            purchasedCheckBox.setChecked(c.isPurchased());

            return convertView;
        }
    }
}

