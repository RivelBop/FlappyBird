package com.rivelbop.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

public class Bird implements Disposable {
    /* Movement */
    private final float
        GRAVITY = -900f, // Constant downward acceleration
        FLAP_FORCE = 450f; // Value to set velocity to when the bird flaps
    private float velocity; // Keeps track of the bird's current velocity to apply

    // Stores the position and texture of the bird
    public final Sprite SPRITE = new Sprite(new Texture("bird.png"));

    public Bird(float x, float y) {
        SPRITE.setScale(2f); // The texture is too small, double the size of the sprite
        SPRITE.setCenter(x, y);
    }

    public void update() {
        // Flap when the player presses SPACE or LEFT_MOUSE
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            velocity = FLAP_FORCE;
        }

        // Apply gravity to the sprite
        float delta = Gdx.graphics.getDeltaTime();
        velocity += GRAVITY * delta;
        SPRITE.translateY(velocity * delta);
    }

    @Override
    public void dispose() {
        // Dispose of the texture created for the sprite
        SPRITE.getTexture().dispose();
    }
}
