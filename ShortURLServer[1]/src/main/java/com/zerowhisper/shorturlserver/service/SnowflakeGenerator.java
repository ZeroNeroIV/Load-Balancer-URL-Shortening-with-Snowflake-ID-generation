package com.zerowhisper.shorturlserver.service;

import static java.lang.System.currentTimeMillis;

public class SnowflakeGenerator {
    private final long epoch = 1704067200000L;  // Epoch (in milliseconds) for 2024-01-01
    private final long machineIdBits = 5L;      // Number of bits for machine ID (up to 32 machines)
    private final long sequenceBits = 12L;      // Number of bits for sequence within a millisecond (4096 IDs per ms)

    private final long maxMachineId = ~(-1L << machineIdBits);  // Maximum machine ID (31)
    private final long maxSequence = ~(-1L << sequenceBits);    // Maximum sequence (4095)

    private final long machineIdShift = sequenceBits;                   // Shift for machine ID
    private final long timestampShift = sequenceBits + machineIdBits;   // Shift for timestamp

    // Variables for state
    private long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    // Constructor to initialize machine ID
    public SnowflakeGenerator(long machineId) {
        if (machineId < 0 || machineId > maxMachineId) {
            throw new IllegalArgumentException("Machine ID out of bounds");
        }
        this.machineId = machineId;
    }

    // Method to generate a new unique ID
    public synchronized Long nextId() {
        long timestamp = currentTimeMillis();

        // If the current timestamp is earlier than the last timestamp, throw an error
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        // If we're in the same millisecond, increment the sequence
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            // If the sequence exceeds its limit, wait for the next millisecond
            if (sequence == 0) {
                timestamp = waitUntilNextMillis(lastTimestamp);
            }
        } else {
            // Reset the sequence for a new millisecond
            sequence = 0L;
        }

        // Update last generated timestamp
        lastTimestamp = timestamp;

        // Generate the ID by shifting and combining bits
        return ((timestamp - epoch) << timestampShift)  // Timestamp part
                | (machineId << machineIdShift)         // Machine ID part
                | sequence;                             // Sequence part
    }

    // Wait until the next millisecond if the sequence overflows
    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }
}
