package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;

/**
 * Created by neuro on 27-01-2017.
 */
public class ShareAppsFileServerSocket extends AptoideFileServerSocket<AndroidAppInfo> {

  public ShareAppsFileServerSocket(int port, AndroidAppInfo androidAppInfo, int timeout) {
    super(port, androidAppInfo.getFilesPathsList(), timeout);
  }

  public ShareAppsFileServerSocket(int bufferSize, int port, AndroidAppInfo androidAppInfo,
      int timeout) {
    super(bufferSize, port, androidAppInfo.getFilesPathsList(), timeout);
  }
}
