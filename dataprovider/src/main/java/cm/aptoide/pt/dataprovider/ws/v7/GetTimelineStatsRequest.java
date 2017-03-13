package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.TimelineStats;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by trinkes on 15/12/2016.
 */

public class GetTimelineStatsRequest extends V7<TimelineStats, GetTimelineStatsRequest.Body> {

  protected GetTimelineStatsRequest(GetTimelineStatsRequest.Body body, String baseHost) {
    super(body, baseHost,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter());
  }

  public static GetTimelineStatsRequest of(BodyInterceptor bodyInterceptor, Long userId) {
    return new GetTimelineStatsRequest((Body) bodyInterceptor.intercept(new Body(userId)), BASE_HOST);
  }

  @Override protected Observable<TimelineStats> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getTimelineStats(body, bypassCache);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {
    private Long userId;

    public Body(Long userId) {
      this.userId = userId;
    }
  }
}
