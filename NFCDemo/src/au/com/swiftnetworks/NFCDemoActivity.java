package au.com.swiftnetworks;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NFCDemoActivity extends Activity {
    private TextView mText;
    private int mCount = 0;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.foreground_dispatch);
        mText = (TextView) findViewById(R.id.text);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
        	NdefMessage[] msgs=getNdefMessages(intent);
        	if(msgs!=null){
        	Log.e("NFCDemo", "msg length " + msgs.length);
        	  for(int i=0;i<msgs.length;i++){
        		  displayMessage(msgs[i]);
        		  try {
					String hexString=getHexString(msgs[i].toByteArray());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	  }
        	}
         
            mText.setText("Discovered tag " + ++mCount + " with intent: " + intent);
        } else {
            mText.setText("Scan a tag");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        mText.setText("Discovered tag " + ++mCount + " with intent: " + intent);
        NdefMessage[] msgs=getNdefMessages(intent);
    	if(msgs!=null){
    	Log.e("NFCDemo", "msg length " + msgs.length);
    	  for(int i=0;i<msgs.length;i++){
    		  displayMessage(msgs[i]);
    		  NdefRecord[] records=msgs[i].getRecords();
    		  if(records!=null){
    			  Log.e("NFCDemo", "record length  " + records.length +" for message:"+i);
    		  }
    	  }
    	}
    }
    
    
    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            else {
            // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
        }        
        else {
            Log.e("NFCDemo", "Unknown intent " + intent);
            finish();
        }
        return msgs;
    }
    
    
    void displayMessage(NdefMessage message){
    	if(message!=null){
    		byte [] buffer=message.toByteArray();
    		try{
    		String hex=getHexString(buffer);
    		 Log.e("NFCDemo raw", hex);
    		}catch(Exception e){
    			
    		}
    		
    	}
    	
    }
    
    public static String getHexString(byte[] b) throws Exception {
    	  String result = "";
    	  for (int i=0; i < b.length; i++) {
    	    result +=
    	          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
    	  }
    	  return result;
    	}

}
