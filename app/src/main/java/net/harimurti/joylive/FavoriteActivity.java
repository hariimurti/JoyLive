package net.harimurti.joylive;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import net.harimurti.joylive.Api.JoyUser;
import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Classes.Preferences;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    private ArrayList<JoyUser> listUser = new ArrayList<>();
    private FavoriteAdapter favoriteAdapter;
    private Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setTitle(R.string.app_favorite);

        pref = new Preferences();

        favoriteAdapter = new FavoriteAdapter(this, listUser);
        final ListView listView = findViewById(R.id.list_content);
        listView.setAdapter(favoriteAdapter);
        listView.setEmptyView(findViewById(R.id.empty));

        RefreshList(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshList(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favoritemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                RefreshList(true);
                Notification.Toast("Refreshed!");
                break;

            case R.id.action_about:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Haii, Haloooooo\n\n" +
                        "Aplikasi ini aku tujukan kepada om-om yg suka nonton joylive!\n" +
                        "Selamat menikmati \uD83D\uDE18\n\n" +
                        "Telegram : @paijemdev")
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

    private void RefreshList(boolean force) {
        ArrayList<JoyUser> listFav = pref.getFavorite();

        if (!force) {
            if (listFav == null) return;
            if (listUser.size() == listFav.size())
                return;
        }

        listUser.clear();
        listUser.addAll(pref.getFavorite());
        favoriteAdapter.notifyDataSetChanged();
    }
}
