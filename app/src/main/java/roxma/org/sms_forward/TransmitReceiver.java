package roxma.org.sms_forward;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;


public class TransmitReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            getSmsFromPhone(context);
        }
    }

    public void getSmsFromPhone(Context context) {
        Uri SMS_INBOX = Uri.parse("content://sms/");
        String sendnumber = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("number", "");
        ContentResolver cr = context.getContentResolver();
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
                SmsManager.getDefault().sendTextMessage(sendnumber, null, message_content, null, null);
            }

        }
    }



}
