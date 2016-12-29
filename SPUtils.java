import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    
    private SPUtils(){
    }
    
    private static final String FILE_NAME = "config_data";
    
    public static void put(String key, Object object) {
        SharedPreferences sp = getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }
    
    public static SharedPreferences getSharedPreferences() {
        return SenseMeetWallApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }
    
    public static String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }
    
    public static int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }
    
    public static float getFloat(String key, float defValue) {
        return getSharedPreferences().getFloat(key, defValue);
    }
    
    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }
    
}
