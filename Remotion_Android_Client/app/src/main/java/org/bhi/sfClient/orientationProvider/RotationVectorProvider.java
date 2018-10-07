package org.bhi.sfClient.orientationProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_ROTATION_VECTOR Android
 * Rotation Vector sensor}.
 * 
 * @author Alexander Pacha
 * 
 */
public class RotationVectorProvider extends OrientationProvider {

    /**
     * Temporary quaternion to store the values obtained from the SensorManager
     */
    final private float[] temporaryQuaternion = new float[4];

    /**
     * Initialises a new RotationVectorProvider
     * 
     * @param sensorManager The android sensor manager
     */
    public RotationVectorProvider(SensorManager sensorManager) {
        super(sensorManager);

        //The rotation vector sensor that is being used for this provider to get device orientation
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, event.values);

            // Get Quaternion
            // Calculate angle. Starting with API_18, Android will provide this value as event.values[3], but if not, we have to calculate it manually.
            SensorManager.getQuaternionFromVector(temporaryQuaternion, event.values);
            currentOrientationQuaternion.setXYZW(temporaryQuaternion[1], temporaryQuaternion[2], temporaryQuaternion[3], -temporaryQuaternion[0]);
        }
    }
}
