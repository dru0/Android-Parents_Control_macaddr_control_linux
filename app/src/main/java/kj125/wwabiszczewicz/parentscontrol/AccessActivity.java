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
import java.io.InputStream;


public class AccessActivity extends Activity {
    int DEBUG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String[] connectState = {new String()};
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);
        final EditText txtHost = (EditText) findViewById(R.id.editText);
        Button ButtonAppy = (Button) findViewById(R.id.button);
        final Switch TB1 = (Switch) findViewById(R.id.switch1);

        final String[] MACS = {"90:E6:BA:DE:E3:AE","00:21:6B:3B:16:D2","B8:76:3F:9F:D7:21","D0:51:62:2B:D4:CD","ff:ff:ff:ff:d4:cd", "b8:27:eb:d1:c9:93"};

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
                            if(DEBUG==1)
                                System.out.print(password_prefs);
                            ssh.connect(hostname_prefs, new IgnoreHostKeyVerification());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            PasswordAuthenticationClient sshpass = new PasswordAuthenticationClient();
                            if(DEBUG==1) {
                                System.out.print(password_prefs);
                                System.out.print(hostname_prefs);
                            }
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
                            //session.startShell();
                            //session.getOutputStream().write(dtables.getBytes());
                            if (TB1.isChecked()) {
                                session.getOutputStream().write("iptables -t nat -A PREROUTING -i br-lan ! -s 192.168.76.1 -p tcp --dport 80 -j DNAT --to-destination 192.168.76.1:8088".getBytes());
                            }
                            itables = itables + iptablesRule(chk1, MACS[0]);
                            itables = itables + iptablesRule(chk2, MACS[1]);
                            itables = itables + iptablesRule(chk3, MACS[2]);
                            itables = itables + iptablesRule(chk4, MACS[3]);
                            itables = itables + iptablesRule(chk5, MACS[4]);
                            itables = itables + iptablesRule(chk7, MACS[5]);

                            //System.out.print(itables);
                            session.executeCommand(itables);
                            String mactxt = "";
                            for(String macn:MACS) {
                                mactxt+="iptables -L|grep -q "+macn+" && echo "+macn+";";
                            }
                            //System.out.print(mactxt);
                            session.executeCommand(mactxt);
                            InputStream in = session.getInputStream();
                            byte buffer[] = new byte[512];
                            int read;
                            while((read = in.read(buffer)) > 0) {
                                String out = new String(buffer, 0, read);
                                System.out.println(out);
                            }

                            in.close();
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
            tab = ("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source " + mac + " -j REJECT;iptables -I INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source " + mac + " -j REJECT;");
        else {
            tab = ("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source " + mac + " -j REJECT;");
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
        if (id == R.id.action_author) {
            Toast.makeText(getApplicationContext(), "druss0@poczta.onet.pl", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
