package com.ankoma88.shoppingdiary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

public class ProductFragment extends Fragment {
    public static final String EXTRA_PRODUCT_ID = "shoppingDiary.CRIME_ID";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_SCAN = 2;

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";



    Product mProduct;
    EditText mProductNameField;
    EditText mCommentField;
    Button mDateButton;
    Button mAddProductButton;
    Button mScanButton;
    CheckBox mPurchasedCheckBox;
    ImageButton mPhotoButton;
    ImageView mPhotoView;
    Callbacks mCallbacks;

    public interface Callbacks {
        void onProductUpdated(Product product);
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

    public static ProductFragment newInstance(UUID productId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PRODUCT_ID, productId);

        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID productId = (UUID)getArguments().getSerializable(EXTRA_PRODUCT_ID);
        mProduct = ProductManager.get(getActivity()).getProduct(productId);

        setHasOptionsMenu(true);

    }

    public void updateDate() {
        mDateButton.setText(mProduct.getDate().toString());
    }

    @Override
    @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product, parent, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }



        mProductNameField = (EditText)v.findViewById(R.id.product_name);
        mProductNameField.setText(mProduct.getProductName());
        mProductNameField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mProduct.setProductName(c.toString());
                mCallbacks.onProductUpdated(mProduct);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {

            }

            public void afterTextChanged(Editable c) {

            }
        });

        mCommentField = (EditText)v.findViewById(R.id.product_comment);
        mCommentField.setText(mProduct.getComment());
        mCommentField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mProduct.setComment(c.toString());
                mCallbacks.onProductUpdated(mProduct);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
            }
        });

        mPurchasedCheckBox = (CheckBox)v.findViewById(R.id.product_purchased);
        mPurchasedCheckBox.setChecked(mProduct.isPurchased());
        mPurchasedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the product's purchased property
                mProduct.setPurchased(isChecked);
                mCallbacks.onProductUpdated(mProduct);
            }
        });

        mDateButton = (Button)v.findViewById(R.id.purchase_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                    .newInstance(mProduct.getDate());
                dialog.setTargetFragment(ProductFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });


        mScanButton = (Button) v.findViewById(R.id.scanner);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent i = new Intent(ACTION_SCAN);
                    i.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(i, REQUEST_SCAN);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(getActivity(), "No Scanner Found", "Download scanner", "Yes", "No").show();
                }
            }
        });


        mAddProductButton = (Button) v.findViewById(R.id.addProductBtn);
        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.product_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // launch camera activity
                Intent i = new Intent(getActivity(), ProductCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        // if camera is not available - disable
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView)v.findViewById(R.id.product_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mProduct.getPhoto();
                if (p == null)
                    return;

                FragmentManager fm = getActivity()
                    .getSupportFragmentManager();
                String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.createInstance(path)
                    .show(fm, DIALOG_IMAGE);
            }
        });

        return v;
    }

    private void showPhoto() {
        // set the image button's image based on photo
        Photo p = mProduct.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PhotoManager.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }


//bar scanner integration
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }


    @Override
    public void onStop() {
        super.onStop();
        PhotoManager.cleanImageView(mPhotoView);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mProduct.setDate(date);
            mCallbacks.onProductUpdated(mProduct);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            // create a new Photo object and attach it to the product
            String filename = data
                .getStringExtra(ProductCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Photo p = new Photo(filename);
                mProduct.setPhoto(p);
                mCallbacks.onProductUpdated(mProduct);
                showPhoto();
            }
        } else if (requestCode == REQUEST_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                Toast toast = Toast.makeText(getActivity(), "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        ProductManager.get(getActivity()).saveProducts();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
