package com.android.dioilham.restaurant.ui;

/**
 * Created by hasyim on 12/20/2015.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.dioilham.restaurant.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

//import butterknife.Bind;
//import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity  {

    @Bind(R.id.profile_picture)
    CircleImageView profilePicture;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.nim)
    TextView nim;
    @Bind(R.id.prodi)
    TextView prodi;
    @Bind(R.id.jurusan)
    TextView jurusan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initialize();
    }

    protected void initialize() {



        /*name.setText(getResources().getString(R.string.me));
        nim.setText(getResources().getString(R.string.npm));
        prodi.setText(getResources().getString(R.string.fakultas));
        jurusan.setText(getResources().getString(R.string.jurusan));*/

    }

}
