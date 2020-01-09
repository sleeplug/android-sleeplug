package es.uvigo.teleco.sleeplug.contract;

import android.provider.BaseColumns;

public final class SettingsContract {

    private SettingsContract() {}

    public static class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "settings";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ENGINE_SPEED = "engineSpeed";
        public static final String COLUMN_ENGINE_TIME = "engineTime";
        public static final String COLUMN_NOTIFICATIONS = "notifications";
    }
}
