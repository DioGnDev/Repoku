package com.android.dioilham.restaurant.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;


import com.android.dioilham.restaurant.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hasyim on 12/20/2015.
 */
public class PetunjukActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petunjuk);
        ButterKnife.bind(this);

        initialize();
    }

    protected void initialize() {

    }

}
