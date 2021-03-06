package com.ygo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ygo.game.Messages.AttackInitiationMessage;
import com.ygo.game.Messages.DirectAttackInitiationMessage;
import com.ygo.game.Types.Player;
import com.ygo.game.Types.Zone;

public class Cannonball {

    public static Texture cannonball;

    private static final float travelTime = 1;
    private float elapsedTime;
    private Decal decal;
    private Vector3 originPos, destPos;
    public Cell attacker, target;
    public boolean done = false;
    public Player initiatedBy;

    @Deprecated
    public Cannonball(Player initiatedBy, Cell origin, Cell destination) {
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
        init(field, Player.valueOf(m.attacker), m.attackerIndex, m.targetIndex);
    }

    public Cannonball(Field field, DirectAttackInitiationMessage m) {
        init(field, Player.valueOf(m.attacker), m.attackerIndex);
    }

    private void init(Field field, Player initiatedBy, int originIndex) {
        Cell origin = field.getCellByIndex(initiatedBy, Zone.Monster, originIndex);
        originPos = new Vector3(origin.getCenter().x, 0.15f, origin.getCenter().y);

        float enemyCellZ = field.getCellByIndex(initiatedBy.getOpponent(), Zone.Monster, 0).getCenter().y;
        if (enemyCellZ < origin.getCenter().y) {
            Vector2 target = field.getCellByIndex(initiatedBy.getOpponent(), Zone.SpellTrap, 2).getCenter().sub(0, origin.size.y);
            destPos = new Vector3(target.x, 0.15f, target.y);
        }
        else {
            Vector2 target = field.getCellByIndex(initiatedBy.getOpponent(), Zone.SpellTrap, 2).getCenter().add(0, origin.size.y);
            destPos = new Vector3(target.x, 0.15f, target.y);
        }

        decal = Decal.newDecal(origin.size.x / 2, origin.size.y / 2, new TextureRegion(cannonball));
        decal.setRotationX(-90);
        decal.setPosition(origin.getCenter().x, .15f, origin.getCenter().y);
        attacker = origin;
        this.initiatedBy = initiatedBy;
    }

    private void init(Field field, Player initiatedBy, int originIndex, int destinationIndex) {
        Cell origin = field.getCellByIndex(initiatedBy, Zone.Monster, originIndex);
        Cell destination = field.getCellByIndex(initiatedBy.getOpponent(), Zone.Monster, destinationIndex);
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
