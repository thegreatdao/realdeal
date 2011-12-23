package com.retrogame.util;

import java.util.Set;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.pool.GenericPool;

import com.retrogame.sprite.bullet.BaseBulletSprite;


public class BulletType1Pool extends GenericPool<BaseBulletSprite> {

	private TextureRegion textureRegion;
	private Engine engine;
	private Set<RectangularShape> targets;

	public BulletType1Pool(TextureRegion textureRegion, Engine engine, Set<RectangularShape> targets) {
		this.textureRegion = textureRegion;
		this.engine = engine;
		this.targets = targets;
	}
	
	@Override
	protected BaseBulletSprite onAllocatePoolItem() {
		return new BaseBulletSprite(Integer.MAX_VALUE, Integer.MAX_VALUE, textureRegion, targets) {

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				if(getY() < -100) {
					recyclePoolItem(this);
				} else {
					for(RectangularShape target : targets) {
						if(target.collidesWith(this)) {
							recyclePoolItem(this);
						}
					}
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
			
		};

	}

	@Override
	protected void onHandleRecycleItem(final BaseBulletSprite pItem) {
		pItem.clearEntityModifiers();
		pItem.clearUpdateHandlers();
		pItem.reset();
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				pItem.detachSelf();
			}
		});
		super.onHandleRecycleItem(pItem);
	}
	
}
