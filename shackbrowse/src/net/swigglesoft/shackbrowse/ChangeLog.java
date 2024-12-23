package net.swigglesoft.shackbrowse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

@SuppressWarnings("JavadocReference")
public class ChangeLog {

    private final Context context;
    private String lastVersion, thisVersion;

    // this is API Level 3 code
    // if you use higher APIs anyway, you can use the field SDK_INT instead
    private final static int API_LEVEL = Integer.parseInt(Build.VERSION.SDK);

    // this is the key for storing the version name in SharedPreferences
    private static final String VERSION_KEY = "PREFS_VERSION_KEY";

    private static final String NO_VERSION = "";

    /**
     * Constructor
     * <p>
     * Retrieves the version names and stores the new version name in
     * SharedPreferences
     *
     * @param context
     */
    public ChangeLog(Context context) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Constructor
     * <p>
     * Retrieves the version names and stores the new version name in
     * SharedPreferences
     *
     * @param context
     * @param sp      the shared preferences to store the last version name into
     */
    public ChangeLog(Context context, SharedPreferences sp) {
        this.context = context;

        // get version numbers
        Log.d(TAG, "lastVersion: " + lastVersion);
        try {
            this.thisVersion = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            this.thisVersion = NO_VERSION;
            Log.e(TAG, "could not get version name from manifest!");
            e.printStackTrace();
        }
        this.lastVersion = sp.getString(VERSION_KEY, NO_VERSION);
        if (this.lastVersion.equals(NO_VERSION)) {
            updateVersionInPreferences();
            this.lastVersion = this.thisVersion;
        }
        Log.d(TAG, "appVersion: " + this.thisVersion);
    }

    /**
     * @return The version name of the last installation of this app (as
     * described in the former manifest). This will be the same as
     * returned by <code>getThisVersion()</code> the second time this
     * version of the app is launched (more precisely: the second time
     * ChangeLog is instantiated).
     * @see AndroidManifest.xml#android:versionName
     */
    public String getLastVersion() {
        return this.lastVersion;
    }

    /**
     * @return The version name of this app as described in the manifest.
     * @see AndroidManifest.xml#android:versionName
     */
    public String getThisVersion() {
        return this.thisVersion;
    }

    /**
     * @return <code>true</code> if this version of your app is started the
     * first time
     */
    public boolean firstRun() {
        return !this.lastVersion.equals(this.thisVersion);
    }

    /**
     * @return <code>true</code> if your app including ChangeLog is started the
     * first time ever. Also <code>true</code> if your app was
     * deinstalled and installed again.
     */
    public boolean firstRunEver() {
        return NO_VERSION.equals(this.lastVersion);
    }

    /**
     * @return An AlertDialog displaying the changes since the previous
     * installed version of your app (what's new). But when this is the
     * first run of your app including ChangeLog then the full log
     * dialog is show.
     */
    public AlertDialog getLogDialog() {
        return this.getDialog(this.firstRunEver());
    }

    /**
     * @return an AlertDialog with a full change log displayed
     */
    public AlertDialog getFullLogDialog() {
        return this.getDialog(true);
    }

