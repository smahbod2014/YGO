package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ygo.game.Types.CardPlayMode;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Location;

public class Card {

    public static final Vector2 SIZE_IN_HAND = new Vector2(Utils.sx(128 * .75f), Utils.sy(128));
    public static Sprite FACE_DOWN_CARD;

    Texture image;
    boolean isHovering;
    int playMode;
    Location location = Location.HAND;
    Vector2 positionInHand = new Vector2();

    CardType cardType;

    /**
     *
     * @param filename The filename of the card's image without the path or extension
     */
    public Card(String filename, CardType cardType) {
        image = new Texture("cards/" + filename + ".jpg");
        this.cardType = cardType;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean contains(Vector2 p) {
        Rectangle r = new Rectangle(positionInHand.x, positionInHand.y, SIZE_IN_HAND.x, SIZE_IN_HAND.y);
        return r.contains(p);
    }

    public void draw(SpriteBatch sb, float x, float y, float width, float height) {
        if (CardPlayMode.isFaceDown(playMode)) {
            //TODO: Will need additional logic here to determine if monster or spell trap, since spell trap are vertical
            FACE_DOWN_CARD.setBounds(x, y, width, height);
            FACE_DOWN_CARD.setOriginCenter();
            FACE_DOWN_CARD.setRotation(90);
            FACE_DOWN_CARD.draw(sb);
        }
        else {
            sb.draw(image, x, y, width, height);
        }
    }

    public void draw(SpriteBatch sb) {
        if (location == Location.HAND) {
            float y = positionInHand.y;
            if (isHovering)
                y += Utils.sy(30);
            sb.draw(image, positionInHand.x, y, SIZE_IN_HAND.x, SIZE_IN_HAND.y);
        }
    }
}
