package com.feca.mface.ui.makeup;

import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feca.mface.R;
import com.feca.mface.ui.model.EyeModel;
import com.feca.mface.ui.model.LipstickModel;
import com.feca.mface.widget.BindableViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bob on 2018/4/18.
 */


public class EyeViewHolder extends BindableViewHolder<EyeModel> {

    @BindView(R.id.color)
    ImageView mEyeColor;

    @BindView(R.id.name)
    TextView mName;

    private GradientDrawable mEyeColorBackground;

    public EyeViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_lipstick);
        ButterKnife.bind(this, itemView);
        mEyeColorBackground = (GradientDrawable) mEyeColor.getBackground();
    }

    @Override
    public void bind(EyeModel lipstick, int position) {
        mEyeColorBackground.setColor(lipstick.getColor());
        mName.setText(lipstick.getName());
    }
}
