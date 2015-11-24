package squemasports.interdisciplinar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import squemasports.interdisciplinar.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    private EditText mLoginView;
    private EditText mPasswordView;
    private EditText mServerView;
    private Button mBtnLogin;

    private View mProgressView;
    private View mEmailLoginFormView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginView = (EditText) findViewById(R.id.login);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });

        mServerView = (EditText) findViewById(R.id.server);
        mServerView.setText(HttpRequest.getServer());

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.scroll_login_form);
        mEmailLoginFormView = findViewById(R.id.login_form);
    }

    public void login() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();
        HttpRequest.setServer(mServerView.getText().toString());

        showProgress(true);
        mAuthTask = new UserLoginTask(login, password);
        mAuthTask.execute((Void) null);

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLogin;
        private final String mPassword;
        private Usuario usuario;
        private String message;

        UserLoginTask(String login, String password) {
            mLogin = login;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("login", mLogin);
                jsonObject.put("senha", mPassword);


                String json = HttpRequest.postJson("login", jsonObject.toString());

                jsonObject = new JSONObject(json);
                String status = jsonObject.getString("status");
                System.out.println("STATUS: " + status + ". OK? " + ("OK".equals(status)));
                if ("OK".equals(status)) {
                    String usrString = jsonObject.getString("jsonentity");
                    JSONObject usrJson = new JSONObject(usrString);

                    usuario = new Usuario();
                    usuario.setId(usrJson.getString("_id"));
                    usuario.setNome(usrJson.getString("nome"));
                    usuario.setLogin(usrJson.getString("login"));

                    return true;

                } else {
                    throw new Exception("Falha no login");
                }

            } catch (Exception ex) {

                System.out.println("Exception: " + ex);
                System.out.println(ex.getMessage());
                System.out.println(ex);

                message = ex.getMessage();

                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            System.out.println("SUCESSO? " + success);
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //startActivity(new Intent(LoginActivity.this, MenuActivity.class));

                System.out.println("Vai pra outra tela");
                System.out.println("Usuário: " + usuario.getNome());
                finish();
            } else {
                if (message != null) {
                    mPasswordView.setError("ERRO: " + message);
                } else {
                    mPasswordView.setError(getString(R.string.error_login));
                }
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
