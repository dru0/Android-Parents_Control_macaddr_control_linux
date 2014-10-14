package kj125.wwabiszczewicz.parentscontrol;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import com.sshtools.j2ssh.*;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

import java.io.IOException;


public class Ustawienia extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustawienia);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button ButtonAppy = (Button) findViewById(R.id.button);
        final EditText txtHost = (EditText) findViewById(R.id.editText);
        final Switch TB1 = (Switch) findViewById(R.id.switch1);

        ButtonAppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        SshClient ssh = new SshClient();
                        try {
                            ssh.connect(txtHost.getText().toString(), new IgnoreHostKeyVerification());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            PasswordAuthenticationClient sshpass = new PasswordAuthenticationClient();
                            sshpass.setUsername("---");
                            sshpass.setPassword("----");
                            ssh.authenticate(sshpass);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            final CheckBox chk1 = (CheckBox) findViewById(R.id.checkBox);
                            final CheckBox chk2 = (CheckBox) findViewById(R.id.checkBox2);
                            final CheckBox chk3 = (CheckBox) findViewById(R.id.checkBox3);
                            final CheckBox chk4 = (CheckBox) findViewById(R.id.checkBox4);
                            final CheckBox chk5 = (CheckBox) findViewById(R.id.checkBox5);
                            String[] dtables = {"iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source 90:E6:BA:DE:E3:AE -j REJECT\n
                                iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source B8:76:3F:9F:D7:21 -j REJECT\n
                                iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source D0:51:62:2B:D4:CD -j REJECT\n
                                iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source d0:51:62:2b:d4:cd -j REJECT\n"};
                            
                            

                            SessionChannelClient session = ssh.openSessionChannel();
                            session.startShell();
                            if (TB1.isChecked()) {
                                session.getOutputStream().write("iptables -t nat -A PREROUTING -i br-lan ! -s 192.168.76.1 -p tcp --dport 80 -j DNAT --to-destination 192.168.76.1:8088".getBytes());
                            }
                            //session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source 90:E6:BA:DE:E3:AE -j REJECT\n".getBytes());
                            //session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source B8:76:3F:9F:D7:21 -j REJECT\n".getBytes());
                            //session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source D0:51:62:2B:D4:CD -j REJECT\n".getBytes());
                            //session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source d0:51:62:2b:d4:cd -j REJECT\n".getBytes());
                            if (chk1.isChecked()) {
                                session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source 90:E6:BA:DE:E3:AE -j REJECT\n".getBytes());
                                session.getOutputStream().write("iptables -I INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source 90:E6:BA:DE:E3:AE -j REJECT\n".getBytes());
                            }
                            if (chk2.isChecked()) {
                                session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source 00:21:6B:3B:16:D2 -j REJECT\n".getBytes());
                                session.getOutputStream().write("iptables -I INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source 00:21:6B:3B:16:D2 -j REJECT\n".getBytes());
                            }
                            if (chk3.isChecked()) {
                                session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source B8:76:3F:9F:D7:21 -j REJECT\n".getBytes());
                                session.getOutputStream().write("iptables -I INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source B8:76:3F:9F:D7:21 -j REJECT\n".getBytes());
                            }

                            if (chk4.isChecked()) {
                                session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source D0:51:62:2B:D4:CD -j REJECT\n".getBytes());
                                session.getOutputStream().write("iptables -I INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source D0:51:62:2B:D4:CD -j REJECT\n".getBytes());
                            }

                            if (chk5.isChecked()) {
                                session.getOutputStream().write("iptables -D INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source d0:51:62:2b:d4:cd -j REJECT\n".getBytes());
                                session.getOutputStream().write("iptables -I INPUT -m state --state NEW,ESTABLISHED,RELATED -m mac --mac-source d0:51:62:2b:d4:cd -j REJECT\n".getBytes());
                            }
                            session.close();
                            ssh.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    }).start();
            }
        });
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
