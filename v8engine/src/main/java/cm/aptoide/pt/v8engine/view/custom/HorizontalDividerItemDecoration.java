package cm.aptoide.pt.v8engine.view.custom;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import cm.aptoide.pt.v8engine.R;

public class HorizontalDividerItemDecoration extends DrawableItemDecoration {

  public HorizontalDividerItemDecoration(Context context) {
    this(context, 0);
  }

  public HorizontalDividerItemDecoration(Context context, int customHorizontalPadding) {
    super(ContextCompat.getDrawable(context, R.drawable.gray_item_decorator),
        customHorizontalPadding);
  }
}
