package org.bhi.sfClient.orientationProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_GRAVITY
 * Gravity} and {@link Sensor#TYPE_MAGNETIC_FIELD Compass}.
 * 
 * @author Alexander Pacha
 * 
 */
public class GravityCompassProvider extends OrientationProvider {

    /**
     * Compass values
     */
    final private float[] magnitudeValues = new float[3];

    /**
     * Gravity values
     */
    final private float[] gravityValues = new float[3];

    /**
     * Inclination values
     */
    float[] inclinationValues = new float[16];

    /**
     * Initialises a new GravityCompassProvider
     * 
     * @param sensorManager The android sensor manager
     */
    public GravityCompassProvider(SensorManager sensorManager) {
        super(sensorManager);

        //Add the compass and the gravity sensor
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnitudeValues, 0, magnitudeValues.length);
        } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            System.arraycopy(event.values, 0, gravityValues, 0, gravityValues.length);
        }

        if (magnitudeValues != null && gravityValues != null) {
            // Fuse gravity-sensor (virtual sensor) with compass
            SensorManager.getRotationMatrix(currentOrientationRotationMatrix.matrix, inclinationValues, gravityValues, magnitudeValues);
            // Transform rotation matrix to quaternion
            currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix);
        }
    }
}
