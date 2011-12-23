package com.retrogame;

import java.util.HashSet;
import java.util.Set;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.MathUtils;

import com.retrogame.util.BulletType1Pool;

public class MainActivity extends BaseGameActivity implements
		IOnSceneTouchListener {

	private static final int ACTIVE_BULLETS_COUNT = 80;
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;

	private Camera camera;

	private BitmapTextureAtlas bitmapTextureAtlas;

	private TiledTextureRegion planeTextureRegion;
	private TextureRegion circleTextureRegion;
	private AnimatedSprite plane;
	private PhysicsHandler physicsHandler;
	private float touchAreaX;
	private float touchAreaY;
	private Sprite planePositionCircleSprite;
	private Sprite planeCenterCircleSprite;
	private Line topBorder;
	private Line bottomBorder;
	private Line leftBorder;
	private Line rightBorder;
	private float planeHalfWidth;
	private float planeHalfHeight;
	private float planePositionCircleSpriteHalfWidth;
	private float planePositionCircleSpriteHalfHeight;
	private TextureRegion bulletType1TextureRegion;
	private BulletType1Pool bulletType1Pool;
	private TiledTextureRegion enemyTextureRegion;
	private Set<RectangularShape> targets = new HashSet<RectangularShape>();

	@Override
	public Engine onLoadEngine() {
		this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				this.camera));
	}

	@Override
	public void onLoadResources() {
		bitmapTextureAtlas = new BitmapTextureAtlas(512, 128,
				TextureOptions.BILINEAR);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		planeTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.bitmapTextureAtlas, this,
						"plane2.png", 0, 0, 3, 1);
		circleTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.bitmapTextureAtlas, this, "circle.png",
						275, 0);
		bulletType1TextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.bitmapTextureAtlas, this, "bullet1.png",
						275, 20);
		enemyTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.bitmapTextureAtlas, this, "enemy.png",
						287, 0, 2, 1);
		this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene();
		scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
		bulletType1Pool = new BulletType1Pool(bulletType1TextureRegion, mEngine, targets);
		bulletType1Pool.batchAllocatePoolItems(ACTIVE_BULLETS_COUNT);
		addBorders(scene);
		plane = new AnimatedSprite(CAMERA_WIDTH / 2 - 30,
				CAMERA_HEIGHT / 2 - 29, this.planeTextureRegion);
		plane.animate(50);
		planePositionCircleSprite = new Sprite(-100, -100, circleTextureRegion);
		planeHalfWidth = plane.getWidth() / 2;
		planeHalfHeight = plane.getHeight() / 2;
		planePositionCircleSpriteHalfWidth = planePositionCircleSprite
				.getWidth() / 2;
		planePositionCircleSpriteHalfHeight = planePositionCircleSprite
				.getHeight() / 2;
		planeCenterCircleSprite = new Sprite(planeHalfWidth
				- planePositionCircleSpriteHalfWidth, planeHalfHeight
				- planePositionCircleSpriteHalfHeight,
				circleTextureRegion.deepCopy());
		physicsHandler = new PhysicsHandler(plane);
		plane.registerUpdateHandler(physicsHandler);
		plane.attachChild(planeCenterCircleSprite);
		AnimatedSprite enemy = new AnimatedSprite(CAMERA_WIDTH / 2, 100, enemyTextureRegion);
		AnimatedSprite enemy2 = new AnimatedSprite(100, 100, enemyTextureRegion.deepCopy());
		AnimatedSprite enemy3 = new AnimatedSprite(400, 100, enemyTextureRegion.deepCopy());
		AnimatedSprite enemy4 = new AnimatedSprite(300, 100, enemyTextureRegion.deepCopy());
		targets.add(enemy);
		targets.add(enemy2);
		targets.add(enemy3);
		targets.add(enemy4);
		enemy.animate(500);
		enemy2.animate(600);
		enemy3.animate(700);
		enemy4.animate(800);
		scene.attachChild(enemy);
		scene.attachChild(enemy2);
		scene.attachChild(enemy3);
		scene.attachChild(enemy4);
		scene.attachChild(plane);
		scene.attachChild(planePositionCircleSprite);
		scene.setOnSceneTouchListener(this);		
		scene.registerUpdateHandler(new IUpdateHandler() {
			private long startShootingTimeMillis = System.currentTimeMillis();
			private long accumulatedTimeForShootingMillis = 0;

			@Override
			public void reset() {

			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				checkBorders();
				if (planeCenterCircleSprite
						.collidesWith(planePositionCircleSprite)) {
					physicsHandler.setVelocity(0, 0);
					float x = plane.getX();
					float y = plane.getY();
					if (x <= 0) {
						plane.setPosition(0, y);
					}
					if (x + plane.getWidth() >= CAMERA_WIDTH) {
						plane.setPosition(CAMERA_WIDTH - plane.getWidth(), y);
					}
					if (y <= 0) {
						plane.setPosition(x, 0);
					}
					if (y + plane.getHeight() >= CAMERA_HEIGHT) {
						plane.setPosition(x, CAMERA_HEIGHT - plane.getHeight());
					}
				}
				final long currentTimeMillis = System.currentTimeMillis();
				if (currentTimeMillis - startShootingTimeMillis >= accumulatedTimeForShootingMillis) {
					shoot(scene);
					startShootingTimeMillis = currentTimeMillis;
				}
			}

			private void shoot(final Scene scene) {
				if (bulletType1Pool.getUnrecycledCount() < ACTIVE_BULLETS_COUNT) {
					addLeftBullet(scene);
					addRightBullet(scene);
				}
			}

			private void addLeftBullet(final Scene scene) {
				Sprite bullet = bulletType1Pool.obtainPoolItem();
				bullet.setPosition(plane.getX() + planeHalfWidth - 14,
						plane.getY() - 3);
				PhysicsHandler physicsHandler = new PhysicsHandler(bullet);
				bullet.registerUpdateHandler(physicsHandler);
				physicsHandler.setVelocity(0, -1000);
					scene.attachChild(bullet);
			}
			
			private void addRightBullet(final Scene scene) {
				Sprite bullet = bulletType1Pool.obtainPoolItem();
				bullet.setPosition(plane.getX() + planeHalfWidth + 6,
						plane.getY() - 3);
				PhysicsHandler physicsHandler = new PhysicsHandler(bullet);
				bullet.registerUpdateHandler(physicsHandler);
				physicsHandler.setVelocity(0, -1000);
					scene.attachChild(bullet);
			}
		});
		return scene;
	}

	private void addBorders(Scene scene) {
		float lineThickness = 6;
		topBorder = new Line(0, -lineThickness, CAMERA_WIDTH, -lineThickness,
				lineThickness);
		bottomBorder = new Line(0, CAMERA_HEIGHT + 1, CAMERA_WIDTH,
				CAMERA_HEIGHT + 1, lineThickness);
		leftBorder = new Line(-lineThickness, 0, -lineThickness, CAMERA_HEIGHT,
				lineThickness);
		rightBorder = new Line(CAMERA_WIDTH + 1, 0, CAMERA_WIDTH + 1,
				CAMERA_HEIGHT, lineThickness);
		scene.attachChild(topBorder);
		scene.attachChild(rightBorder);
		scene.attachChild(bottomBorder);
		scene.attachChild(leftBorder);
	}

	@Override
	public void onLoadComplete() {

	}

	private void checkBorders() {
		float velocityX = physicsHandler.getVelocityX();
		float velocityY = physicsHandler.getVelocityY();
		if (topBorder.collidesWith(plane)) {
			physicsHandler.setVelocity(velocityX, 0);
			plane.setPosition(plane.getX(), 0);
		}
		if (rightBorder.collidesWith(plane)) {
			physicsHandler.setVelocity(0, velocityY);
			plane.setPosition(CAMERA_WIDTH - plane.getWidth(), plane.getY());
		}
		if (bottomBorder.collidesWith(plane)) {
			physicsHandler.setVelocity(velocityX, 0);
			plane.setPosition(plane.getX(), CAMERA_HEIGHT - plane.getHeight());
		}
		if (leftBorder.collidesWith(plane)) {
			physicsHandler.setVelocity(0, velocityY);
			plane.setPosition(0, plane.getY());
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent sceneTouchEvent) {
		final float VELOCITY = 600;
		touchAreaX = sceneTouchEvent.getX();
		touchAreaY = sceneTouchEvent.getY();
		if (touchAreaX - planeHalfWidth <= 0) {
			touchAreaX = planeHalfWidth;
		}
		if (touchAreaX + planeHalfWidth > CAMERA_WIDTH) {
			touchAreaX = CAMERA_WIDTH - planeHalfWidth;
		}
		if (touchAreaY - planeHalfHeight <= 0) {
			touchAreaY = planeHalfHeight;
		}
		if (touchAreaY + planeHalfHeight > CAMERA_HEIGHT) {
			touchAreaY = CAMERA_HEIGHT - planeHalfHeight;
		}
		float x = touchAreaX - (plane.getX() + planeHalfWidth);
		float y = touchAreaY - (plane.getY() + planeHalfHeight);
		float atan2 = MathUtils.atan2(y, x);
		physicsHandler.setVelocity(VELOCITY * (float) Math.cos(atan2), VELOCITY
				* (float) Math.sin(atan2));
		planePositionCircleSprite.setPosition(touchAreaX
				- planePositionCircleSpriteHalfWidth, touchAreaY
				- planePositionCircleSpriteHalfHeight);
		return true;
	}
}