    private AlertDialog getDialog(boolean full) {
        WebView wv = new WebView(this.context);

        if (API_LEVEL >= Build.VERSION_CODES.HONEYCOMB)
            Compatibility.setViewLayerTypeSoftware(wv);

        wv.setBackgroundColor(0); // transparent
        wv.loadDataWithBaseURL(null, this.getLog(full), "text/html", "UTF-8",
                null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(
                        context.getResources().getString(
                                full ? R.string.changelog_full_title
                                        : R.string.changelog_title))
                .setView(wv)
                .setCancelable(false)
                // OK button
                .setPositiveButton(
                        context.getResources().getString(
                                R.string.changelog_ok_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                updateVersionInPreferences();
                                if (mCLCL != null)
                                    mCLCL.onClose();
                            }
                        });

                /*if (!full) {
                        // "more ..." button
                        builder.setNegativeButton(R.string.changelog_show_full,
                                        new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                        getFullLogDialog().show();
                                                }
                                        });
                }*/
        builder.setNeutralButton("App Info",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateVersionInPreferences();
                        startAppInfo();
                        if (mCLCL != null)
                            mCLCL.onClose();
                    }
                });
        return builder.create();
    }

    protected void startAppInfo() {
        Intent i = new Intent(context, DonateActivity.class);
        if (context instanceof MainActivity)
            ((MainActivity) context).startActivityForResult(i, ThreadListFragment.OPEN_PREFS);
            /*
        	Intent i = new Intent(context, DonateActivity.class);
            context.startActivity(i); */
    }

    private void updateVersionInPreferences() {
        // save new version number to preferences
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(VERSION_KEY, thisVersion);
        // // on SDK-Versions > 9 you should use this:
        // if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
        // editor.commit();
        // } else {
        // editor.apply();
        // }
        editor.apply();
    }

    /**
     * @return HTML displaying the changes since the previous installed version
     * of your app (what's new)
     */
    public String getLog() {
        return this.getLog(false);
    }

    /**
     * @return HTML which displays full change log
     */
    public String getFullLog() {
        return this.getLog(true);
    }

    /**
     * modes for HTML-Lists (bullet, numbered)
     */
    private enum Listmode {
        NONE, ORDERED, UNORDERED,
    }

    ;

    private Listmode listMode = Listmode.NONE;
    private StringBuffer sb = null;
    private onChangeLogCloseListener mCLCL;
    private static final String EOCL = "END_OF_CHANGE_LOG";

    private String getLog(boolean full) {
        // read changelog.txt file
        sb = new StringBuffer();
        try {
            InputStream ins = context.getResources().openRawResource(
                    R.raw.changelog);
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));

            String line = null;
            boolean advanceToEOVS = false; // if true: ignore further version
            // sections
            while ((line = br.readLine()) != null) {
                line = line.trim();
                char marker = line.length() > 0 ? line.charAt(0) : 0;
                if (marker == '$') {
                    // begin of a version section
                    this.closeList();
                    String version = line.substring(1).trim();
                    // stop output?
                    if (!full) {
                        if (this.lastVersion.equals(version)) {
                            advanceToEOVS = true;
                        } else if (version.equals(EOCL)) {
                            advanceToEOVS = false;
                        }
                    }
                } else if (!advanceToEOVS) {
                    switch (marker) {
                        case '%':
                            // line contains version title
                            this.closeList();
                            sb.append("<div class='title'>"
                                    + line.substring(1).trim() + "</div>\n");
                            break;
                        case '_':
                            // line contains version title
                            this.closeList();
                            sb.append("<div class='subtitle'>"
                                    + line.substring(1).trim() + "</div>\n");
                            break;
                        case '!':
                            // line contains free text
                            this.closeList();
                            sb.append("<div class='freetext'>"
                                    + line.substring(1).trim() + "</div>\n");
                            break;
                        case '#':
                            // line contains numbered list item
                            this.openList(Listmode.ORDERED);
                            sb.append("<li>" + line.substring(1).trim() + "</li>\n");
                            break;
                        case '*':
                            // line contains bullet list item
                            this.openList(Listmode.UNORDERED);
                            sb.append("<li>" + line.substring(1).trim() + "</li>\n");
                            break;
                        default:
                            // no special character: just use line as is
                            this.closeList();
                            sb.append(line + "\n");
                    }
                }
            }
            this.closeList();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private void openList(Listmode listMode) {
        if (this.listMode != listMode) {
            closeList();
            if (listMode == Listmode.ORDERED) {
                sb.append("<div class='list'><ol>\n");
            } else if (listMode == Listmode.UNORDERED) {
                sb.append("<div class='list'><ul>\n");
            }
            this.listMode = listMode;
        }
    }

    private void closeList() {
        if (this.listMode == Listmode.ORDERED) {
            sb.append("</ol></div>\n");
        } else if (this.listMode == Listmode.UNORDERED) {
            sb.append("</ul></div>\n");
        }
        this.listMode = Listmode.NONE;
    }

    private static final String TAG = "ChangeLog";

    /**
     * manually set the last version name - for testing purposes only
     *
     * @param lastVersion
     */
    void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    /**
     * Methods introduced in API-Levels > 3
     */
    @SuppressLint("NewApi")
    static class Compatibility {
        /**
         * due to an error in android itself, do this to
         * prevent the background to be transparant and
         * therefore white
         * <p>
         * API Level 11
         *
         * @see http://code.google.com/p/android-change-log/issues/detail?id=17
         */
        static void setViewLayerTypeSoftware(View v) {
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setChangeLogCloseListener(onChangeLogCloseListener clcl) {
        mCLCL = clcl;
    }

    public interface onChangeLogCloseListener {
        public void onClose();
    }
}