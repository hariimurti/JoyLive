package net.harimurti.joylive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import net.harimurti.joylive.Api.JoyLive;
import net.harimurti.joylive.Classes.Menu;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Api.JoyUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce;
    private SwipeRefreshLayout swipeRefresh;
    private static ArrayList<JoyUser> listUser = new ArrayList<>();
    private static MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        mainAdapter = new MainAdapter(this, listUser);
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
                    JoyLive.GetMoreUsers();
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
                listUser.clear();
                JoyLive.GetUsers(1);
            }
        });

        IntentFilter filter = new IntentFilter("RefreshAdapter");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(RefreshReceiver, filter);

        //load users for first time
        JoyLive.GetUsers(1);
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
            Notification.Toast("Aduh, kok udahan si om??\nPencet sekali lagi dong ahh..");
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static boolean AddUser(JoyUser user) {
        if (!JoyUser.isContainInList(listUser, user)) {
            listUser.add(user);
            return true;
        }
        return false;
    }

    private final BroadcastReceiver RefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainAdapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        }
    };
}
