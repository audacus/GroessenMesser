package ch.fenceposts.appquest.groessenmesser;

import java.io.IOException;

import ch.fenceposts.appquest.groessenmesser.controller.ControllerSensor;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener, SurfaceHolder.Callback, Camera.PictureCallback {

	private static final String DEBUG_TAG = "mydebug";
	private final float[] magneticFieldData = new float[3];
	private final float[] accelerationData = new float[3];
	private Camera camera;
	private ControllerSensor controllerSensor;
	private SensorManager sensorManager;
	private Sensor sensorMagneticField;
	private Sensor sensorAccelerometer;
	private SurfaceHolder cameraPreviewHolder;
	private SurfaceView surfaceViewCameraPreview;
	private RadioButton radioButtonAngleAlpha;
	private RadioButton radioButtonAngleBeta;
	private RadioGroup radioButtonGroupAngles;
	private TextView textViewAngleValueAlpha;
	private TextView textViewAngleValueBeta;

	@Override
	// wenn die Activity erstellt wird ...
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// SensorManager instanzieren -> ControllerSensor.java
		controllerSensor = new ControllerSensor(getApplicationContext());
		sensorManager = controllerSensor.getSensorManager();

		// debuggen wie viele Magnetic_Field Sensoren es gibt
		Log.d(DEBUG_TAG, "magnetic field sensors: " + Integer.toString(controllerSensor.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size()));
		// debugg wie viele Accelerometer Sensoren es gibt
		Log.d(DEBUG_TAG, "accelerator sensors: " + Integer.toString(controllerSensor.getSensorList(Sensor.TYPE_ACCELEROMETER).size()));

		// check and try to add magnetic field sensor
		if ((sensorMagneticField = controllerSensor.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0)) == null) {
			try {
				throw new Exception("No magnetic field sensor found!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// check and try to add accelerometer sensor
		if ((sensorAccelerometer = controllerSensor.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0)) == null) {
			try {
				throw new Exception("No accelerator sensor found!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// initialize layout components
			// camera preview
			surfaceViewCameraPreview = (SurfaceView) findViewById(R.id.surfaceViewCameraPreview);
			// radio button group angles
			radioButtonGroupAngles = (RadioGroup) findViewById(R.id.radioButtonGroupAngles);
			// radio button group angles children
			radioButtonAngleAlpha = (RadioButton) findViewById(R.id.radioButtonAngleAlpha);
			radioButtonAngleBeta = (RadioButton) findViewById(R.id.radioButtonAngleBeta);
			// text views angle values
			textViewAngleValueAlpha = (TextView) findViewById(R.id.textViewAngleValueAlpha);
			textViewAngleValueBeta = (TextView) findViewById(R.id.textViewAngleValueBeta);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Holder hält das Kamerabild
		cameraPreviewHolder = surfaceViewCameraPreview.getHolder();

		if (sensorMagneticField != null) {
			// Sensor auf Activity (this) registrieren wenn er vorhanden ist mit einer normalen Abfrageggeschwindigkeit
			sensorManager.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (sensorAccelerometer != null) {
			// Sensor auf Activity (this) registrieren wenn er vorhanden ist mit einer normalen Abfrageggeschwindigkeit
			sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		}

		cameraPreviewHolder.addCallback(this);
		
		// must have code for taking pictures (not needed)
		new Camera.PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

			}
		};

		new Camera.ShutterCallback() {

			@Override
			public void onShutter() {

			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();

		// wenn die camera nicht null ist ...
		if (camera != null) {
			// -> preview stoppen
			camera.stopPreview();
			// camera für andere Applikationen freigeben
			camera.release();
		}
		
		// Sensoren unregistrieren
		sensorManager.unregisterListener(this);
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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calculate, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		camera = Camera.open();
	}

	@Override
	// wenn SurfaceView gechanged wird (z.B. erstellt) ...
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// bugfix wegen canvas
		camera.stopPreview();
		// camera.setDisplayOrientation(90);

		// aus Beispiel vom Buch kopierter Code
		Camera.Parameters params = camera.getParameters();
		Camera.Size previewSize = params.getPreviewSize();

		// loggen von der SurfaceView grösse und der CameraPreview Grösser
		Log.d(DEBUG_TAG, "cameraPreview width & height: " + Integer.toString(surfaceViewCameraPreview.getWidth()) + " & " + Integer.toString(surfaceViewCameraPreview.getHeight()));
		Log.d(DEBUG_TAG, "camerePreview width / height: " + Float.toString((float) surfaceViewCameraPreview.getWidth() / (float) surfaceViewCameraPreview.getHeight()));

		Log.d(DEBUG_TAG, "previewSize width & height: " + Integer.toString(previewSize.width) + " & " + Integer.toString(previewSize.height));
		Log.d(DEBUG_TAG, "previewSize width / height: " + Float.toString((float) previewSize.width / (float) previewSize.height));

		// grösse Preview setzen
		params.setPreviewSize(previewSize.width, previewSize.height);
		camera.setParameters(params);

		try {
			// previewHolder von SurfaceView als PreviewDisplay setzen
			camera.setPreviewDisplay(cameraPreviewHolder);
		} catch (IOException ioe) {
			Log.d(DEBUG_TAG, ioe.getMessage());
		}
		
		// surfaceView anzeigen
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// Snippet aus GitHub von Misto
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			System.arraycopy(event.values, 0, accelerationData, 0, 3);
		}

		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			System.arraycopy(event.values, 0, magneticFieldData, 0, 3);
		}

		int currentRotationValue = (int) getCurrentRotationValue();

		// TODO hier weiterverarbeiten
		// Log.d(DEBUG_TAG, Double.toString(currentRotationValue));

		// checked angle radio button
		int checkedAngleRadioButtonId = radioButtonGroupAngles.getCheckedRadioButtonId();
		View checkedAngleRadioButton = radioButtonGroupAngles.findViewById(checkedAngleRadioButtonId);
		int checkedAngleRadioButtonIndex = radioButtonGroupAngles.indexOfChild(checkedAngleRadioButton);
		
		// currentRotationValue zu ausgewähltem Checkbutton setzen
		try {
			// check checked angle radio button
			if (checkedAngleRadioButtonIndex == radioButtonGroupAngles.indexOfChild(radioButtonAngleAlpha)) {
				textViewAngleValueAlpha.setText(Integer.toString(currentRotationValue));
			} else if (checkedAngleRadioButtonIndex == radioButtonGroupAngles.indexOfChild(radioButtonAngleBeta)) {
				// bei Beta ist der Winkel = (Winkel - WinkelAlpha)
				textViewAngleValueBeta.setText(Integer.toString(currentRotationValue - Integer.parseInt((String) textViewAngleValueAlpha.getText())));
			}
		} catch (NumberFormatException nfe) {
			Log.d(DEBUG_TAG, "Number format exception!");
		}
	}

	// snippet von Misto von GitHub
	private double getCurrentRotationValue() {
		float[] rotationMatrix = new float[16];

		if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerationData, magneticFieldData)) {

			float[] orientation = new float[4];
			SensorManager.getOrientation(rotationMatrix, orientation);

			double tilting = Math.toDegrees(orientation[2]);

			return Math.abs(tilting);
		}

		return 0;
	}

	// Werte von Alpha und Beta an CalculateView übergeben
	public void gotoCalculate(View view) {
		// neuer Intent mit CalculateActivity
		Intent intentCalculate = new Intent(this, CalculateActivity.class);
		
		intentCalculate.putExtra("ch.appquest.groessenmesser.alphaValue", (String) textViewAngleValueAlpha.getText());
		intentCalculate.putExtra("ch.appquest.groessenmesser.betaValue", (String) textViewAngleValueBeta.getText());
		
		// Intent starten
		startActivity(intentCalculate);
	}
}
