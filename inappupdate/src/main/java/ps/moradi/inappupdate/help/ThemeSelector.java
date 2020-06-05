package ps.moradi.inappupdate.help;

import android.content.Context;

import ps.moradi.inappupdate.R;

public class ThemeSelector {

    public static final String PURBLE = "purple";
    public static final String GREEN = "green";
    public static final String DARK = "dark";
    public static final String RED = "red";
    public static final String BLUE = "blue";
    public static final String PINK = "pink";
    public static final String YELLOW = "yellow";

    public void chooseTheme(Context context) {

        context.setTheme(R.style.InAppUpdateTheme);
    }
}
