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

public class Card {

    public static final Vector2 SIZE_IN_HAND_NEAR = new Vector2(128 * .75f, 128).scl(.9f);
//    public static Sprite FACE_DOWN_CARD;
    public static TextureRegion FACE_DOWN_CARD_TEXTURE;

    Texture image;
    Decal decal, faceDown;
    boolean isHovering;
    int playMode;
    String id;
    Location location = Location.HAND;
    Vector2 positionInHand = new Vector2();
    CardType cardType;

    /**
     *
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

    public void drawOnField(DecalBatch db, float x, float z, float width, float height) {
        if (CardPlayMode.isFaceDown(playMode)) {
            //TODO: Will need additional logic here to determine if monster or spell trap, since spell trap are vertical
            faceDown.setRotation(new Quaternion(Vector3.X, -90).mulLeft(new Quaternion(Vector3.Y, 90)));
            faceDown.setPosition(x + width / 2, 0, z - height / 2);
            faceDown.setWidth(width);
            faceDown.setHeight(height);
            db.add(faceDown);
        }
        else {
            decal.setRotation(new Quaternion(Vector3.X, -90));
            decal.setPosition(x + width / 2, 0, z - height / 2);
            decal.setWidth(width);
            decal.setHeight(height);
            db.add(decal);
        }
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
