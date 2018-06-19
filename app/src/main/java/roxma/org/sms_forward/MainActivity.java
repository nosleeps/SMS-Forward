package roxma.org.sms_forward;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
        Log.d("log","number: " + number);
        EditText editText = (EditText) findViewById(R.id.edit_phone_number);
        editText.setText(number, TextView.BufferType.EDITABLE);
        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true,
                smsObserver);
//        getSmsFromPhone();
    }

    public void sendSMS(View v)
    {
        EditText editText = (EditText) findViewById(R.id.edit_phone_number);
        String number = editText.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("number", number);
        editor.commit();

        String message  = "This is a test message to " + number;
        Log.i("sms","message send:" + message);
//        SmsManager.getDefault().sendTextMessage(number,null,message,null,null);
//        SmsManager.getDefaultSmsSubscriptionId();
    }

    public List<?> getSubScription(){
        SubscriptionManager sm = SubscriptionManager.from(getApplicationContext());
        List<?> list = sm.getActiveSubscriptionInfoList();
        return list;
    }

    public Handler smsHandler = new Handler() {
        //这里可以进行回调的操作
        //TODO
    };

    public void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[] { "address","body","type","person" };//"_id", "address", "person",, "date", "type
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToFirst()) {
            String number = cur.getString(0);//手机号
            String body = cur.getString(1);//
            int type = cur.getInt(2);
            String person = cur.getString(3);//
            String message_content = "["+number+"]"+body;
            if(type==1){
                Log.i("Message",number+":"+body);
                SmsManager.getDefault().sendTextMessage(number,null,message_content,null,null);
            }
            //这里我是要获取自己短信服务号码中的验证码~~
//            Pattern pattern = Pattern.compile(" [a-zA-Z0-9]{10}");
//            Matcher matcher = pattern.matcher(body);
//            if (matcher.find()) {
//                String res = matcher.group().substring(1, 11);
//
//            }
        }
    }

    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，使用我们获取短消息的方法
            getSmsFromPhone();
        }
    }
    }
