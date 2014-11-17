package ch.fenceposts.appquest.groessenmesser;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.fenceposts.appquest.groessenmesser.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class CalculateActivity extends Activity {

	private static final String DEBUG_TAG = "mydebug";
	private static final int SCAN_QR_CODE_REQUEST_CODE = 0;
	private String alphaValue;
	private String betaValue;
	private EditText editTextGivenValue;
	private RadioGroup radioButtonGroupCalculate;
	private RadioButton radioButtonCalculateDistance;
	private RadioButton radioButtonCalculateHeight;
	private TextView textViewCalculateValueAlpha;
	private TextView textViewCalculateValueBeta;
	private TextView textViewGivenValue;
	private TextView textViewCalculatedValue;
	private TextView textViewResultValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// calculate view anzeigen
		setContentView(R.layout.activity_calculate);

		Intent intent = getIntent();
		// Alpha und Beta value bekommen die als extras in der MainActivity
		// gesetzt wurden
		alphaValue = intent
				.getStringExtra("ch.appquest.groessenmesser.alphaValue");
		betaValue = intent
				.getStringExtra("ch.appquest.groessenmesser.betaValue");

		// Design komponenten initialisieren
		textViewCalculateValueAlpha = (TextView) findViewById(R.id.textViewCalculateValueAlpha);
		textViewCalculateValueBeta = (TextView) findViewById(R.id.textViewCalculateValueBeta);
		textViewGivenValue = (TextView) findViewById(R.id.textViewGivenValue);
		textViewCalculatedValue = (TextView) findViewById(R.id.textViewCalculatedValue);
		textViewResultValue = (TextView) findViewById(R.id.textViewResultValue);

		editTextGivenValue = (EditText) (TextView) findViewById(R.id.editTextGivenValue);

		textViewCalculateValueAlpha.setText(alphaValue);
		textViewCalculateValueBeta.setText(betaValue);

		radioButtonGroupCalculate = (RadioGroup) findViewById(R.id.radioButtonGroupCalculate);
		radioButtonCalculateDistance = (RadioButton) findViewById(R.id.radioButtonCalculateDistance);
		radioButtonCalculateHeight = (RadioButton) findViewById(R.id.radioButtonCalculateHeight);

		// wenn ein bei der Radiogroup etwas anderes ausgewählt wird ...
		radioButtonGroupCalculate
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						View checkedCalculateRadioButton = group
								.findViewById(checkedId);
						int checkedCalculateRadioButtonIndex = group
								.indexOfChild(checkedCalculateRadioButton);
						// setze Text für Distanz ausrechnen
						if (checkedCalculateRadioButtonIndex == group
								.indexOfChild(radioButtonCalculateDistance)) {
							textViewGivenValue.setText("height");
							textViewCalculatedValue.setText("distance");
							// ... setze Text für Höhe ausrechnen
						} else if (checkedCalculateRadioButtonIndex == group
								.indexOfChild(radioButtonCalculateHeight)) {
							textViewGivenValue.setText("distance");
							textViewCalculatedValue.setText("height");
						}
						calculateResult(null);
					}
				});
		editTextGivenValue.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				calculateResult(null);
			}
		});
	}

	@Override
	// Snippet von HSR zum einlesen vom QR-Code
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.add("Log");
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	// Wenn man einen QR-Code eingelesen hat -> Result verarbeiten (loggen)
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String logMsg = intent.getStringExtra("SCAN_RESULT");
				// Weiterverarbeitung..
				log(logMsg);
			}
		}
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

	// Mathematik zeugs -> distanz und höhe ausrechnen
	public void calculateResult(View view) {
		try {

			double alphaAlpha = Double
					.parseDouble((String) textViewCalculateValueAlpha.getText());
			double betaBeta = Double
					.parseDouble((String) textViewCalculateValueBeta.getText());
			double distance;
			double height;
			double alphaGamma;
			double betaAlpha = alphaAlpha;
			double betaGamma;
			double alphaHypotenuse;
			double betaAdjacent;
			double result;

			// get checked angle radio button
			int checkedCalculateRadioButtonId = radioButtonGroupCalculate
					.getCheckedRadioButtonId();
			View checkedCalculateRadioButton = radioButtonGroupCalculate
					.findViewById(checkedCalculateRadioButtonId);
			int checkedCalculateRadioButtonIndex = radioButtonGroupCalculate
					.indexOfChild(checkedCalculateRadioButton);

			// wenn die Distanz zum ausrechnen gewählt wurde -> berechne Distanz
			if (checkedCalculateRadioButtonIndex == radioButtonGroupCalculate
					.indexOfChild(radioButtonCalculateDistance)) {
				height = Double.parseDouble(editTextGivenValue.getText()
						.toString());
				betaGamma = 180 - betaAlpha - betaBeta;
				// b = (c * sin(beta)) / sin(gamma)
				betaAdjacent = (height * Math.sin(Math.toRadians(betaGamma)) / Math
						.sin(Math.toRadians(betaBeta)));
				alphaHypotenuse = betaAdjacent;

				alphaGamma = 180 - 90 - alphaAlpha;

				// adjacent = cos(alpha) * hypotenuse
				distance = Math.cos(Math.toRadians(alphaGamma))
						* alphaHypotenuse;
				result = distance;

				// ..sonst berechne die Höhe
			} else {
				distance = Double.parseDouble(editTextGivenValue.getText()
						.toString());
				// hypotenuse = opposite leg / sin(alpha)
				alphaHypotenuse = distance
						/ Math.sin(Math.toRadians(alphaAlpha));
				betaAdjacent = alphaHypotenuse;
				betaGamma = 180 - betaAlpha - betaBeta;
				// height = (c * sin(alpha)) / sin(gamma)
				height = (betaAdjacent * Math.sin(Math.toRadians(betaBeta)))
						/ Math.sin(Math.toRadians(betaGamma));
				result = height;
			}
			textViewResultValue.setText(Double.toString(new BigDecimal(result)
					.setScale(2, RoundingMode.HALF_UP).doubleValue()));
		} catch (NumberFormatException nfe) {
			Log.d(DEBUG_TAG, "Number format exception!");
		}

	}

	// Log-Funktion von der HSR zum loggen des ausgerechneten Resultates und dem
	// QR-Code
	private void log(String qrCode) {
		Intent intent = new Intent("ch.appquest.intent.LOG");

		if (getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
			Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG)
					.show();
			return;
		}

		intent.putExtra("ch.appquest.taskname", "Grössen Messer");
		CharSequence calculatedObjectHeight = textViewResultValue.getText();
		// Achtung, je nach App wird etwas anderes eingetragen (siehe Tabelle
		// ganz unten):
		intent.putExtra("ch.appquest.logmessage", qrCode + ": "
				+ calculatedObjectHeight);

		startActivity(intent);
	}
}
