package lpetlinski.pircclient.androidclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_IP = "SERVER_IP";
    public static final String SERVER_PORT = "SERVER_PORT";
    public static final String MJPEG_SERVER_PORT = "MJPEG_SERVER_PORT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFieldValidation();
        getDefaultValues();
    }

    private void connectToServer() {
        EditText ipInput = (EditText)findViewById(R.id.ipInput);
        EditText portInput = (EditText)findViewById(R.id.portInput);
        EditText mjpegPortInput = (EditText) findViewById(R.id.mjpegPortInput);

        //noinspection ConstantConditions
        if(ipInput.getError() != null || portInput.getError() != null || mjpegPortInput.getError() != null) {
            return;
        }

        final Intent carControllerIntent = new Intent(this, PiControllerActivity.class);
        //noinspection ConstantConditions
        carControllerIntent.putExtra(SERVER_IP, ipInput.getText().toString());

        //noinspection ConstantConditions
        carControllerIntent.putExtra(SERVER_PORT, Integer.parseInt(portInput.getText().toString()));

        //noinspection ConstantConditions
        carControllerIntent.putExtra(MJPEG_SERVER_PORT, mjpegPortInput.getText().toString());

        startActivity(carControllerIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.connect) {
            connectToServer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFieldValidation() {
        EditText ipField = (EditText) findViewById(R.id.ipInput);

        //noinspection ConstantConditions
        ipField.addTextChangedListener(new TextValidator(ipField) {
            @Override
            public String validate(String text) {
                String[] split = text.split("\\.");
                boolean isError = false;
                if(split.length != 4) {
                    isError = true;
                }
                for (String aSplit : split) {
                    try {
                        Integer result = Integer.parseInt(aSplit);
                        if (result.toString().length() != aSplit.length() || result > 255 || result < 0) {
                            isError = true;
                        }
                    } catch (NumberFormatException exc) {
                        isError = true;
                    }
                }
                if(isError) {
                    return "Invalid IP format.";
                } else {
                    setServerIp(text);
                    return null;
                }
            }
        });


        EditText portField = (EditText) findViewById(R.id.portInput);
        //noinspection ConstantConditions
        portField.addTextChangedListener(new TextValidator(portField) {
            @Override
            public String validate(String text) {
                boolean isError = false;
                try {
                   Integer result = Integer.parseInt(text);
                    if(result.toString().length() != text.length() || result < 0) {
                        isError = true;
                    }
                } catch (NumberFormatException exc) {
                    isError = true;
                }
                if(isError) {
                    return "Invalid port format";
                } else {
                    setServerPort(text);
                    return null;
                }
            }
        });

        EditText mjpegPortField = (EditText) findViewById(R.id.mjpegPortInput);
        //noinspection ConstantConditions
        mjpegPortField.addTextChangedListener(new TextValidator(mjpegPortField) {
            @Override
            public String validate(String text) {
                boolean isError = false;
                try {
                    Integer result = Integer.parseInt(text);
                    if(result.toString().length() != text.length() || result < 0) {
                        isError = true;
                    }
                } catch (NumberFormatException exc) {
                    isError = true;
                }
                if(isError) {
                    return "Invalid port format";
                } else {
                    setMjpegServerPort(text);
                    return null;
                }
            }
        });
    }

    @SuppressLint("CommitPrefEdits")
    private void setMjpegServerPort(String port) {
        SharedPreferences preferences = getPreferences(0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MJPEG_SERVER_PORT, port);
        editor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    private void setServerIp(String ip) {
        SharedPreferences preferences = getPreferences(0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SERVER_IP, ip);
        editor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    private void setServerPort(String port) {
        SharedPreferences preferences = getPreferences(0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SERVER_PORT, port);
        editor.commit();
    }

    private void getDefaultValues() {
        SharedPreferences preferences = getPreferences(0);
        String ip = preferences.getString(SERVER_IP, "");
        String port = preferences.getString(SERVER_PORT, "");
        String mjpegPort = preferences.getString(MJPEG_SERVER_PORT, "");

        EditText ipField = (EditText) findViewById(R.id.ipInput);
        EditText portField = (EditText) findViewById(R.id.portInput);
        EditText mjpegPortField = (EditText) findViewById(R.id.mjpegPortInput);

        if(!ip.isEmpty()) {
            //noinspection ConstantConditions
            ipField.setText(ip);
        }
        if(!port.isEmpty()) {
            //noinspection ConstantConditions
            portField.setText(port);
        }
        if(!mjpegPort.isEmpty()) {
            //noinspection ConstantConditions
            mjpegPortField.setText(mjpegPort);
        }
    }
}
