package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import rx.Observable;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadAnalyticsRequest
    extends AnalyticsBaseRequest<DownloadInstallAnalyticsBaseBody> {

  private String action;
  private String name;
  private String context;

  protected DownloadAnalyticsRequest(DownloadInstallAnalyticsBaseBody body, String action, String name, String context) {
    super(body);
    this.action = action;
    this.name = name;
    this.context = context;
  }

  public static DownloadAnalyticsRequest of(DownloadInstallAnalyticsBaseBody body, String action,
      String name, String context, BodyInterceptor bodyInterceptor) {
    return new DownloadAnalyticsRequest(
        (DownloadInstallAnalyticsBaseBody) bodyInterceptor.intercept(body), action,
        name, context);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(name, action, context, body);
  }
}
