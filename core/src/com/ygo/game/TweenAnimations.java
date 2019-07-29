package com.ygo.game;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.ygo.game.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TweenAnimations {

    private static List<Data> data = new ArrayList<>();

    public static void submit(Card card, Cell origin, Cell destination, Runnable completionCallback) {
        card.setBeingAnimated(true);
        data.add(new Data(card, origin, destination, completionCallback, 0.5f));
    }

    public static void update(float dt) {
        List<Data> dataToRemove = new ArrayList<>();
        for (Data d : data) {
            d.t += dt;
            float progress = Interpolation.pow2.apply(Math.min(1f, d.t / d.requiredTime));
            Vector3 pos = Utils.lerpVector3(d.origin.getPaddedPosition3(), d.destination.getPaddedPosition3(), progress);
            d.card.setAnimationPosition(pos);

            if (progress >= 1) {
                d.card.setBeingAnimated(false);
                d.completionCallback.run();
                dataToRemove.add(d);
            }
        }

        data.removeAll(dataToRemove);
    }

    private static class Data {
        public float t;
        public float requiredTime;
        public Cell origin;
        public Cell destination;
        public Card card;
        public Runnable completionCallback;

        public Data(Card card, Cell origin, Cell destination, Runnable completionCallback, float requiredTime) {
            this.requiredTime = requiredTime;
            this.origin = origin;
            this.destination = destination;
            this.completionCallback = completionCallback;
            this.card = card;
        }
    }
}
