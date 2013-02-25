package me.voidmain.touchtomysite;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class WriteTagActivity extends Activity {

	private static final String TAG = WriteTagActivity.class.getCanonicalName();

	private String mTargetUrl;

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_tag);

		mTargetUrl = getIntent().getExtras().getString(
				TouchToMySiteConstants.EXTRA_URL_KEY);
		String template = getResources().getString(R.string.write_tag_hint);
		String hint = String.format(template, mTargetUrl);
		TextView writeTagHint = (TextView) findViewById(R.id.tv_write_tag_hint);
		writeTagHint.setText(hint);

		mAdapter = NfcAdapter.getDefaultAdapter(this);

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		IntentFilter techDetected = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);
		mFilters = new IntentFilter[] { ndefDetected, tagDetected, techDetected };
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					null);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Log.d(TAG, "Process NDEF discovered action");
		} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Log.d(TAG, "Process TAG discovered action");
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.d(TAG, "Process TECH discovered action");
		} else {
			Log.d(TAG, "Ignore action " + intent.getAction());
		}

		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		try {
			write(tag);
			Toast.makeText(this, R.string.write_tag_success, Toast.LENGTH_LONG)
					.show();
			finish();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
	}

	private void write(Tag tag) throws IOException, FormatException {
		NdefMessage rawMessage = new NdefMessage(
				NdefRecord.createUri(mTargetUrl));
		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Write unformatted tag");
			try {
				format.connect();
				format.format(rawMessage);
			} catch (Exception e) {
			} finally {
				try {
					format.close();
				} catch (IOException e) {
				}
			}
			Log.d(TAG, "Cannot write unformatted tag");
		} else {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				try {
					Log.d(TAG, "Write formatted tag");

					ndef.connect();
					if (!ndef.isWritable()) {
						Log.d(TAG, "Tag is not writeable");
					}

					if (ndef.getMaxSize() < rawMessage.toByteArray().length) {
						Log.d(TAG,
								"Tag size is too small, have "
										+ ndef.getMaxSize() + ", need "
										+ rawMessage.toByteArray().length);
					}
					ndef.writeNdefMessage(rawMessage);
				} catch (Exception e) {
				} finally {
					try {
						ndef.close();
					} catch (IOException e) {
					}
				}
			} else {
			}
			Log.d(TAG, "Cannot write formatted tag");
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_write_tag, menu);
	// return true;
	// }

}
