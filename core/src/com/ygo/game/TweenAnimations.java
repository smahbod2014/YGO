package com.ygo.game;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ygo.game.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TweenAnimations {

    private static List<Data> data = new ArrayList<>();

    public static void submit(Cell origin, Cell destination) {
        origin.isAnimating = true;
        data.add(new Data(origin, destination, 0.5f));
    }

    public static void update(float dt) {
        data.forEach(d -> {
            d.t += dt;
            float progress = Interpolation.pow2.apply(Math.min(1f, d.t / d.requiredTime));
            Vector2 pos = Utils.lerpVector2(d.origin.position, d.destination.position, progress);
            d.origin.animationPosition = new Vector3(pos.x, 0, pos.y);

            if (progress >= 1) {
                d.origin.isAnimating = false;
            }
        });
    }

    private static class Data {
        public float t;
        public float requiredTime;
        public Cell origin;
        public Cell destination;

        public Data(Cell origin, Cell destination, float requiredTime) {
            this.requiredTime = requiredTime;
            this.origin = origin;
            this.destination = destination;
        }
    }
}
