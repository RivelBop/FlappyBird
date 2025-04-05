package com.rivelbop.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

/**
 * Handles a top and bottom pipe pair.
 */
public class PipeGroup implements Disposable {
    private final Pipe
        BOTTOM = new Pipe(false),
        TOP = new Pipe(true); // Since pipe texture is facing up, must be flipped to face down
    private final float SPEED = -250f;
    private boolean hasScored; // Flag that prevents additional scores if the pipe has already been scored on

    /**
     * Creates a pipe-group at position x.
     * Requires x-position since there are 2 pipe groups that reference the positions of each other.
     * Doesn't require y-position as param (since it is randomized).
     *
     * @param x The x-position of the pipes.
     */
    public PipeGroup(float x) {
        BOTTOM.SPRITE.setPosition(x, MathUtils.random(-FlappyBird.HEIGHT / 3f, FlappyBird.HEIGHT / 8f));
        updateTop();
    }

    /**
     * Updates the top pipes x and y positions according to the bottom pipes.
     * Sets the x-pos directly to the bottom pipe's, but the y-pos has an offset of 200f.
     */
    private void updateTop() {
        TOP.SPRITE.setPosition(BOTTOM.SPRITE.getX(), BOTTOM.SPRITE.getY() + BOTTOM.SPRITE.getHeight() * 2f + 200f);
    }

    /**
     * Moves the pipes left and checks if they are off-screen.
     *
     * @return If the pipes are off-screen.
     */
    public boolean update() {
        // Move both pipes left (using SPEED * deltaTime)
        BOTTOM.SPRITE.translateX(SPEED * Gdx.graphics.getDeltaTime());
        updateTop();

        // Checks if pipes are off-screen
        return BOTTOM.SPRITE.getX() < -BOTTOM.SPRITE.getBoundingRectangle().width;
    }

    /**
     * Checks if the bird overlaps with the top or bottom pipe.
     *
     * @param bird The bird to check collisions for.
     * @return Whether the bird overlaps the pipe group (any of the two pipes).
     */
    public boolean collides(Bird bird) {
        Rectangle pBox = bird.SPRITE.getBoundingRectangle();
        return pBox.overlaps(BOTTOM.SPRITE.getBoundingRectangle()) || pBox.overlaps(TOP.SPRITE.getBoundingRectangle());
    }

    /**
     * Checks to see if the bird passes the pipe to determine if the pipe is scored.
     * This will only return true once (after the initial pipe pass) to avoid constant scoring.
     *
     * @param bird The bird to check position.
     * @return Whether the bird has passed the pipe and scored.
     */
    public boolean hasScored(Bird bird) {
        if (!hasScored && bird.SPRITE.getX() > getX() + BOTTOM.SPRITE.getWidth() / 2f) {
            hasScored = true;
            return true;
        }
        return false;
    }

    /**
     * Draws both the top and bottom pipe to a {@link SpriteBatch}.
     *
     * @param batch The batch to draw to.
     */
    public void render(SpriteBatch batch) {
        BOTTOM.SPRITE.draw(batch);
        TOP.SPRITE.draw(batch);
    }

    /**
     * Retrieves the x-position of the bottom pipe.
     * We only need to track the x-position of one pipe since both are placed at the same x-pos.
     *
     * @return The x-pos of the pipes.
     */
    public float getX() {
        return BOTTOM.SPRITE.getX();
    }

    @Override
    public void dispose() {
        TOP.dispose();
        BOTTOM.dispose();
    }
}
