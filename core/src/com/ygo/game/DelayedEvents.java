package com.ygo.game;

import com.badlogic.gdx.utils.Array;

/**
 * Created by semahbod on 10/15/16.
 */
public class DelayedEvents {

    private static class Event {
        private Runnable runnable;
        private float remainingTime;

        public Event(Runnable runnable, float remainingTime) {
            this.runnable = runnable;
            this.remainingTime = remainingTime;
        }
    }

    private static Array<Event> events = new Array<Event>();

    public static void schedule(Runnable runnable, float delaySeconds) {
        events.add(new Event(runnable, delaySeconds));
    }

    public static void update(float dt) {
        for (int i = 0; i < events.size; i++) {
            Event event = events.get(i);
            event.remainingTime -= dt;
            if (event.remainingTime <= 0) {
                event.runnable.run();
                events.removeValue(event, true);
                i--;
            }
        }
    }
}
