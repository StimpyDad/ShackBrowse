package net.swigglesoft.shackbrowse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginForm {
    public MaterialDialog _progressDialog;
    protected String _userName = null;
    protected String _password = null;
    private Activity _context;
    private SharedPreferences _prefs;
    private net.swigglesoft.shackbrowse.LoginForm.OnVerifiedListener _OnVerifiedListener;
    // enter login info
    private boolean _verified = false;
    private boolean _quiet = false;
    private NetworkNotificationServers _GCMAccess;

    public LoginForm(Activity context) {
        this(context, false);
    }

    public LoginForm(Activity context, boolean disableAuto) {
        _context = context;
        _prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        _userName = _prefs.getString("userName", "");
        _password = _prefs.getString("password", "");
        _verified = _prefs.getBoolean("usernameVerified", false);
        if ((!_verified) && (!disableAuto)) {
            if (!_userName.equals("")) {
                _progressDialog = MaterialProgressDialog.show(_context, "Verifying Credentials", "This should only happen once if successful.");
                _quiet = true;
                new CredentialsVerifyTask().execute();
            } else
                show();
        } else
            show();
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setTitle("Enter Credentials");
        final View view = _context.getLayoutInflater().inflate(R.layout.dialog_login, null);
        final EditText usern = (EditText) view.findViewById(R.id.loginUsername);
        final EditText passw = (EditText) view.findViewById(R.id.loginPassword);
        final TextView header = (TextView) view.findViewById(R.id.loginHeader);
        header.setText("If you do not have an account, sign-up first at Shacknews.com");
        usern.setText(_userName);
        passw.setText(_password);
        builder.setView(view);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                _progressDialog = MaterialProgressDialog.show(_context, "Verifying", "Attempting to log in...");
                _userName = usern.getText().toString().trim();
                _password = passw.getText().toString();
                new CredentialsVerifyTask().execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (_OnVerifiedListener != null)
                    _OnVerifiedListener.onFailure();
            }
        });
        AlertDialog d = builder.create();
        d.show();
    }

    public interface OnVerifiedListener {
        public void onSuccess();

        public void onFailure();
    }

    public LoginForm setOnVerifiedListener(OnVerifiedListener listener) {
        _OnVerifiedListener = listener;
        return this;
    }

    class CredentialsVerifyTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                boolean result = ShackApi.testLogin(_context, _userName, _password);
                return result;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            _progressDialog.dismiss();

            final Boolean res = result;

            if (res) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("usernameVerified", true);
                editor.putString("userName", _userName);
                editor.putString("password", _password);
                editor.commit();

                try {
                    final JSONObject vchk = new JSONObject(prefs.getString("versioncheck", "{\"b\":[\"\"],\"ver\":[\"5.5.1\"],\"mode\":\"u\",\"msg\":\"\",\"pkg\":\"\"}"));
                    final JSONArray blist = vchk.getJSONArray("b");

                    // check for blist
                    for (int i = 0; i < blist.length(); i++) {
                        if (((MainActivity) _context).getCloudUsername().equalsIgnoreCase(blist.getString(i)))
                            _context.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!_prefs.contains("noteEnabled")) {

                    NetworkNotificationServers.OnGCMInteractListener GCMlistener = new NetworkNotificationServers.OnGCMInteractListener() {
                        @Override
                        public void networkResult(String res) {
                            // this allows the check mark to be placed when push notifications are automatically enabled if the setting has never been touched
                            SharedPreferences.Editor edit = _prefs.edit();
                            if (res.contains("ok")) {
                                edit.putBoolean("noteEnabled", true);
                                System.out.println("PUSHREG: registered");
                            }
                            edit.commit();
                        }
                    };
                    _GCMAccess = new NetworkNotificationServers(_context, GCMlistener);
                    _GCMAccess.doRegisterTask("reg");
                }

            }
            if ((!_quiet) || (!res)) {
                _context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                        builder.setTitle("Verify Result");
                        if (res) {
                            builder.setMessage("Credentials verified. Username specific features enabled.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (_OnVerifiedListener != null)
                                        _OnVerifiedListener.onSuccess();
                                }
                            });
                        } else {
                            builder.setMessage("Login failed. Your credentials may not be set correctly.");
                            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor editor = _prefs.edit();
                                    editor.putBoolean("usernameVerified", false);
                                    editor.commit();
                                    if (_OnVerifiedListener != null)
                                        _OnVerifiedListener.onFailure();
                                }
                            });
                        }
                        builder.create().show();
                    }
                });
            } else if (res) {
                if (_OnVerifiedListener != null)
                    _OnVerifiedListener.onSuccess();
            }


            _quiet = false;
        }
    }
}
