package net.harimurti.joylive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import net.harimurti.joylive.Api.JoyUser;
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        listUser.clear();
        listUser.addAll(pref.getFavorite());
        favoriteAdapter.notifyDataSetChanged();
    }
}
