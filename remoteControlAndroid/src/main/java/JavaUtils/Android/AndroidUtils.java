package JavaUtils.Android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

public class AndroidUtils {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Popup showPopup(Activity a, String title, String message, String positive, String neutral, String negative) {
        AndroidCheck ac = AndroidCheck.getInstance(a);
        if (ac.checkVersion(Build.VERSION_CODES.HONEYCOMB)) {
            Popup p = new Popup(title, message, positive, neutral, negative);
            p.show(a.getFragmentManager(), "");
            return p;
        } else {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static InputPopup showStringInputPopup(Activity a, String title, String message, String proposal, String neutralButton) {
        AndroidCheck ac = AndroidCheck.getInstance(a);
        if (ac.checkVersion(Build.VERSION_CODES.HONEYCOMB)) {
            InputPopup p = new InputPopup(title, message, proposal, neutralButton);
            p.show(a.getFragmentManager(), "");
            return p;
        } else {
            return null;
        }
    }

}
