package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.AppViewConfiguration;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppStats;
import cm.aptoide.pt.view.app.AppsList;
import cm.aptoide.pt.view.app.DetailedApp;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import cm.aptoide.pt.view.app.FlagsVote;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AppViewManager {

  private final UpdatesManager updatesManager;
  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final AppCenter appCenter;
  private final ReviewsManager reviewsManager;
  private final AdsManager adsManager;
  private final StoreManager storeManager;
  private final FlagManager flagManager;
  private final StoreUtilsProxy storeUtilsProxy;
  private final AptoideAccountManager aptoideAccountManager;
  private final AppViewConfiguration appViewConfiguration;
  private final int limit;
  private PreferencesManager preferencesManager;
  private DownloadStateParser downloadStateParser;
  private AppViewAnalytics appViewAnalytics;
  private NotificationAnalytics notificationAnalytics;
  private DetailedApp cachedApp;

  public AppViewManager(UpdatesManager updatesManager, InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, StoreManager storeManager, FlagManager flagManager,
      StoreUtilsProxy storeUtilsProxy, AptoideAccountManager aptoideAccountManager,
      AppViewConfiguration appViewConfiguration, PreferencesManager preferencesManager,
      DownloadStateParser downloadStateParser, AppViewAnalytics appViewAnalytics,
      NotificationAnalytics notificationAnalytics, int limit) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.appCenter = appCenter;
    this.reviewsManager = reviewsManager;
    this.adsManager = adsManager;
    this.storeManager = storeManager;
    this.flagManager = flagManager;
    this.storeUtilsProxy = storeUtilsProxy;
    this.aptoideAccountManager = aptoideAccountManager;
    this.appViewConfiguration = appViewConfiguration;
    this.preferencesManager = preferencesManager;
    this.downloadStateParser = downloadStateParser;
    this.appViewAnalytics = appViewAnalytics;
    this.notificationAnalytics = notificationAnalytics;
    this.limit = limit;
  }

  public Single<AppViewViewModel> loadAppViewViewModel() {
    if (appViewConfiguration.getAppId() >= 0) {
      return loadAppViewViewModel(appViewConfiguration.getAppId(),
          appViewConfiguration.getStoreName(), appViewConfiguration.getPackageName());
    } else if (appViewConfiguration.hasMd5()) {
      return loadAppViewViewModelFromMd5(appViewConfiguration.getMd5());
    } else if (appViewConfiguration.hasUniqueName()) {
      return loadAppViewViewModelFromUniqueName(appViewConfiguration.getUniqueName());
    } else {
      return loadAppViewViewModel(appViewConfiguration.getPackageName(),
          appViewConfiguration.getStoreName());
    }
  }

  public Single<ReviewsViewModel> loadReviewsViewModel(String storeName, String packageName,
      String languagesFilterSort) {
    return reviewsManager.loadReviews(storeName, packageName, 3, languagesFilterSort)
        .map(result -> new ReviewsViewModel(result.getReviewList(), result.isLoading(),
            result.getError()));
  }

  public Single<SimilarAppsViewModel> loadSimilarApps(String packageName, List<String> keyWords) {
    return loadAdForSimilarApps(packageName, keyWords).flatMap(
        ad -> loadRecommended(limit, packageName).map(
            recommendedAppsRequestResult -> new SimilarAppsViewModel(ad,
                recommendedAppsRequestResult.getList(), recommendedAppsRequestResult.isLoading(),
                recommendedAppsRequestResult.getError())));
  }

  public Single<MinimalAd> loadAdsFromAppView(String packageName, String storeName) {
    return adsManager.loadAds(packageName, storeName);
  }

  public Single<Boolean> flagApk(String storeName, String md5, FlagsVote.VoteType type) {
    return flagManager.flagApk(storeName, md5, type.name()
        .toLowerCase())
        .map(response -> (response.isOk() && !response.hasErrors()));
  }

  public Completable subscribeStore(String storeName) {
    return Completable.fromAction(
        () -> storeUtilsProxy.subscribeStore(storeName, null, null, aptoideAccountManager));
  }

  private Single<AppViewViewModel> loadAppViewViewModel(long appId, String storeName,
      String packageName) {
    if (cachedApp != null) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedApp(appId, storeName, packageName)
        .flatMap(result -> map(result));
  }

  private Single<AppViewViewModel> loadAppViewViewModel(String packageName, String storeName) {
    if (cachedApp != null && cachedApp.getPackageName()
        .equals(packageName) && cachedApp.getStore()
        .getName()
        .equals(storeName)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedApp(packageName, storeName)
        .flatMap(result -> map(result));
  }

  private Single<AppViewViewModel> loadAppViewViewModelFromMd5(String md5) {
    if (cachedApp != null && cachedApp.getMd5()
        .equals(md5)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedAppFromMd5(md5)
        .flatMap(result -> map(result));
  }

  private Single<AppViewViewModel> loadAppViewViewModelFromUniqueName(String uniqueName) {
    if (cachedApp != null && cachedApp.getUniqueName()
        .equals(uniqueName)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedAppAppFromUniqueName(uniqueName)
        .flatMap(result -> map(result));
  }

  private Single<AppsList> loadRecommended(int limit, String packageName) {
    return appCenter.loadRecommendedApps(limit, packageName);
  }

  private Single<MinimalAd> loadAdForSimilarApps(String packageName, List<String> keyWords) {
    return adsManager.loadAd(packageName, keyWords);
  }

  private Single<Boolean> isStoreFollowed(long storeId) {
    return storeManager.isSubscribed(storeId)
        .first()
        .toSingle();
  }

  private Single<AppViewViewModel> createAppViewViewModel(DetailedApp app) {
    AppStats stats = app.getStats();
    cachedApp = app;
    return isStoreFollowed(cachedApp.getStore()
        .getId()).map(
        isStoreFollowed -> new AppViewViewModel(app.getId(), app.getName(), app.getStore(),
            appViewConfiguration.getStoreTheme(), app.isGoodApp(), app.getMalware(),
            app.getAppFlags(), app.getTags(), app.getUsedFeatures(), app.getUsedPermissions(),
            app.getFileSize(), app.getMd5(), app.getPath(), app.getPathAlt(), app.getVersionCode(),
            app.getVersionName(), app.getPackageName(), app.getSize(), stats.getDownloads(),
            stats.getGlobalRating(), stats.getPackageDownloads(), stats.getRating(),
            app.getDeveloper(), app.getGraphic(), app.getIcon(), app.getMedia(), app.getModified(),
            app.getAdded(), app.getObb(), app.getPay(), app.getWebUrls(), app.isPaid(),
            app.getUniqueName(), appViewConfiguration.shouldInstall(),
            appViewConfiguration.getAppc(), appViewConfiguration.getMinimalAd(),
            appViewConfiguration.getEditorsChoice(), appViewConfiguration.getOriginTag(),
            isStoreFollowed));
  }

  private Single<AppViewViewModel> map(DetailedAppRequestResult result) {
    if (result.getDetailedApp() != null) {
      return createAppViewViewModel(result.getDetailedApp());
    } else if (result.isLoading()) {
      return Single.just(new AppViewViewModel(result.isLoading()));
    } else if (result.hasError()) {
      return Single.just(new AppViewViewModel(result.getError()));
    } else {
      return Single.just(new AppViewViewModel(DetailedAppRequestResult.Error.GENERIC));
    }
  }

  private void increaseInstallClick() {
    preferencesManager.setNotLoggedInInstallClicks();
  }

  public boolean showRootInstallWarningPopup() {
    return installManager.showWarning();
  }

  public void saveRootInstallWarning(Boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  private GetAppMeta.App buildAppMockedApp() {
    GetAppMeta.App app = new GetAppMeta.App();
    app.setId(37032862);
    app.setName("AutoDoc");
    app.setPackageName("de.autodoc.gmbh");
    app.setSize(29101728);
    app.setIcon("http://pool.img.aptoide.com/wadogo/28e6bfc1151ed8da26a028f1118cc353_icon.png");
    app.setGraphic(
        "http://pool.img.aptoide.com/wadogo/c5ff946d644c0162963fee037149af88_fgraphic_705x345.png");
    app.setAdded("2017-04-27 20:19:19");
    app.setModified("2018-05-02 23:20:59");
    GetAppMeta.GetAppMetaFile file = buildAppFile();
    app.setFile(file);
    return app;
  }

  private GetAppMeta.GetAppMetaFile buildAppFile() {
    GetAppMeta.GetAppMetaFile file = new GetAppMeta.GetAppMetaFile();
    file.setVername("1.4.6");
    file.setVercode(149);
    file.setMd5sum("e900c63e3ca3da65f7c4aa4390b1304c");
    file.setPath(
        "http://pool.apk.aptoide.com/wadogo/de-autodoc-gmbh-149-37032862-e900c63e3ca3da65f7c4aa4390b1304c.apk");
    file.setPathAlt(
        "http://pool.apk.aptoide.com/wadogo/alt/ZGUtYXV0b2RvYy1nbWJoLTE0OS0zNzAzMjg2Mi1lOTAwYzYzZTNjYTNkYTY1ZjdjNGFhNDM5MGIxMzA0Yw.apk");
    GetAppMeta.GetAppMetaFile.Signature signature = buildSignature();
    file.setSignature(signature);
    return file;
  }

  private GetAppMeta.GetAppMetaFile.Signature buildSignature() {
    GetAppMeta.GetAppMetaFile.Signature signature = new GetAppMeta.GetAppMetaFile.Signature();
    signature.setOwner("CN=Alexej Erdle, OU=CEO, O=Autodoc GmbH, L=Berlin, ST=Berlin, C=DE");
    signature.setSha1("C5:49:29:2E:5E:4C:B5:64:3E:44:30:7C:B0:78:98:26:9C:40:00:99");
    return signature;
  }

  public Completable downloadApp(DownloadAppViewModel.Action downloadAction, String packageName,
      long appId) {
    increaseInstallClick();
    return Observable.just(
        downloadFactory.create(cachedApp, downloadStateParser.parseDownloadAction(downloadAction)))
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(__ -> setupDownloadEvents(download, packageName, appId)))
        .toCompletable();
  }

  private void setupDownloadEvents(Download download, String packageName, long appId) {
    appViewAnalytics.setupDownloadEvents(download,
        notificationAnalytics.getCampaignId(packageName, appId),
        notificationAnalytics.getAbTestingGroup(packageName, appId), AnalyticsManager.Action.CLICK);
  }

  public Observable<DownloadAppViewModel> getDownloadAppViewModel(String md5, String packageName,
      int versionCode) {
    return installManager.getInstall(md5, packageName, versionCode)
        .map(install -> new DownloadAppViewModel(
            downloadStateParser.parseDownloadType(install.getType()), install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState())));
  }

  public Completable pauseDownload(String md5) {
    return Completable.fromAction(() -> installManager.stopInstallation(md5));
  }

  public Completable resumeDownload(String md5, String packageName, long appId) {
    return installManager.getDownload(md5)
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(__ -> setupDownloadEvents(download, packageName, appId)));
  }

  public Completable cancelDownload(String md5, String packageName, int versionCode) {
    return Completable.fromAction(
        () -> installManager.removeInstallationFile(md5, packageName, versionCode));
  }
}