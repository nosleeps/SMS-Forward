package roxma.org.sms_forward;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private SmsObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String number = getSharedPreferences("data", Context.MODE_PRIVATE).getString("number", "");
        Log.d("log", "number: " + number);
        EditText editText = (EditText) findViewById(R.id.edit_phone_number);
        editText.setText(number, TextView.BufferType.EDITABLE);
//        smsObserver = new SmsObserver(this, null);
//        getContentResolver().registerContentObserver(SMS_INBOX, true,
//                smsObserver);
    }

    public void savePhoneNumber(View v) {
        EditText editText = (EditText) findViewById(R.id.edit_phone_number);
        String number = editText.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("number", number);
        editor.commit();

    }


    public void getSmsFromPhone() {
        String sendnumber = getSharedPreferences("data", Context.MODE_PRIVATE).getString("number", "");
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"address", "body", "type", "person"};
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToFirst()) {
            String number = cur.getString(0);
            String body = cur.getString(1);
            int type = cur.getInt(2);
            String person = cur.getString(3);
            String message_content = "[" + number + "]" + body;
            if (type == 1) {
                Log.i("Message", number + ":" + body);
//                SmsManager.getDefault().sendTextMessage(sendnumber, null, message_content, null, null);
            }

        }
    }

    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getSmsFromPhone();
        }
    }

    public class TransmitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;
            if (null != bundle) {
                getSmsFromPhone();
            }
        }



    }
}
