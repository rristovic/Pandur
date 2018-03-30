package com.pandurbg.android.ui.MainActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import com.pandurbg.android.R;
import com.pandurbg.android.model.Post;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Radovan Ristovic on 3/22/2018.
 * Quantox.com
 * radovanr995@gmail.com
 */

public class AddNewPostDialog extends Dialog {

    private final Post mModel;
    @BindView(R.id.tv_post_description)
    TextView mTvDescription;
    @BindView(R.id.tv_post_street)
    TextView mTvPostStreet;
    @BindView(R.id.tv_post_time)
    TextView mTvPostTime;

    public AddNewPostDialog(@NonNull Context context, Post p) {
        super(context);
        this.mModel = p;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.post_dialog_popup);
        ButterKnife.bind(this);
        mTvDescription.setText(mModel.getDescription());
        mTvPostStreet.setText(mModel.getStreet());
        mTvPostTime.setText(mModel.getTime());
    }


    @OnClick(R.id.btn_close_popup)
    public void closePopup() {
        this.dismiss();
    }

}
