package com.rivelbop.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class Ground implements Disposable {
    private final Texture TEXTURE = new Texture("ground.png");
    private final Sprite // Both parts are used to loop the ground seamlessly
        SPRITE_0 = new Sprite(TEXTURE), // First part of the ground
        SPRITE_1 = new Sprite(TEXTURE); // Second part of the ground
    private final float SPEED = -250f; // The speed the ground moves

    public Ground() {
        float scale = FlappyBird.WIDTH / SPRITE_0.getWidth(); // Scale the ground parts to be the size of the game width
        SPRITE_0.setSize(SPRITE_0.getWidth() * scale, SPRITE_0.getHeight() * scale);
        SPRITE_0.setY(-SPRITE_0.getHeight() / 4f);

        SPRITE_1.setSize(SPRITE_1.getWidth() * scale, SPRITE_1.getHeight() * scale);
        SPRITE_1.setPosition(FlappyBird.WIDTH, -SPRITE_1.getHeight() / 4f);
    }

    public void update() {
        float speed = SPEED * Gdx.graphics.getDeltaTime();
        SPRITE_0.translateX(speed);
        SPRITE_1.translateX(speed);

        // Set the ground parts to be right before each other when they are completely off-screen
        if (SPRITE_0.getX() <= -FlappyBird.WIDTH) {
            SPRITE_0.setX(SPRITE_1.getX() + FlappyBird.WIDTH);
        } else if (SPRITE_1.getX() <= -FlappyBird.WIDTH) {
            SPRITE_1.setX(SPRITE_0.getX() + FlappyBird.WIDTH);
        }
    }

    public void render(SpriteBatch batch) {
        SPRITE_0.draw(batch);
        SPRITE_1.draw(batch);
    }

    /**
     * Helps position the background correctly.
     *
     * @return The y-pos of the top of the ground sprite.
     */
    public float topY() {
        return SPRITE_0.getY() + SPRITE_0.getHeight();
    }

    @Override
    public void dispose() {
        TEXTURE.dispose();
    }
}
