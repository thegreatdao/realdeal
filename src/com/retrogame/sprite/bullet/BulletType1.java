package com.retrogame.sprite.bullet;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class BulletType1 extends Sprite {

	private boolean attachedToScene;

	public BulletType1(float pX, float pY,
			TextureRegion pTextureRegion, boolean attachedToScene) {
		super(pX, pY, pTextureRegion);
		this.attachedToScene = attachedToScene;
	}

	public boolean isAttachedToScene() {
		return attachedToScene;
	}

	public void setAttachedToScene(boolean attachedToScene) {
		this.attachedToScene = attachedToScene;
	}
	
}
