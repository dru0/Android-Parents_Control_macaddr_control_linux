package kj125.wwabiszczewicz.parentscontrol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.sshtools.j2ssh.*;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

import java.io.IOException;


public class AccessActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String[] connectState = {new String()};
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);
        final EditText txtHost = (EditText) findViewById(R.id.editText);
        Button ButtonAppy = (Button) findViewById(R.id.button);
        final Switch TB1 = (Switch) findViewById(R.id.switch1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        ButtonAppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String password_prefs = sharedPrefs.getString("password_text", "");
                final String hostname_prefs = sharedPrefs.getString("hostname_text", "192.168.0.1");
                final String login_prefs = sharedPrefs.getString("login_text", "root");
                new Thread(new Runnable() {
                    public void run() {
                        SshClient ssh = new SshClient();
                        try {
                            System.out.print(password_prefs);
                            ssh.connect(hostname_prefs, new IgnoreHostKeyVerification());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            PasswordAuthenticationClient sshpass = new PasswordAuthenticationClient();
                            System.out.print(password_prefs);
                            System.out.print(hostname_prefs);
                            sshpass.setUsername(login_prefs);
                            sshpass.setPassword(password_prefs);
                            int isConnected = ssh.authenticate(sshpass);
                            if (isConnected == AuthenticationProtocolState.COMPLETE)
                                connectState[0] = "Połączony z hostem. Aktywowanie zasad dostępu.";
                            else
                                connectState[0] = "Problem z połączeniem.\nSprawdz czy login i hasło są poprawne.";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            final Switch chk1 = (Switch) findViewById(R.id.checkBox);
                            final Switch chk2 = (Switch) findViewById(R.id.checkBox2);
                            final Switch chk3 = (Switch) findViewById(R.id.checkBox3);
                            final Switch chk4 = (Switch) findViewById(R.id.checkBox4);
                            final Switch chk5 = (Switch) findViewById(R.id.checkBox5);
                            final Switch chk6 = (Switch) findViewById(R.id.checkBox6);
                            final Switch chk7 = (Switch) findViewById(R.id.checkBox7);

                            String itables = "";
                            SessionChannelClient session = ssh.openSessionChannel();
                            session.startShell();
                            //session.getOutputStream().write(dtables.getBytes());
                            if (TB1.isChecked()) {
                                session.getOutputStream().write("iptables -t nat -A PREROUTING -i br-lan ! -s 192.168.76.1 -p tcp --dport 80 -j DNAT --to-destination 192.168.76.1:8088".getBytes());
                            }
                            itables = itables + iptablesRule(chk1, "90:E6:BA:DE:E3:AE");
                            itables = itables + iptablesRule(chk2, "00:21:6B:3B:16:D2");
                            itables = itables + iptablesRule(chk3, "B8:76:3F:9F:D7:21");
                            itables = itables + iptablesRule(chk4, "D0:51:62:2B:D4:CD");
                            itables = itables + iptablesRule(chk5, "d0:51:62:2b:d4:cd");
                            itables = itables + iptablesRule(chk7, "b8:27:eb:d1:c9:93");

                            System.out.print(itables);
                            session.getOutputStream().write(itables.getBytes());
                            session.close();
                            ssh.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    }).start();
                Toast.makeText(getApplicationContext(), connectState[0], Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String iptablesRule(Switch sw, String mac) {
        String tab;
        if (!sw.isChecked())
            tab = ("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source " + mac + " -j REJECT\niptables -I INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source " + mac + " -j REJECT\n");
        else {
            tab = ("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source " + mac + " -j REJECT\n");
        }
        return tab;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ustawienia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent ConfigIntent = new Intent(getApplicationContext(), ConfigActivity.class);
            startActivity(ConfigIntent);
            final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }
        return super.onOptionsItemSelected(item);
    }
}
