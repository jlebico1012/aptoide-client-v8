package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by pedroribeiro on 16/12/16.
 */

public class SetUserRequest extends V7<BaseV7Response, SetUserRequest.Body> {

  private static final String BASE_HOST = "https://ws75-primary.aptoide.com/api/7/";

  protected SetUserRequest(Body body, String baseHost) {
    super(body, baseHost,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter());
  }

  public static SetUserRequest of(String user_access, BodyInterceptor bodyInterceptor) {
    Body body = new Body(user_access);
    return new SetUserRequest((Body) bodyInterceptor.intercept(body), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setUser(body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    public String user_access;

    public Body(String user_access) {
      this.user_access = user_access;
    }
  }
}
