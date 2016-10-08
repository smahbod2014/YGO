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

    public static final Vector2 SIZE_IN_HAND_NEAR = new Vector2(128 * .75f, 128).scl(.9f);
    public static final float THICKNESS = 0.0125f;
    public static TextureRegion FACE_DOWN_CARD_TEXTURE;

    public Texture image;
    public Decal decal, faceDown;
    public boolean isHovering;
    public int playMode;
    public String id;
    public Location location = Location.HAND;
    public Vector2 positionInHand = new Vector2();
    public CardType cardType;

    /**
     * @param filename The filename of the card's image without the path or extension
     */
    public Card(String filename, CardType cardType) {
        image = new Texture("cards/" + filename + ".jpg");
        decal = Decal.newDecal(new TextureRegion(image), true);
        faceDown = Decal.newDecal(FACE_DOWN_CARD_TEXTURE, true);
        id = filename;
        this.cardType = cardType;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Card copy() {
        return new Card(id, cardType);
    }

    public boolean contains(Vector2 p) {
        Rectangle r = new Rectangle(positionInHand.x, positionInHand.y, SIZE_IN_HAND_NEAR.x, SIZE_IN_HAND_NEAR.y);
        if (r.contains(p)) {
            return true;
        }
//        return r.contains(p);
        return false;
    }

    public void drawOnField(DecalBatch db, float x, float z, float width, float height, PlayerType p) {
        drawOnField(db, x, 0, z, width, height, p);
    }

    public void drawOnField(DecalBatch db, float x, float y, float z, float width, float height, PlayerType p) {
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
        }
        which.setPosition(x + width / 2, y, z - height / 2);
        if (p == PlayerType.OPPONENT_PLAYER) {
            which.setRotation(new Quaternion(Vector3.Y, 180).mul(which.getRotation()));
        }
        which.setWidth(width);
        which.setHeight(height);
        db.add(which);
    }

    public void draw(SpriteBatch sb) {
        if (location == Location.HAND) {
            float y = positionInHand.y;
            if (isHovering)
                y += 30;
            sb.draw(image, positionInHand.x, y, SIZE_IN_HAND_NEAR.x, SIZE_IN_HAND_NEAR.y);
        }
    }
}
