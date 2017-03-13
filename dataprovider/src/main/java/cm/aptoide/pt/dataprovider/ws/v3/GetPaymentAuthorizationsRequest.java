/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 05/01/2017.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class GetPaymentAuthorizationsRequest extends V3<PaymentAuthorizationsResponse> {

  private GetPaymentAuthorizationsRequest(BaseBody baseBody) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter());
  }

  public static GetPaymentAuthorizationsRequest of(String accessToken) {
    BaseBody args = new BaseBody();
    args.put("access_token", accessToken);
    return new GetPaymentAuthorizationsRequest(args);
  }

  @Override
  protected Observable<PaymentAuthorizationsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPaymentAuthorization(map);
  }
}
