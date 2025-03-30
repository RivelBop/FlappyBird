package com.rivelbop.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class FlappyBird extends ApplicationAdapter {
    // Create a 9/16 aspect ratio (similar to a mobile device)
    public static int WIDTH = 480, HEIGHT = WIDTH * 16/9;
    // Keeps the screen within a certain resolution (fits within black bars when necessary)
    private final FitViewport VIEWPORT = new FitViewport(WIDTH, HEIGHT);

    // Renderer that is optimized and used to render textures/sprites
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        // Apply the viewport to the camera and set the viewport to use for rendering
        VIEWPORT.apply(true);
        // Project textures to the viewport's camera
        batch.setProjectionMatrix(VIEWPORT.getCamera().combined);
        // Draw textures, sprites, etc.
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Set viewport bounds using the specified screen size and centers the camera
        VIEWPORT.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
