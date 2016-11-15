package com.ygo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Location;
import com.ygo.game.Types.PlayerType;

public class Card {

    public static final float THICKNESS = 0.0125f;
    public static TextureRegion FACE_DOWN_CARD_TEXTURE;
    public int maximumNumberOfAttacks = 1;

    public Texture image;
    public Decal decal, faceDown;
    public boolean isHovering;
    public int playMode;
    public String id;
    public Location location = Location.HAND;
    public Vector2 positionInHand = new Vector2();
    public int cardType;
    public int atk;
    public int def;
    public int level;
    public int attacksThisTurn = 0;

    /**
     * @param filename The filename of the card's image without the path or extension
     */
    public Card(String filename, int cardType, int atk, int def, int level) {
        image = new Texture("cards/" + filename + ".jpg");
        decal = Decal.newDecal(new TextureRegion(image), true);
        faceDown = Decal.newDecal(FACE_DOWN_CARD_TEXTURE, true);
        id = filename;
        this.cardType = cardType;
        this.atk = atk;
        this.def = def;
        this.level = level;
    }

    public Card(String filename, int cardType) {
        this(filename, cardType, 0, 0, 0);
    }

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Card) obj).id);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Card copy() {
        return new Card(id, cardType, atk, def, level);
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
        if (CardPlayMode.isFaceDown(playMode)) {
            //TODO: Will need additional logic here to determine if monster or spell trap, since spell trap are vertical
//            faceDown.setRotation(new Quaternion(Vector3.X, -90).mulLeft(new Quaternion(Vector3.Y, 90)));
            faceDown.setRotationX(-90);
            if (CardPlayMode.isFaceDownDefense(playMode)) {
                faceDown.setRotation(new Quaternion(Vector3.Y, 90).mul(faceDown.getRotation()));
            }
            which = faceDown;
        }
        else {
//            decal.setRotation(new Quaternion(Vector3.X, -90));
            decal.setRotationX(-90);
            which = decal;
            if (CardPlayMode.isDefenseMode(this)) {
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

    public boolean isMonster() {
        return (cardType & CardType.MONSTER) != 0;
    }

    public boolean isSpell() {
        return (cardType & CardType.SPELL) != 0;
    }

    public boolean isTrap() {
        return (cardType & CardType.TRAP) != 0;
    }

    public boolean canAttack() {
        return CardPlayMode.isAttackMode(playMode) && attacksThisTurn < maximumNumberOfAttacks;
    }
}
