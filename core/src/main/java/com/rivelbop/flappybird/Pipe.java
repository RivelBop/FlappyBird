package com.rivelbop.flappybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

public class Pipe implements Disposable {
    public final Sprite SPRITE = new Sprite(new Texture("pipe.png"));

    public Pipe(boolean isFlipped) {
        SPRITE.setScale(2f); // The texture is too small, double the size of the sprite
        SPRITE.setFlip(false, isFlipped); // Flip the pipe vertically (if prompted)
    }

    @Override
    public void dispose() {
        SPRITE.getTexture().dispose(); // Dispose of the texture created for the sprite
    }
}
