package JavaUtils.Android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import de.pro_open.remotecontrol.R;

@SuppressLint("ValidFragment")
public class InputPopup extends DialogFragment {

    String title, message, hint, button;
    EditText inputField;

    public InputPopup(String title, String message, String hint, String button) {
        this.title = title;
        this.message = message;
        this.hint = hint;
        this.button = button;
    }

    public String getInput() {
        while (this.isVisible() || inputField == null) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Editable e = inputField.getText();
        String input = e.toString();
        return input;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Create the AlertDialog object and return it
        builder = builder.setTitle(title);
        builder = builder.setMessage(message);
        View v = View.inflate(getActivity(), R.layout.input_popup, null);
        builder.setView(v);
        builder.setNeutralButton(button, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        Dialog d = builder.create();
        ((inputField = (EditText) v.findViewById(R.id.inputField))).setHint(hint);
        return d;
    }
}
