package ps.moradi.inappupdate.help;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class LocaleManager extends ContextWrapper {

    private static String languageCode;

    public LocaleManager(Context context, String language) {
        super(context);

        if (language == null) {
            languageCode = SharedPrefrence.getString(context, SharedKeys.LANGUAGE, "fa");
        }

        changeLocale(context);
    }

    public static LocaleManager wrap(Context context) {

        LocaleManager localeManage = new LocaleManager(context, null);

        return localeManage;
    }

    public static LocaleManager wrap(Context context, String language) {

        LocaleManager localeManage = new LocaleManager(context, language);

        return localeManage;
    }

    public static Context changeLocale(Context context) {

        Locale newLocale = new Locale(languageCode);
        if (languageCode.equals("fa")) {
            newLocale = new Locale(languageCode, "IR");
        }

        Locale.setDefault(newLocale);

        Resources res = context.getResources();
        Configuration configuration = new Configuration(res.getConfiguration());
        DisplayMetrics dm = res.getDisplayMetrics();

        configuration.locale = newLocale;
        res.updateConfiguration(configuration, dm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            configuration.locale = newLocale;
            configuration.setLocale(newLocale);
            configuration.setLayoutDirection(newLocale);
            res.updateConfiguration(configuration, dm);
            context = context.createConfigurationContext(configuration);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Set<Locale> set = new LinkedHashSet<>();
            // bring the target locale to the front of the list
            set.add(newLocale);

            LocaleList all = LocaleList.getDefault();
            for (int i = 0; i < all.size(); i++) {
                // append other locales supported by the user
                set.add(all.get(i));
            }

            Locale[] locales = set.toArray(new Locale[0]);
            configuration.setLocales(new LocaleList(locales));
            res.updateConfiguration(configuration, dm);
            context = context.createConfigurationContext(configuration);

        }

        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.wrap(base));
    }

}
