package com.rivelbop.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * A Flappy Bird clone.
 * Used to teach libGDX game development.
 *
 * @author David Jerzak
 * @see <a href="https://www.udemy.com/user/david-jerzak/">Udemy</a>
 */
public class FlappyBird extends ApplicationAdapter {
    /* Window */
    // Create a 9/16 aspect ratio (similar to a mobile device)
    public static int WIDTH = 480, HEIGHT = WIDTH * 16/9;
    // Keeps the screen within a certain resolution (fits within black bars when necessary)
    private final FitViewport VIEWPORT = new FitViewport(WIDTH, HEIGHT);

    /* Visuals */
    // Renderer that is optimized and used to draw textures/sprites
    private SpriteBatch batch;

    /* Game Elements */
    private Bird bird;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Create the bird on the left side of the screen and above the half-way y-pos to avoid logo sprite obstruction
        bird = new Bird(WIDTH / 4f, HEIGHT / 2f + 100f);
    }

    @Override
    public void render() {
        /* Clear Screen */
        ScreenUtils.clear(Color.BLACK);

        /* Update Logic */
        bird.update();

        /* Render */
        // Apply the viewport to the camera and set the viewport to use for rendering
        VIEWPORT.apply(true);
        // Project textures to the viewport's camera
        batch.setProjectionMatrix(VIEWPORT.getCamera().combined);
        // Draw textures, sprites, etc.
        batch.begin();
        // Use the sprite to draw the bird
        bird.SPRITE.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Set viewport bounds using the specified screen size and center the camera
        VIEWPORT.update(width, height, true);
    }

    @Override
    public void dispose() {
        bird.dispose();
        batch.dispose();
    }
}
