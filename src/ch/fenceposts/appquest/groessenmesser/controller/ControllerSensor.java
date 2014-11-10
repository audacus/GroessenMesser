package ch.fenceposts.appquest.groessenmesser.controller;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class ControllerSensor {
	private SensorManager sensorManager;

	// Constructor
	public ControllerSensor(Context context) {
		this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * @return the sensorManager
	 */
	public SensorManager getSensorManager() {
		return sensorManager;
	}

	/**
	 * @param sensorManager the sensorManager to set
	 */
	public void setSensorManager(SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	public List<Sensor> getSensorList(int sensorType) {
		return sensorManager.getSensorList(sensorType);
	}

}
