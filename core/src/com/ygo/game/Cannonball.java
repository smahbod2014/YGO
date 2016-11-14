package com.ygo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.ygo.game.GameStates.PlayState;
import com.ygo.game.Messages.AttackInitiationMessage;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.ZoneType;

public class Cannonball {

    public static Texture cannonball;

    private static final float travelTime = 1;
    private float elapsedTime;
    private Decal decal;
    private Vector3 originPos, destPos;
    public Cell attacker, target;
    public boolean done = false;
    public PlayerType initiatedBy;

    @Deprecated
    public Cannonball(PlayerType initiatedBy, Cell origin, Cell destination) {
        originPos = new Vector3(origin.getCenter().x, 0.15f, origin.getCenter().y);
        destPos = new Vector3(destination.getCenter().x, 0.15f, destination.getCenter().y);
        decal = Decal.newDecal(origin.size.x / 2, origin.size.y / 2, new TextureRegion(cannonball));
        decal.setRotationX(-90);
        decal.setPosition(origin.getCenter().x, .15f, origin.getCenter().y);
        attacker = origin;
        target = destination;
        this.initiatedBy = initiatedBy;
    }

    public Cannonball(Field field, AttackInitiationMessage m) {
        init(field, PlayerType.valueOf(m.attacker), m.attackerIndex, m.targetIndex);
    }

    private void init(Field field, PlayerType initiatedBy, int originIndex, int destinationIndex) {
        Cell origin = field.getCellByIndex(initiatedBy, ZoneType.MONSTER, originIndex);
        Cell destination = field.getCellByIndex(initiatedBy.getOpponent(), ZoneType.MONSTER, destinationIndex);
        originPos = new Vector3(origin.getCenter().x, 0.15f, origin.getCenter().y);
        destPos = new Vector3(destination.getCenter().x, 0.15f, destination.getCenter().y);
        decal = Decal.newDecal(origin.size.x / 2, origin.size.y / 2, new TextureRegion(cannonball));
        decal.setRotationX(-90);
        decal.setPosition(origin.getCenter().x, .15f, origin.getCenter().y);
        attacker = origin;
        target = destination;
        this.initiatedBy = initiatedBy;
    }

    private Vector3 lerpVector(Vector3 from, Vector3 to, float t) {
        return new Vector3(MathUtils.lerp(from.x, to.x, t), MathUtils.lerp(from.y, to.y, 5), MathUtils.lerp(from.z, to.z, t));
    }

    public void update(float dt) {
        if (!done) {
            elapsedTime += dt;
            float t = elapsedTime / travelTime;
            decal.setPosition(lerpVector(originPos, destPos, t));
            if (t >= 1f) {
                done = true;
            }
        }
    }

    public void render(DecalBatch batch) {
        batch.add(decal);
    }
}
