package au.com.swiftnetworks;

import java.io.IOException;
import java.security.MessageDigest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import au.com.swiftnetworks.R;

public class MainActivity extends Activity implements OnClickListener {
	// UI Elements
	private static TextView block_0_Data;
	private static TextView block_1_Data;
	private static TextView status_Data;
	// NFC parts
	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;
	// Hex help
	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };
	// Just for alerts

	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int NETWORK = 4;
	private static final String TAG = "purchtagscanact";
	
	private byte[] keyB={(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
	
	
	
	public static void calculateMD5(){
		try{
		MessageDigest digester = MessageDigest.getInstance("MD5");
		  byte[] bytes = { (byte)0x65,(byte)0x69,(byte)0x74,(byte)0x73,(byte)0x67,(byte)0x6c,(byte)0x6f,(byte)0x62,
				  (byte)0x61,(byte)0x6c,(byte)0x22,(byte)0x01,(byte)0x10,(byte)0x90,(byte)0x10,(byte)0x25,
				  (byte)0x53,(byte)0x75,(byte)0x70,(byte)0x65,(byte)0x72,(byte)0x76,(byte)0x69,(byte)0x73,
				  (byte)0x6f,(byte)0x72,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff
				  
		  };
		  int byteCount=32;
		  digester.update(bytes, 0, byteCount);
		  byte[] digest = digester.digest();
		  
		  String md5Hash=getHexString(digest,digest.length);
		  Log.d(TAG, md5Hash);
		  
		}
		catch(Exception e){
			
		}

	}
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		block_0_Data = (TextView) findViewById(R.id.block_0_data);
		block_1_Data = (TextView) findViewById(R.id.block_1_data);
		status_Data = (TextView) findViewById(R.id.status_data);

		// Capture Purchase button from layout
		Button scanBut = (Button) findViewById(R.id.clear_but);
		// Register the onClick listener with the implementation above
		scanBut.setOnClickListener(this);

		// Register the onClick listener with the implementation above
		scanBut.setOnClickListener(this);

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		// Create a generic PendingIntent that will be deliver to this activity.
		// The NFC stack
		// will fill in the intent with the details of the discovered tag before
		// delivering to
		// this activity.
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// Setup an intent filter for all MIME based dispatches
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };

		// Setup a tech list for all NfcF tags
		mTechLists = new String[][] { new String[] { MifareClassic.class
				.getName() } };

		Intent intent = getIntent();
		resolveIntent(intent);
		//calculateMD5();

	}

	void resolveIntent1(Intent intent) {
        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;
            
            try {       //  5.1) Connect to card 
            mfc.connect();
            boolean auth = false;
            String cardData = null;
            // 5.2) and get the number of sectors this card has..and loop thru these sectors
            int secCount = mfc.getSectorCount();
            int bCount = 0;
            int bIndex = 0;
            for(int j = 0; j < secCount; j++){
                // 6.1) authenticate the sector
                auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
                if(auth){
                    // 6.2) In each sector - get the block count
                    bCount = mfc.getBlockCountInSector(j);
      
                    bIndex = mfc.sectorToBlock(j);
                    Log.i(TAG, "sector:"+j);
                    for(int i = 0; i < bCount; i++){
                        
                        // 6.3) Read the block
                        data = mfc.readBlock(bIndex);    
                        // 7) Convert the data into a string from Hex format.  
                        
                        Log.i(TAG, getHexString(data, data.length));
                        bIndex++;
                    }
                }else{ // Authentication failed - Handle it
                    
                }
            }    
        }catch (IOException e) { 
                Log.e(TAG, e.getLocalizedMessage());
                showAlert(3);
            }
    }// End of method
	}
	
	void resolveIntent(Intent intent) {
		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// status_Data.setText("Discovered tag with intent: " + intent);
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			byte[] data;
			try {
				mfc.connect();
				boolean auth = false;
				String cardData = null;
				status_Data.setText("Authenticating the Tag..");
				int sectorCount=mfc.getSectorCount();
				int blockCount=mfc.getBlockCount();
				Log.e(TAG, "sector:"+sectorCount+" block:"+blockCount);
				
				// Authenticating and reading Block 0 /Sector 1
				//auth = mfc.authenticateSectorWithKeyA(0,
				//		MifareClassic.KEY_DEFAULT);
				//auth = mfc.authenticateSectorWithKeyA(1,
				//		MifareClassic.KEY_DEFAULT);
				auth = mfc.authenticateSectorWithKeyA(2,
						MifareClassic.KEY_DEFAULT);
				if (auth) {
					data = mfc.readBlock(7);
					cardData = getHexString(data, data.length);

					if (cardData != null) {						
						block_0_Data.setText(cardData);
					} else {
						showAlert(EMPTY_BLOCK_0);
					}
					
					// reading Block 0 /Sector 1
					data = mfc.readBlock(8);
					cardData = getHexString(data, data.length);

					if (cardData != null) {
						block_1_Data.setText(cardData);
					} else {
						showAlert(EMPTY_BLOCK_1);
					}
				} else {
					showAlert(AUTH);
				}

			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				showAlert(NETWORK);
			}
		} else {
			status_Data.setText("Online + Scan a tag");
		}
	}

	private void showAlert(int alertCase) {
		// prepare the alert box
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		switch (alertCase) {

		case AUTH:// Card Authentication Error
			alertbox.setMessage("Authentication Failed on Block 0");
			break;
		case EMPTY_BLOCK_0: // Block 0 Empty
			alertbox.setMessage("Failed reading Block 0");
			break;
		case EMPTY_BLOCK_1:// Block 1 Empty
			alertbox.setMessage("Failed reading Block 0");
			break;
		case NETWORK: // Communication Error
			alertbox.setMessage("Tag reading error");
			break;
		}
		// set a positive/yes button and create a listener
		alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			// Save the data from the UI to the database - already done
			public void onClick(DialogInterface arg0, int arg1) {
				clearFields();
			}
		});
		// display box
		alertbox.show();

	}

	public void onClick(View v) {
		clearFields();
	}

	private static void clearFields() {
		block_0_Data.setText("");
		block_1_Data.setText("");
		status_Data.setText("Ready for Scan");
	}

	public static String getHexString(byte[] raw, int len) {
		byte[] hex = new byte[2 * len];
		int index = 0;
		int pos = 0;

		for (byte b : raw) {
			if (pos >= len)
				break;

			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}

		return new String(hex);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		resolveIntent1(intent);
		// mText.setText("Discovered tag " + ++mCount + " with intent: " +
		// intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}
}