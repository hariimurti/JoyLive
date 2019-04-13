package net.harimurti.joylive;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.harimurti.joylive.Classes.Notification;
import net.harimurti.joylive.Classes.Preferences;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AuthDialog extends AsyncTask<Activity, Void, Boolean> {
    private static boolean onPostExecute;
    private Activity activity;
    private Preferences pref = new Preferences();

    @Override
    protected Boolean doInBackground(Activity... params) {
        activity = params[0];
        return pref.isRegistered();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) return;
        if (onPostExecute) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_auth, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        onPostExecute = true;
        String serial = App.getSerialNumber();

        EditText inputKey = dialogView.findViewById(R.id.inputKey);
        EditText inputSerial = dialogView.findViewById(R.id.inputSerial);
        inputSerial.setText(serial);
        inputSerial.setInputType(InputType.TYPE_NULL);
        inputSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("Serial", serial));
                Notification.Toast("Copied into clipboard");
            }
        });

        dialog.setNeutralButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String key = inputKey.getText().toString();
                if (!pref.Register(key)) {
                    Notification.Toast("Key is invalid!");
                    activity.finish();
                }
            }
        });

        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });

        dialog.show();
    }
}
