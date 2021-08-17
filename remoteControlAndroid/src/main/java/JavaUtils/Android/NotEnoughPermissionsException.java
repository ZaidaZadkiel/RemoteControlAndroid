package JavaUtils.Android;

public class NotEnoughPermissionsException extends Exception {

    String perm;

    public NotEnoughPermissionsException(String permissions) {
        perm = permissions;
    }

    public String getNeededPermissions() {
        return perm;
    }

}
