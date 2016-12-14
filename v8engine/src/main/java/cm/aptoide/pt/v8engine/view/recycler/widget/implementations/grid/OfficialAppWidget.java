package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OfficialAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

public class OfficialAppWidget extends Widget<OfficialAppDisplayable> {

  private static final String TAG = OfficialAppWidget.class.getName();

  private Button installButton;
  private TextView installMessage;
  private TextView appName;
  private View appRating;
  private View verticalSeparator;
  private TextView appDownloads;
  private TextView appVersion;
  private TextView appSize;

  public OfficialAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    installButton = (Button) itemView.findViewById(R.id.app_install_button);
    installMessage = (TextView) itemView.findViewById(R.id.install_message);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    verticalSeparator = itemView.findViewById(R.id.vertical_separator);
    appRating = itemView.findViewById(R.id.app_rating);
    appDownloads = (TextView) itemView.findViewById(R.id.app_downloads);
    appVersion = (TextView) itemView.findViewById(R.id.app_version);
    appSize = (TextView) itemView.findViewById(R.id.app_size);
  }

  @Override public void bindView(OfficialAppDisplayable displayable) {

    final FragmentActivity context = getContext();
    final Pair<String, GetApp> messageAndApp = displayable.getMessageGetApp();
    final boolean isAppInstalled = isAppInstalled(messageAndApp.second);

    int color;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      color = context.getResources().getColor(R.color.default_color, context.getTheme());
    } else {
      color = context.getResources().getColor(R.color.default_color);
    }

    final GetAppMeta.App appData = messageAndApp.second.getNodes().getMeta().getData();
    final String appName = appData.getName();

    if (!TextUtils.isEmpty(messageAndApp.first)) {

      final String[] parts = Translator.translateToMultiple(messageAndApp.first);
      SpannableString middle =
          new SpannableString(String.format(isAppInstalled ? parts[1] : parts[2], appName));
      middle.setSpan(new ForegroundColorSpan(color), 0, middle.length(), Spanned.SPAN_MARK_MARK);

      SpannableStringBuilder text = new SpannableStringBuilder();
      text.append(parts[0]);
      text.append(middle);
      text.append(parts[1]);
      installMessage.setText(text);
    } else {
      installMessage.setVisibility(View.GONE);
      verticalSeparator.setVisibility(View.GONE);
    }

    this.appName.setText(appName);
    this.appDownloads.setText(String.format(context.getString(R.string.downloads_count),
        AptoideUtils.StringU.withSuffix(appData.getStats().getDownloads())));

    this.appVersion.setText(
        String.format(context.getString(R.string.version_number), appData.getFile().getVername()));

    this.appSize.setText(String.format(context.getString(R.string.app_size),
        AptoideUtils.StringU.formatBytes(appData.getFile().getFilesize())));

    // check if app is installed. if it is, show open button

    compositeSubscription.add(RxView.clicks(installButton).subscribe(a -> {

      // TODO: 7/12/2016 sithengineer
      ShowMessage.asSnack(installButton, "to do");
    }, err -> {
      Log.e(TAG, "", err);
      CrashReports.logException(err);
    }));
  }

  private boolean isAppInstalled(GetApp app) {
    //InstalledRepository installedRepo = RepositoryFactory.getRepositoryFor(Installed.class);
    //return installedRepo.contains(app.getNodes().getMeta().getData().getPackageName());
    return false;
  }
}
