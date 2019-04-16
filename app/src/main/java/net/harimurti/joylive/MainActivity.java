package net.harimurti.joylive;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.harimurti.joylive.Classes.AsyncSleep;
import net.harimurti.joylive.Classes.ListChecker;
import net.harimurti.joylive.Rest.RestClient;
import net.harimurti.joylive.Classes.Menu;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.JsonData.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<User> users = new ArrayList<>();
    private MainAdapter mainAdapter;
    private RestClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        Activity activity = this;
        new AuthDialog().execute(this);
        mainAdapter = new MainAdapter(this, users);

        final ListView listView = findViewById(R.id.list_content);
        listView.setAdapter(mainAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int amountVisible, int totalItems) {
                //leave me alone
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                        listView.getFooterViewsCount()) >= (mainAdapter.getCount() - 1)) {

                    //listView has hit the bottom
                    if (!RestClient.isLoggedIn) return;
                    client.getRooms(false);
                }
            }
        });

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ResetList();
            }
        });

        RelativeLayout welcome = findViewById(R.id.layout_welcome);

        client = new RestClient();
        client.setOnMyInfoListener(new RestClient.OnMyInfoListener() {
            @Override
            public void onProgress(String status) {
                Notification.Toast(status);
            }

            @Override
            public void onLoginRequired() {
                client.login();
            }

            @Override
            public void onError(String status) {
                Notification.Toast(status);
                new AsyncSleep().setListener(new AsyncSleep.Listener() {
                    @Override
                    public void onFinish() {
                        client.getMyInfo();
                    }
                }).start(5);
            }

            @Override
            public void onSuccess() {
                client.getRooms(true);
            }
        });
        client.setOnRoomsListener(new RestClient.OnRoomsListener() {
            @Override
            public void onError() {
                Notification.Toast(getString(R.string.something_wrong));
            }

            @Override
            public void onLoginRequired() {
                client.login();
            }

            @Override
            public void onSuccess(User[] result) {
                if (result.length == 0) {
                    Log.w("NewUser", "Result is null");
                    Notification.Toast(getString(R.string.something_wrong));
                    return;
                }

                int count = 0;
                for (User user: result) {
                    // skip male, show female & unknown
                    if (user.sex.equals("1")) continue;

                    if (!new ListChecker(users).isContain(user)) {
                        users.add(user);
                        count++;
                    }
                }

                Log.d("NewUser", "Added " + count + " from " + result.length);
                Notification.Toast(count > 0 ? "Found " + count + " new users" : "Nothing new");

                final boolean hideWelcome = (count > 0);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        if (hideWelcome) welcome.setVisibility(View.GONE);
                    }
                });
            }
        });
        // start get my info
        client.getMyInfo();
    }

    private void ResetList() {
        if (!RestClient.isLoggedIn) return;

        users.clear();
        client.getRooms(true);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
                break;

            case R.id.action_reset:
                ResetList();
                break;

            case R.id.action_about:
                Menu.About(this);
                break;

            case R.id.action_exit:
                Menu.Exit(this);
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Menu.Exit(this);
        }
        else {
            Notification.Toast(getString(R.string.press_back_twice));
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
