package JavaUtils.Android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;

@SuppressLint("ValidFragment")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Popup extends DialogFragment {

    static int POSITIVE = 1;
    static int NEUTRAL = 0;
    static int NEGATIVE = -1;
    String title, positive, neutral, negative, message;
    int clicked = -2;

    public Popup(String title, String message, String positive, String neutral, String negative) {
        this.title = title;
        this.positive = positive;
        this.neutral = neutral;
        this.negative = negative;
        this.message = message;
    }

    public int getClicked() {
        while (clicked == -2) {
            try {
                Thread.sleep(20L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return clicked;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Create the AlertDialog object and return it
        builder = builder.setTitle(title);
        builder = builder.setMessage(message);
        if (positive != null) builder = builder.setPositiveButton(positive, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                clicked = POSITIVE;
            }

        });

        if (negative != null) builder = builder.setNegativeButton(negative, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                clicked = NEGATIVE;
            }

        });

        if (neutral != null) builder = builder.setNegativeButton(neutral, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                clicked = NEUTRAL;
            }

        });
        return builder.create();
    }

}
