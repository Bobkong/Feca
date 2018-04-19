package com.feca.mface.ui.model;

import android.content.Context;
import android.content.res.TypedArray;

import com.feca.mface.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bob on 2018/4/18.
 */

public class EyeModel {
    private static final List<EyeModel> EYE = new ArrayList<>();
    private int mColor;
    private String mName;

    public EyeModel(int color, String name) {
        mColor = color;
        mName = name;
    }


    public static List<EyeModel> eyesticks(Context context) {
        if (!EYE.isEmpty())
            return EYE;
        TypedArray colors = context.getResources().obtainTypedArray(R.array.eye_color_logo);
        TypedArray names = context.getResources().obtainTypedArray(R.array.eye_names);
        for (int i = 0; i < names.length();i++) {
            int c = colors.getColor(i, 0);
            EYE.add(new EyeModel(c, names.getString(i)));
        }
        colors.recycle();
        names.recycle();
        return EYE;
    }


    public int getColor() {
        return mColor;
    }

    public String getName() {
        return mName;
    }
}
