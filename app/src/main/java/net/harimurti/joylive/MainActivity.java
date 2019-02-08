package net.harimurti.joylive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Classes.Preferences;
import net.harimurti.joylive.JsonData.JoyUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce;
    private SwipeRefreshLayout swipeRefresh;
    private static ArrayList<JoyUser> listUser = new ArrayList<>();
    private static ListAdapter listAdapter;
    private static Context context;
    private static Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);
        context = this;

        pref = new Preferences(this);

        listAdapter = new ListAdapter(this, listUser);
        final ListView listView = findViewById(R.id.content);
        listView.setAdapter(listAdapter);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int amountVisible, int totalItems) {
//                if (firstVisibleItem+1 + amountVisible > totalItems) {
//                    JoyLiveApi.GetMoreUsers();
//                }
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                        listView.getFooterViewsCount()) >= (listAdapter.getCount() - 1)) {

                    //listView has hit the bottom
                    JoyLiveApi.GetMoreUsers();
                }
            }
        });

        swipeRefresh = findViewById(R.id.swiperefresh);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listUser.clear();
                JoyLiveApi.GetUsers(1);
            }
        });

        IntentFilter filter = new IntentFilter("RefreshAdapter");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(RefreshReceiver, filter);

        //load users for first time
        JoyLiveApi.GetUsers(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        MenuItem item = menu.findItem(R.id.action_switch);
        View view = item.getActionView();

        Switch switcha = view.findViewById(R.id.sw_player);
        switcha.setChecked(pref.getBoolean(Preferences.KEY_3RD_PLAYER));
        switcha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean swb = switcha.isChecked();
                pref.setBoolean(Preferences.KEY_3RD_PLAYER, swb);
                Notification.Toast(swb ? "menggunakan player eksternal" : "menggunakan player internal");
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Haii, Haloooooo\n\n" +
                        "Aplikasi ini aku tujukan kepada om-om yg suka nonton joylive!\n\n" +
                        "Selamat menikmati \uD83D\uDE18")
                        .setTitle(R.string.app_name)
                        .create();
                dialog.show();
                break;

            case R.id.action_exit:
                this.finish();
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
            return;
        }

        if (!this.doubleBackToExitPressedOnce)
            Notification.Toast("Aduh, kok udahan si om??\nPencet sekali lagi dong ahh..");

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static Context getContext() {
        return context;
    }

    public static void AddUser(JoyUser user) {
        if (!listUser.contains(user))
            listUser.add(user);
    }

    private final BroadcastReceiver RefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            listAdapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        }
    };
}
