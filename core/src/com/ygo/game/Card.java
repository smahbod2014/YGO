package com.ygo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ygo.game.Types.Attribute;
import com.ygo.game.Types.CardFlavor;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;
import com.ygo.game.Types.Race;
import com.ygo.game.db.CardDefinition;

import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.Arrays;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class Card {

    public static final float THICKNESS = 0.0125f;
    public static TextureRegion FACE_DOWN_CARD_TEXTURE;
    public int maximumNumberOfAttacks = 1;
    private int maximumNumberOfBattlePositionChanges = 1;

    public Texture image;
    public Decal decal, faceDown;
    public boolean isHovering;
    public Location location = Location.HAND;
    public Vector2 positionInHand = new Vector2();
    public int attacksThisTurn = 0;
    private int battlePoisitionChangesThisTurn = 0;
    private CardPlayMode playMode;
    private UUID uniqueId;
    private CardDefinition definition;

    public Card(CardDefinition def, UUID uniqueId) {
        checkNotNull(def);
        image = CardManager.getOrLoadTexture(def.getSerial());
        decal = Decal.newDecal(new TextureRegion(image), true);
        faceDown = Decal.newDecal(FACE_DOWN_CARD_TEXTURE, true);
        this.definition = def;
        this.uniqueId = uniqueId;
        this.playMode = new CardPlayMode(CardPlayMode.NONE);
    }

    @Deprecated
    public Card(CardDefinition def) {
        this(def, UUID.randomUUID());
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Deprecated
    public Card copy() {
        return new Card(definition, UUID.randomUUID());
    }

    public boolean contains(Vector2 p, boolean opponentsCard) {
        Vector2 cardSize = opponentsCard ? Hand.CARD_SIZE_IN_HAND_FAR : Hand.CARD_SIZE_IN_HAND_NEAR;
        Rectangle r = new Rectangle(positionInHand.x, positionInHand.y, cardSize.x, cardSize.y);
        if (r.contains(p)) {
            return true;
        }
//        return r.contains(p);
        return false;
    }

    public void drawOnField(DecalBatch db, float x, float z, float width, float height, boolean opponentsCard) {
        drawOnField(db, x, 0, z, width, height, opponentsCard);
    }

    public void drawOnField(DecalBatch db, float x, float y, float z, float width, float height, boolean opponentsCard) {
        Decal which;
        if (playMode.isFaceDown()) {
            //TODO: Will need additional logic here to determine if monster or spell trap, since spell trap are vertical
//            faceDown.setRotation(new Quaternion(Vector3.X, -90).mulLeft(new Quaternion(Vector3.Y, 90)));
            faceDown.setRotationX(-90);
            if (playMode.isDefenseMode()) {
                faceDown.setRotation(new Quaternion(Vector3.Y, 90).mul(faceDown.getRotation()));
            }
            which = faceDown;
        }
        else {
//            decal.setRotation(new Quaternion(Vector3.X, -90));
            decal.setRotationX(-90);
            which = decal;
            if (playMode.isDefenseMode()) {
                decal.setRotation(new Quaternion(Vector3.Y, 90).mul(decal.getRotation()));
            }
        }
        which.setPosition(x + width / 2, y, z - height / 2);
        if (opponentsCard) {
            which.setRotation(new Quaternion(Vector3.Y, 180).mul(which.getRotation()));
        }
        which.setWidth(width);
        which.setHeight(height);
        db.add(which);
    }

    public void draw(SpriteBatch sb, boolean opponentsCard) {
        if (location == Location.HAND) {
            float y = positionInHand.y;
            if (isHovering)
                y += 30;
            if (opponentsCard) {
                sb.draw(FACE_DOWN_CARD_TEXTURE, positionInHand.x, y, Hand.CARD_SIZE_IN_HAND_FAR.x, Hand.CARD_SIZE_IN_HAND_FAR.y);
            }
            else {
                sb.draw(image, positionInHand.x, y, Hand.CARD_SIZE_IN_HAND_NEAR.x, Hand.CARD_SIZE_IN_HAND_NEAR.y);
            }
        }
    }

    public void onEffectActivation(PlayerType activator) {
        CardManager.getGlobals().get("c" + getSerial()).get("onEffectActivation").call(CoerceJavaToLua.coerce(activator.name()));
    }

    @Override
    public int hashCode() {
        return getSerial().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return getSerial().equals(((Card) o).getSerial());
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public boolean canAttack() {
        return playMode.isAttackMode() && attacksThisTurn < maximumNumberOfAttacks;
    }

    public boolean canChangeBattlePosition() {
        return battlePoisitionChangesThisTurn < maximumNumberOfBattlePositionChanges;
    }

    /** Pass in modes, one per element */
    public void setPlayMode(int... positions) {
        Arrays.stream(positions).forEach(playMode::changeMode);
    }

    public void overwritePlayMode(CardPlayMode mode) {
        this.playMode = mode;
    }

    public CardPlayMode getPlayMode() {
        return playMode;
    }

    public CardType getType() {
        return definition.getType();
    }

    public Attribute getAttribute() {
        return definition.getAttribute();
    }

    public Race getRace() {
        return definition.getRace();
    }

    public int getAtk() {
        return definition.getAtk();
    }

    public int getDef() {
        return definition.getDef();
    }

    public int getLevel() {
        return definition.getLevel();
    }

    public String getName() {
        return definition.getName();
    }

    public String getSerial() {
        return definition.getSerial();
    }

    public boolean isNormal() {
        return definition.getFlavors().contains(CardFlavor.Normal);
    }

    public boolean isEffect() {
        return definition.getFlavors().contains(CardFlavor.Effect);
    }

    public boolean isEquip() {
        return definition.getFlavors().contains(CardFlavor.Equip);
    }
}
