package com.dancing_koala.speedmeter.models;

import java.util.Random;

/**
 * Model representing a tracking session
 */
public class TrackingSession {

    /**
     * Random chars count to use for random ID generation.
     */
    private static final int ID_RANDOM_CHARS_COUNT = 51;
    /**
     * Authorized chars for random ID generation.
     */
    private static final String AUTHORIZED_ID_CHARS = "ABCDEFHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxy0123456789$#._!?";

    /**
     * The tracking session's id represented by a 64 chars string.
     */
    private String mId;
    /**
     * The timestamp on which the tracking session started.
     */
    private long mStartTime;
    /**
     * The timestamp on which the tracking session ended.
     */
    private long mEndTime;
    /**
     * The total distance traveled of the tracking session
     */
    private float mDistance;
    /**
     * The total distance of the tracking session
     */
    private float mAverageSpeed;

    /**
     * Constructor
     *
     * @param creationTimestamp The tracking session's creation timestamp
     */
    public TrackingSession(long creationTimestamp) {
        this.mId = generateRandomId(creationTimestamp);
        this.mStartTime = creationTimestamp;
        this.mEndTime = -1;
    }

    /**
     * Constructor
     *
     * @param id        The tracking session's id
     * @param startTime The tracking session's start timestamp
     * @param endTime   The tracking session's end timestamp
     */
    public TrackingSession(String id, long startTime, long endTime) {
        this.mId = id;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }

    /**
     * Generates a random ID based on 51 random (authorized) chars on which is appended the
     * session's creation timestamp.
     *
     * @param timestamp The timestamp to append to the random chars
     * @return The randomly generated ID
     */
    public static String generateRandomId(long timestamp) {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();

        while (builder.length() < ID_RANDOM_CHARS_COUNT) {
            builder.append(AUTHORIZED_ID_CHARS.charAt(rand.nextInt(AUTHORIZED_ID_CHARS.length())));
        }

        builder.append(timestamp);

        return builder.toString();
    }

    /**
     * Gets the tracking session's id
     *
     * @return The tracking session's id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the tracking session's id
     *
     * @param id The id to be set
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Gets the tracking session's start timestamp
     *
     * @return The tracking session's start timestamp
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * Sets the tracking session's start timestamp
     *
     * @param startTime The start timestamp to be set
     */
    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    /**
     * Gets the tracking session's end timestamp
     *
     * @return The tracking session's end timestamp
     */
    public long getEndTime() {
        return mEndTime;
    }

    /**
     * Sets the tracking session's end timestamp
     *
     * @param mEndTime The end timestamp to be set
     */
    public void setEndTime(long mEndTime) {
        this.mEndTime = mEndTime;
    }

    /**
     * Gets the tracking session's traveled distance
     *
     * @return The tracking session's traveled distance
     */
    public float getDistance() {
        return mDistance;
    }

    /**
     * Sets the tracking session's traveled distance
     *
     * @param distance The distance to be set
     */
    public void setDistance(float distance) {
        this.mDistance = distance;
    }

    /**
     * Gets the tracking session's average speed
     *
     * @return The tracking session's average speed
     */
    public float getAverageSpeed() {
        return mAverageSpeed;
    }

    /**
     * Sets the tracking session's average speed
     *
     * @param averageSpeed The end timestamp to be set
     */
    public void setAverageSpeed(float averageSpeed) {
        this.mAverageSpeed = averageSpeed;
    }

    @Override
    public String toString() {
        return "TrackingSession{" +
                "mId='" + mId + '\'' +
                ", mStartTime=" + mStartTime +
                ", mEndTime=" + mEndTime +
                ", mDistance=" + mDistance +
                ", mAverageSpeed=" + mAverageSpeed +
                '}';
    }
}
