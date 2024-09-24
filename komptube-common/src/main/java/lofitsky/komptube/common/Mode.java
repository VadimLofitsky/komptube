package lofitsky.komptube.common;

public enum Mode {
    SHARE_TO_KOMP,
    SHARE_TO_PHONE,
    KOMP_WEBSERVER_HEALTH_CHECK,
    PHONE_WEBSERVER_HEALTH_CHECK,
    HEALTH_CHECK;

    public static Mode valueOfOrNull(String value) {
        for(Mode mode : values()) {
            if(mode.toString().equals(value)) {
                return mode;
            }
        }
        return null;
    }
}
