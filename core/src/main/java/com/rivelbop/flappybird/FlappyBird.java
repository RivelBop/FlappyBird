package com.rivelbop.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
    // Flappy Bird Font (used to display score)
    private BitmapFont font;
    // Used to render scores using the BitmapFont
    private GlyphLayout scoreLayout, highScoreLayout;
    // City backdrop
    private Sprite background;

    /* Game Elements */
    private Sound
        scoreSound, // Sound that plays when player scores
        hitSound, // Plays when the bird dies
        dieSound; // Plays when the bird dies
    private Ground ground;
    private Bird bird;
    private PipeGroup pipes1, pipes2;

    /* Menus */
    private Sprite
        start, // Displayed at the start of the game
        gameOver; // Displayed when player loses

    /* Score */
    private Preferences saveData; // Used to access save data
    private int
        highScore,
        score; // Keeps track of the player's score

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Load font through generator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        font = generator.generateFont(parameter);
        generator.dispose(); // After font generation, generator is no longer necessary

        scoreLayout = new GlyphLayout();
        highScoreLayout = new GlyphLayout();

        scoreSound = Gdx.audio.newSound(Gdx.files.internal("score.ogg"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.ogg"));
        dieSound = Gdx.audio.newSound(Gdx.files.internal("die.ogg"));

        start = new Sprite(new Texture("start.png"));
        start.setScale(1.75f);
        start.setAlpha(0.75f);
        start.setCenter(WIDTH / 2f, HEIGHT / 2f);

        gameOver = new Sprite(new Texture("gameover.png"));
        gameOver.setScale(1.75f);
        gameOver.setAlpha(0.75f);
        gameOver.setCenter(WIDTH / 2f, HEIGHT / 2f);

        ground = new Ground();
        background = new Sprite(new Texture("background.png"));
        background.setScale(2.35f); // Fills up the whole screen
        background.setY(ground.topY()); // Set the background on top of the ground sprite

        // Create the bird on the left side of the screen and above the half-way y-pos to avoid logo sprite obstruction
        bird = new Bird(WIDTH / 4f, HEIGHT / 2f + 100f);

        // Create pipes off-screen (add 30f to shift entirely off-screen)
        pipes1 = new PipeGroup(WIDTH + 30f);
        pipes2 = new PipeGroup(WIDTH * 2f + 30f);

        saveData = Gdx.app.getPreferences("FlappyBird"); // Get save file
        highScore = saveData.getInteger("highScore", 0); // Get high score
        score = 0; // Reset the score
    }

    @Override
    public void render() {
        /* Clear Screen */
        ScreenUtils.clear(Color.BLACK);

        /* Update Logic */
        // Start/Restart game
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (bird.isDead) { // Restart game
                dispose();
                create();
            } else { // Start game
                start.setAlpha(0f);
            }
        }

        // If the bird is not dead and touches the ground or collides with any pipe
        if (!bird.isDead && (bird.SPRITE.getY() <= ground.topY() || pipes1.collides(bird) || pipes2.collides(bird))) {
            if (score > highScore) { // If the current score is greater than the saved high score
                saveData.putInteger("highScore", score); // Save the score as the high score
                highScore = score; // Update the high score count (to display it)
            }
            bird.isDead = true; // Ensures the sounds don't play continuously
            hitSound.play();
            dieSound.play();
        }

        if (!bird.isDead) {
            ground.update(); // Update the ground movement when the bird isn't dead
        }

        if (start.getColor().a == 0f) { // The game has started
            // Update the bird's movement and animations when he doesn't hit the ground
            if (bird.SPRITE.getY() > ground.topY()) {
                bird.update();
            }

            if (!bird.isDead) {
                if (pipes1.update()) { // Update first pipe group until it reaches the side of the screen
                    pipes1.dispose(); // Dispose of the textures stored in the group
                    pipes1 = new PipeGroup(WIDTH * 2f - (WIDTH - pipes2.getX())); // Create a new group on the right
                }
                if (pipes2.update()) {
                    pipes2.dispose();
                    pipes2 = new PipeGroup(WIDTH * 2f - (WIDTH - pipes1.getX()));
                }
            }
        } else {
            bird.updateAnimation(); // Update animations (not movement) if the game hasn't started yet
        }

        // If the player scores, increase score count and play the score sound effect
        if (pipes1.hasScored(bird) || pipes2.hasScored(bird)) {
            score++;
            scoreSound.play();
        }

        font.setColor(Color.WHITE);
        scoreLayout.setText(font, Integer.toString(score)); // Prepare the glyph for score rendering

        font.setColor(Color.GREEN);
        highScoreLayout.setText(font, Integer.toString(highScore));

        /* Render */
        // Apply the viewport to the camera and set the viewport to use for rendering
        VIEWPORT.apply(true);
        // Project textures to the viewport's camera
        batch.setProjectionMatrix(VIEWPORT.getCamera().combined);
        // Draw textures, sprites, etc.
        batch.begin();

        background.draw(batch);

        // Use the render methods to draw both pipes (top and bottom) at once per PipeGroup
        pipes1.render(batch);
        pipes2.render(batch);

        // Use the sprite to draw the bird
        bird.SPRITE.draw(batch);

        ground.render(batch);

        // Render the score in the center-top of the screen
        font.draw(batch, scoreLayout, WIDTH / 2f - scoreLayout.width / 2f, HEIGHT - 50f);

        // Render the high score under the score
        font.draw(batch, highScoreLayout,
            WIDTH / 2f - highScoreLayout.width / 2f, HEIGHT - scoreLayout.height - 75f);

        // Render both the start and game over menus
        start.draw(batch);
        if (bird.isDead) {
            gameOver.draw(batch);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Set viewport bounds using the specified screen size and center the camera
        VIEWPORT.update(width, height, true);
    }

    @Override
    public void dispose() {
        saveData.flush(); // Must be flushed to finish saving data

        pipes2.dispose();
        pipes1.dispose();
        bird.dispose();

        ground.dispose();
        background.getTexture().dispose();

        start.getTexture().dispose();
        gameOver.getTexture().dispose();

        scoreSound.dispose();
        hitSound.dispose();
        dieSound.dispose();

        font.dispose();
        batch.dispose();
    }
}
