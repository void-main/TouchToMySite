package me.voidmain.touchtomysite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private EditText mEtUrl;
	private Button mBtnConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mEtUrl = (EditText) findViewById(R.id.et_website_url);
		mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
		
		mBtnConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = mEtUrl.getText().toString();
				if(TextUtils.isEmpty(url)) {
					Toast.makeText(MainActivity.this, R.string.err_no_website_url, Toast.LENGTH_LONG).show();
				} else {
					Intent writeTagIntent = new Intent(MainActivity.this, WriteTagActivity.class);
					writeTagIntent.putExtra(TouchToMySiteConstants.EXTRA_URL_KEY, url);
					MainActivity.this.startActivity(writeTagIntent);
				}
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}

}
