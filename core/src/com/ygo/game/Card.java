package com.ygo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Card {

    Texture image;

    /**
     *
     * @param filename The filename of the card's image without the path or extension
     */
    public Card(String filename) {
        image = new Texture("cards/" + filename + ".jpg");
//        image = new Texture(Gdx.files.internal("badlogic.jpg"));
    }

    public void draw(SpriteBatch sb, float x, float y, float width, float height) {
        sb.draw(image, x, y, width, height);
    }

}
