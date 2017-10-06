package com.example.jakubbadysiak.trackmyroute.Accelerometer;

/**
 * Created by jakub.badysiak on 03.10.2017.
 */

public class StepDetector {
    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;

    private static final float STEP_THRESHOLD = 50f;
    private static final int STEP_DELAY_NS = 250000000;

    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private long lastStepTimeNs = 0;
    private float oldVelocityEstimate = 0;

    private StepListener stepListener;

    public void registerListener(StepListener stepListener){
        this.stepListener = stepListener;
    }

    public void updateAccel(long timeNs, float x, float y, float z){
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        float[] worldZ = new float[3];
        worldZ[0] = SensorFilter.sum(accelRingX)/Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = SensorFilter.sum(accelRingY)/Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = SensorFilter.sum(accelRingZ)/Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalizationFactor = SensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0]/normalizationFactor;
        worldZ[1] = worldZ[1]/normalizationFactor;
        worldZ[2] = worldZ[2]/normalizationFactor;

        float  currentZ = SensorFilter.dot(worldZ, currentAccel) - normalizationFactor;
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

        float velocityEstimate = SensorFilter.sum(velRing);

        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD && (timeNs - lastStepTimeNs > STEP_DELAY_NS)){
            stepListener.step(timeNs);
            lastStepTimeNs = timeNs;
        }
        oldVelocityEstimate = velocityEstimate;
    }
}
