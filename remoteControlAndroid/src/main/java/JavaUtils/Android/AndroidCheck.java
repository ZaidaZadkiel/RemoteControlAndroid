package JavaUtils.Android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.util.ArrayList;

public class AndroidCheck {

    private static AndroidCheck check;
    private ArrayList<String> permissions = new ArrayList<String>();

    private AndroidCheck(Context c) {
        PackageManager manager = c.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(c.getPackageName(), PackageManager.GET_PERMISSIONS);
            for (String p : info.requestedPermissions) {
                System.out.println("Permission granted: " + p);
                permissions.add(p);
            }
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static AndroidCheck getInstance(Context c) {
        if (check == null) check = new AndroidCheck(c);
        return check;
    }

    public boolean hasPermissions(String... permissions) {
        for (String s : permissions) {
            if (!this.permissions.contains(s)) return false;
        }
        return true;
    }

    public boolean checkVersion(int version) {
        return version <= Build.VERSION.SDK_INT;
    }


}
