package com.ldt.musicr.ui.widget.bubblepicker.rendering;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import com.ldt.musicr.App;
import com.elmurzaev.music.R;
import com.ldt.musicr.ui.widget.bubblepicker.BubblePickerListener;
import com.ldt.musicr.ui.widget.bubblepicker.model.BubbleGradient;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PickerAdapter<T> extends Adapter implements BubblePickerListener {
  private final static String ROBOTO_BOLD = "roboto_bold.ttf";
  private final static String ROBOTO_MEDIUM = "roboto_medium.ttf";
  private final static String ROBOTO_REGULAR = "roboto_regular.ttf";
  public Typeface mBoldTypeface;
  public Typeface mMediumTypeface;
  public Typeface mRegularTypeface;
  protected Context mContext;
  protected int[] mColors;
  protected ArrayList<T> mData = new ArrayList<>();
  private float oneDp = App.getInstance().getResources().getDimension(R.dimen.oneDP);
  private float unitSize = 45f; // 45dp for minimum size
  private PickerListener mListener;

  public PickerAdapter(Context context) {
    mContext = context;
    init();
  }

  @Override
  public float getCircleRadiusUnit(float width, float height) {
    float minRadiusInPixel = oneDp * 50;
    float sizePerUnit = minRadiusInPixel / ((width > height) ? width : height);
    return 0.075f;
  }

  private void init() {
    if (mContext != null) {
      AssetManager assets = mContext.getAssets();
      mBoldTypeface = Typeface.createFromAsset(assets, ROBOTO_BOLD);
      mMediumTypeface = Typeface.createFromAsset(assets, ROBOTO_MEDIUM);
      mRegularTypeface = Typeface.createFromAsset(assets, ROBOTO_REGULAR);

      mColors = mContext.getResources().getIntArray(R.array.colors);
      oneDp = mContext.getResources().getDimension(R.dimen.oneDP);
    }
  }

  public final void setData(List<T> list) {
    mData.clear();
    if (list != null) mData.addAll(list);
    notifyDataSetChanged();
  }

  public void setListener(PickerListener listener) {
    mListener = listener;
  }

  @Override
  public void destroy() {
    super.destroy();
    removeListener();
    mContext = null;
  }

  public void removeListener() {
    mListener = null;
    BubblePicker picker = getBubblePicker();
    if (picker != null) picker.setListener(null);
  }

  @Override
  public void onBubbleSelected(@NotNull PickerItem item, int position) {
    if (mListener != null) mListener.onPickerSelected(item, position, mData.get(position));
  }

  @Override
  public void onBubbleDeselected(@NotNull PickerItem item, int position) {
    if (mListener != null) mListener.onPickerDeselected(item, position, mData.get(position));
  }

  @Override
  protected void onAttach(BubblePicker picker) {
    super.onAttach(picker);
    picker.setListener(this);
  }

  @Override
  public boolean onBindItem(PickerItem item, boolean create, int i) {
    if (mColors != null) {
      item.setGradient(new BubbleGradient(mColors[(i * 2) % mColors.length],
         mColors[(i * 2) % 10 + 1], BubbleGradient.VERTICAL));
    }

    item.setTypeface(mMediumTypeface);
    item.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
    return true;
  }

  @Override
  public final int getItemCount() {
    return mData.size();
  }

  public interface PickerListener {
    void onPickerSelected(PickerItem item, int position, Object o);

    void onPickerDeselected(PickerItem item, int position, Object o);
  }
}
