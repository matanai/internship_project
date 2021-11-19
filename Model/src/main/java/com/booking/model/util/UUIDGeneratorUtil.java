package com.booking.model.util;

import com.fasterxml.uuid.Generators;

import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.IntStream;

/**
 * Utility class to generate UUID
 */
public class UUIDGeneratorUtil
{
    private static final int QUEUE_SIZE = 10_000;
    private static final Deque<UUID> PRE_GEN_QUEUE = new ConcurrentLinkedDeque<>();

    public static UUID getUUID() {
        if (PRE_GEN_QUEUE.isEmpty()) {
            synchronized (UUIDGeneratorUtil.class) {
                if (PRE_GEN_QUEUE.isEmpty()) {
                    _replenishQueue();
                }
            }
        }

        return PRE_GEN_QUEUE.poll();
    }

    private static void _replenishQueue() {
        IntStream.rangeClosed(0, QUEUE_SIZE)
                .forEach(e -> PRE_GEN_QUEUE.push(Generators.timeBasedGenerator().generate()));
    }
}
