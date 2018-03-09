package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.fragment.FragmentView;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 03/03/2018.
 */

public class BottomNavigationFragmentView extends FragmentView
    implements AptoideBottomNavigationView {

  protected BottomNavigationView bottomNavigationView;
  private PublishSubject<Integer> navigationSubject;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    navigationSubject = PublishSubject.create();
    getFragmentChildNavigator(R.id.fragment_placeholder).navigateTo(new BottomHomeFragment(), true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      navigationSubject.onNext(item.getItemId());
      return true;
    });

    attachPresenter(new BottomNavPresenter(this));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.activity_main, container, false);
  }

  @Override public Observable<Integer> navigationEvent() {
    return navigationSubject;
  }

  @Override public void showFragment(Integer menuItemId) {
    Fragment selectedFragment = null;
    switch (menuItemId) {
      case R.id.action_home:
        selectedFragment = new BottomHomeFragment();
        break;
      case R.id.action_search:
        break;
      case R.id.action_stores:
        break;
      case R.id.action_apps:
        selectedFragment = new BottomHomeFragment();
        break;
    }
    if (selectedFragment != null) {
      FragmentNavigator fragmentChildNavigator =
          getFragmentChildNavigator(R.id.fragment_placeholder);
      fragmentChildNavigator.navigateTo(selectedFragment, true);
    }
  }
}
