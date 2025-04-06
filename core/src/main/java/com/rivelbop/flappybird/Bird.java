package com.rivelbop.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Bird implements Disposable {
    /* Movement */
    private final float
        GRAVITY = -900f, // Constant downward acceleration
        FLAP_FORCE = 450f, // Value to set velocity to when the bird flaps
        MAX_DEG = 25f, // The maximum upwards angle the bird can reach when flapped
        ROT_ACCEL = -200f; // The acceleration applied to the rotation of the bird when falling
    private float
        velocity, // Keeps track of the bird's current velocity to apply
        rotVel; // Keeps track of the bird's current rotation velocity to apply

    // Flap sound effect
    private final Sound FLAP_SOUND = Gdx.audio.newSound(Gdx.files.internal("flap.ogg"));

    /* Bird Visuals */
    private final Animation<Texture> ANIMATION = new Animation<>(
        0.167f, // Change the frame every 1/6th of a second (each frame below is shown twice every second)
        new Array<>(new Texture[]{
            new Texture("bird_0.png"),
            new Texture("bird_1.png"),
            new Texture("bird_2.png")
        }), Animation.PlayMode.LOOP // Continuously loops the animation frames
    );
    private float stateTime = 0f; // Keeps track of the animation's state time to display the appropriate frame
    // Stores the position and texture of the bird
    public final Sprite SPRITE = new Sprite(ANIMATION.getKeyFrame(stateTime));

    public Bird(float x, float y) {
        SPRITE.setScale(2f); // The texture is too small, double the size of the sprite
        SPRITE.setCenter(x, y);
    }

    public void update() {
        // Flap when the player presses SPACE or LEFT_MOUSE
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            velocity = FLAP_FORCE;
            FLAP_SOUND.play();
        }

        // Apply gravity to the sprite
        float delta = Gdx.graphics.getDeltaTime();
        velocity += GRAVITY * delta;
        SPRITE.translateY(velocity * delta);

        if (SPRITE.getY() + SPRITE.getHeight() > FlappyBird.HEIGHT) { // If the bird goes off the top of the screen
            SPRITE.setY(FlappyBird.HEIGHT - SPRITE.getHeight()); // Move the bird back in bounds (right under top)
        }

        if (velocity > 0f) { // The bird is not falling
            SPRITE.setRotation(MAX_DEG); // Snap the bird's rotation to the max upward degree
            rotVel = 0f; // Reset the rotation velocity
            stateTime += delta; // Update the animation's state time
        } else if (velocity < 0f) { // The bird is falling
            if (SPRITE.getRotation() > -90f) { // The bird has not entirely rotated to face directly down
                rotVel += ROT_ACCEL * delta; // Add the rotation acceleration to the current rotation velocity
                SPRITE.rotate(rotVel * delta); // Rotate the bird using the rotation velocity
            } else {
                SPRITE.setRotation(-90f); // Just for a perfect 90 degrees! ;)
            }

            if (SPRITE.getRotation() > -45f) { // Update the animation until the bird rotates below -45 deg
                stateTime += delta;
            } else {
                // If the bird is nearly directly facing down
                // Set the frame to the mid-wing texture ("bird_1.png")
                // This happens to be the second frame in the animation and can be called with the frame duration
                stateTime = ANIMATION.getFrameDuration();
            }
        }
        // Update the sprite's texture to the frame provided by the animation and state time
        SPRITE.setTexture(ANIMATION.getKeyFrame(stateTime));
    }

    @Override
    public void dispose() {
        for (Texture t : ANIMATION.getKeyFrames()) {
            t.dispose(); // Dispose of each texture of the animation
        }
        FLAP_SOUND.dispose();
    }
}
