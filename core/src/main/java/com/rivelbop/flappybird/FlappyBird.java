package com.rivelbop.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
    public static int WIDTH = 480, HEIGHT = WIDTH * 16 / 9;
    // Keeps the screen within a certain resolution (fits within black bars when necessary)
    private final FitViewport VIEWPORT = new FitViewport(WIDTH, HEIGHT);

    /* Visuals */
    // Renderer that is optimized and used to draw textures/sprites
    private SpriteBatch batch;

    /* Game Elements */
    private Sound scoreSound; // Sound that plays when player scores
    private Bird bird;
    private PipeGroup pipes1, pipes2;

    @Override
    public void create() {
        batch = new SpriteBatch();
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("score.ogg"));

        // Create the bird on the left side of the screen and above the half-way y-pos to avoid logo sprite obstruction
        bird = new Bird(WIDTH / 4f, HEIGHT / 2f + 100f);

        // Create pipes off-screen
        pipes1 = new PipeGroup(WIDTH);
        pipes2 = new PipeGroup(WIDTH * 2f);
    }

    @Override
    public void render() {
        /* Clear Screen */
        ScreenUtils.clear(Color.BLACK);

        /* Update Logic */
        // If the bird touches the bottom of the screen or collides with any pipe
        if (bird.SPRITE.getY() <= 0f || pipes1.collides(bird) || pipes2.collides(bird)) {
            // Restart the game
            dispose();
            create();
        }

        bird.update();
        if (pipes1.update()) { // Update first pipe group until it reaches the side of the screen
            pipes1.dispose(); // Dispose of the textures stored in the group
            pipes1 = new PipeGroup(WIDTH * 2f - (WIDTH - pipes2.getX())); // Create a new group on the right
        }
        if (pipes2.update()) {
            pipes2.dispose();
            pipes2 = new PipeGroup(WIDTH * 2f - (WIDTH - pipes1.getX()));
        }

        // If the player scores, play the score sound effect
        if (pipes1.hasScored(bird) || pipes2.hasScored(bird)) {
            scoreSound.play();
        }

        /* Render */
        // Apply the viewport to the camera and set the viewport to use for rendering
        VIEWPORT.apply(true);
        // Project textures to the viewport's camera
        batch.setProjectionMatrix(VIEWPORT.getCamera().combined);
        // Draw textures, sprites, etc.
        batch.begin();
        // Use the render methods to draw both pipes (top and bottom) at once per PipeGroup
        pipes1.render(batch);
        pipes2.render(batch);
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
        pipes2.dispose();
        pipes1.dispose();
        bird.dispose();
        scoreSound.dispose();
        batch.dispose();
    }
}
