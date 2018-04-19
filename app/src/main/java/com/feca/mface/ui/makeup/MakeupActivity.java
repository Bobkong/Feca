package com.feca.mface.ui.makeup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feca.mface.R;
import com.feca.mface.core.facedetection.DetectedFaces;
import com.feca.mface.core.facedetection.FaceDetectService;
import com.feca.mface.core.facemakeup.Eyestick;
import com.feca.mface.core.facemakeup.FacesMakeup;
import com.feca.mface.core.facemakeup.Lipstick;
import com.feca.mface.ui.model.EyeModel;
import com.feca.mface.ui.model.LipstickModel;
import com.feca.mface.ui.model.MakeupModeModel;
import com.feca.mface.util.RxBitmap;
import com.feca.mface.widget.OnRecyclerViewItemClickListener;
import com.feca.mface.widget.ReadOnlyAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Bob on 2017/9/10.
 */
@SuppressLint("CheckResult")
@EActivity(R.layout.activity_makeup)
public class MakeupActivity extends AppCompatActivity {

    @ViewById(R.id.picture)
    ImageView mPhoto;

    @ViewById(R.id.lipsticks)
    RecyclerView mBottomList;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.makeup_mode)
    TextView mMakeupMode;

    @ViewById(R.id.bottom_bar)
    View mBottomBar;

    private int which_lipstick = -1;
    private boolean ifEyeMade = false;
    private boolean ifLipMade = false;

    TypedArray colors;

    private Bitmap nowPhoto;
    private Bitmap originPhoto;
    private Bitmap onlyEye;
    private Bitmap onlyLip;

    private LipstickViewHolder mSelectedLipstickHolder;
    private EyeViewHolder mSelectedEyeHolder;

    private RecyclerView.Adapter<LipstickViewHolder> mLipstickColorAdapter;
    private RecyclerView.Adapter<EyeViewHolder> mEyeColorAdapter;
    private final RecyclerView.Adapter<MakeupModeViewHolder> mMakeupModeAdapter =
            new ReadOnlyAdapter<>(MakeupModeModel.modes(), MakeupModeViewHolder.class);
    private FacesMakeup mFacesMakeup;

    @SuppressLint("CheckResult")
    private void initFaceMakeup() {
        Uri uri = getIntent().getData();
        if (uri == null)
            return;
        Observable<Bitmap> bitmapObservable = RxBitmap.decodeBitmap(getContentResolver(), uri, 1024 * 1500);
        Observable<DetectedFaces> detectedFacesObservable =
                bitmapObservable.flatMap(new Function<Bitmap, ObservableSource<DetectedFaces>>() {
                    @Override
                    public ObservableSource<DetectedFaces> apply(@NonNull Bitmap bitmap) throws Exception {
                        return FaceDetectService.getInstance().detect(bitmap);

                    }
                });
        Observable.zip(bitmapObservable, detectedFacesObservable, new BiFunction<Bitmap, DetectedFaces, FacesMakeup>() {
            @Override
            public FacesMakeup apply(@NonNull Bitmap bitmap, @NonNull DetectedFaces faces) {
                return new FacesMakeup(bitmap, faces);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FacesMakeup>() {
                    @Override
                    public void accept(@NonNull FacesMakeup o) {
                        mFacesMakeup = o;
                        mPhoto.setImageBitmap(o.getOriginalFace());
                        nowPhoto = o.getOriginalFace();
                        originPhoto = o.getOriginalFace();
                    }
                });

    }


    @AfterViews
    void setUpViews() {
        initFaceMakeup();
        setupToolbar();

        //获取眼影颜色
        colors = this.getResources().obtainTypedArray(R.array.eye_colors);

        mBottomList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBottomList.setAdapter(mMakeupModeAdapter);
        mLipstickColorAdapter = new ReadOnlyAdapter<>(LipstickModel.lipsticks(this)
                , LipstickViewHolder.class);
        mEyeColorAdapter = new ReadOnlyAdapter<>(EyeModel.eyesticks(this)
                , EyeViewHolder.class);
        mBottomList.addOnItemTouchListener(new OnRecyclerViewItemClickListener(this) {
            @Override
            public void onItemClick(View view, int position) {
                RecyclerView.ViewHolder holder = mBottomList.getChildViewHolder(view);
                if (holder instanceof MakeupModeViewHolder) {
                    onMakeupModeSelected((MakeupModeViewHolder) holder, position);
                } else if (holder instanceof LipstickViewHolder) {
                    onLipstickColorSelected((LipstickViewHolder) holder, position);
                    which_lipstick = position;
                } else if (holder instanceof EyeViewHolder) {
                    onEyeColorSelected((EyeViewHolder) holder, position);
                }
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onEyeColorSelected(EyeViewHolder holder, final int position) {
        if (mSelectedEyeHolder != null) {
            mSelectedEyeHolder.mEyeColor.setImageDrawable(null);
        }
        mSelectedEyeHolder = holder;
        mSelectedEyeHolder.mEyeColor.setImageResource(R.drawable.ic_done_white_48dp);
        Integer[] eye_colors = new Integer[4];
        eye_colors[0] = colors.getColor(position * 4, 0);
        eye_colors[1] = colors.getColor(position * 4 + 1, 0);
        eye_colors[2] = colors.getColor(position * 4 + 2, 0);
        eye_colors[3] = colors.getColor(position * 4 + 3, 0);
        Observable.just(eye_colors)
                .map(new Function<Integer[], Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull Integer[] color) throws Exception {
                        if (ifEyeMade){
                            mFacesMakeup.setmOriginalFace(originPhoto);
                        }
                        if (which_lipstick == 8)
                            mFacesMakeup.reset();
                        mFacesMakeup.makeup(new Eyestick(color[2], color[3]));
                        mFacesMakeup.makeup(new Eyestick(color[1], color[3]));
                        mFacesMakeup.makeup(new Eyestick(color[0], color[3]));
                        which_lipstick = 8;
                        return mFacesMakeup.getMadeUpFace();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(@NonNull Bitmap bitmap) throws Exception {
                        mPhoto.setImageBitmap(bitmap);
                        nowPhoto = bitmap;
                    }
                });
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void onLipstickColorSelected(LipstickViewHolder holder, final int position) {
        if (mSelectedLipstickHolder != null) {
            mSelectedLipstickHolder.mLipstickColor.setImageDrawable(null);
        }
        mSelectedLipstickHolder = holder;
        mSelectedLipstickHolder.mLipstickColor.setImageResource(R.drawable.ic_done_white_48dp);
        LipstickModel lipstickModel = LipstickModel.lipsticks(this).get(position);
        Observable.just(lipstickModel.getColor())
                .map(new Function<Integer, Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull Integer color) throws Exception {
                        if (ifEyeMade){
                            mFacesMakeup.setmOriginalFace(originPhoto);
                        }
                        if (which_lipstick >= 0 && which_lipstick <=7)
                            mFacesMakeup.reset();
                        mFacesMakeup.makeup(new Lipstick(color));
                        which_lipstick = position;
                        return mFacesMakeup.getMadeUpFace();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(@NonNull Bitmap bitmap) throws Exception {
                        mPhoto.setImageBitmap(bitmap);
                        nowPhoto = bitmap;
                    }
                });
    }

    private void onMakeupModeSelected(MakeupModeViewHolder holder, int position) {
        if(mFacesMakeup == null){
            Toast.makeText(this, R.string.wait_for_detection, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mFacesMakeup.isFaceDetected()){
            Toast.makeText(this, R.string.no_face_detected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (position == 0) {
            mBottomList.setAdapter(mLipstickColorAdapter);
            mBottomBar.setVisibility(View.VISIBLE);
            mMakeupMode.setText(holder.mModeName.getText());
        } else if (position == 1) {
            mBottomList.setAdapter(mEyeColorAdapter);
            mBottomBar.setVisibility(View.VISIBLE);
            mMakeupMode.setText(holder.mModeName.getText());
        } else {
            Toast.makeText(this, R.string.developing, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    void savePhoto() {
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean granted) throws Exception {
                        if (granted) {
                            doSavingPhoto();
                        } else {
                            Toast.makeText(MakeupActivity.this, R.string.no_permission, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Click(R.id.close)
    void undoMakeup() {
        mPhoto.setImageBitmap(mFacesMakeup.getOriginalFace());
        which_lipstick = -1;
        exitMakeupMode();
    }

    private void exitMakeupMode() {

        mBottomBar.setVisibility(View.GONE);
        mBottomList.setAdapter(mMakeupModeAdapter);
    }

    @Click(R.id.done)
    void completeMakeup() {
        if (which_lipstick == 8){
            ifEyeMade = true;
        }
        else if(which_lipstick >= 0 && which_lipstick <= 7){
            ifLipMade = true;
        }
        mFacesMakeup.setmOriginalFace(nowPhoto);
        exitMakeupMode();
    }


    private void doSavingPhoto() {
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
        RxBitmap.saveBitmap(mFacesMakeup.getMadeUpFace(), file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(@NonNull File file) throws Exception {
                        Toast.makeText(MakeupActivity.this, "Saved to " + file.getPath(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    void buy() {
        Uri uri;
        switch (which_lipstick) {
            case -1:
                uri = Uri.parse("https://list.tmall.com/search_product.htm?q=%E5%8F%A3%E7%BA%A2&type=p&vmarket=&spm=875.7931836%2FB.a2227oh.d100&from=mallfp..pc_1_searchbutton");
                break;
            case 0:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.44.46278078cJh4TU&id=558489127914&ns=1&abbucket=16#detail");
                break;
            case 1:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.19.48e4c161RvSKVM&id=558437348781&ns=1&abbucket=16#detail");
                break;
            case 2:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.19.7c9d2dd31r6Rxx&id=567044414925&ns=1&abbucket=16#detail");
                break;
            case 3:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.131.3208a650vNMWSZ&id=544703253902&ns=1&abbucket=16#detail");
                break;
            case 4:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.26.214e637adbqgrn&id=565146846350&ns=1&abbucket=16#detail");
                break;
            case 5:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.44.33fb746fWJorog&id=564200224082&ns=1&abbucket=16#detail");
                break;
            case 6:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.79.5ac7b04fSypD9Y&id=562721016722&ns=1&abbucket=16#detail");
                break;
            case 7:
                uri = Uri.parse("https://item.taobao.com/item.htm?spm=a230r.1.14.14.14e63b026wf7op&id=563866569196&ns=1&abbucket=16#detail");
                break;
            case 8:
                uri = Uri.parse("https://detail.tmall.com/item.htm?id=555683378365&ali_refid=a3_430583_1006:1151043678:N:%E7%9C%BC%E5%BD%B1:b24a0ab2032fa8ab058f7e5006b5ed28&ali_trackid=1_b24a0ab2032fa8ab058f7e5006b5ed28&spm=a230r.1.14.3");
                break;
            default:
                uri = Uri.parse("https://list.tmall.com/search_product.htm?q=%E5%8F%A3%E7%BA%A2&type=p&vmarket=&spm=875.7931836%2FB.a2227oh.d100&from=mallfp..pc_1_searchbutton");
                break;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_makeup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buy:
                buy();
                break;
            case R.id.save:
                savePhoto();
                break;
        }
        return true;
    }

}
