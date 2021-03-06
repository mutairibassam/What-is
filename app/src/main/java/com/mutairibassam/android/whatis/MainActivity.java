package com.mutairibassam.android.whatis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText mUserInput;
    private Button mBtnSearch;

    private String mStoredString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserInput = findViewById(R.id.et_whatis);
        mBtnSearch = findViewById(R.id.btn_search);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStoredString = mUserInput.getText().toString().toLowerCase().trim();

                if(TextUtils.isEmpty(mStoredString)) {
                    Toast.makeText(MainActivity.this, "Please type something", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", mStoredString);
                    Log.d(TAG, "onComplete: " + mStoredString);
                    ResultActivity resultActivity = new ResultActivity();
                    resultActivity.setArguments(bundle);
                    resultActivity.show(getSupportFragmentManager(), "identifier");

                }

            }
        });

    }
}