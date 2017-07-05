package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 05/07/2017.
 */

public class TimelineStatsPost implements Post {
  private final String dummyCardId;
  private final long followers;
  private final long following;
  private final CardType cardType;

  public TimelineStatsPost(String dummyCardId, long followers, long following, CardType cardType) {
    this.dummyCardId = dummyCardId;
    this.followers = followers;
    this.following = following;
    this.cardType = cardType;
  }

  public long getFollowers() {
    return followers;
  }

  public long getFollowing() {
    return following;
  }

  @Override public String getCardId() {
    return dummyCardId;
  }

  @Override public CardType getType() {
    return cardType;
  }
}
