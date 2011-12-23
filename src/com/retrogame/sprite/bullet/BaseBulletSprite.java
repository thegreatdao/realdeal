package com.retrogame.sprite.bullet;

import java.util.Set;

import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class BaseBulletSprite extends Sprite {

	private Set<RectangularShape> targets;
	private boolean attachedToScene;

	public BaseBulletSprite(float pX, float pY, TextureRegion pTextureRegion, Set<RectangularShape> targets) {
		super(pX, pY, pTextureRegion);
		this.targets = targets;
	}

	public synchronized void addTarget(RectangularShape target) {
		targets.add(target);
	}
	
	public synchronized void removeTarget(RectangularShape target) {
		targets.remove(target);
	}
	
	public synchronized void clearTargets() {
		targets.clear();
	}

	public boolean isAttachedToScene() {
		return attachedToScene;
	}

	public void setAttachedToScene(boolean attachedToScene) {
		this.attachedToScene = attachedToScene;
	}
	
}
