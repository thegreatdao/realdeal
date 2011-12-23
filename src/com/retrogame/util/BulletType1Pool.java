package com.retrogame.util;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.pool.GenericPool;


public class BulletType1Pool extends GenericPool<Sprite> {

	private TextureRegion textureRegion;
	private Engine engine;

	public BulletType1Pool(TextureRegion textureRegion, Engine engine) {
		this.textureRegion = textureRegion;
		this.engine = engine;
	}
	
	@Override
	protected Sprite onAllocatePoolItem() {
		return new Sprite(Integer.MAX_VALUE, Integer.MAX_VALUE, textureRegion.deepCopy()) {

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				if(getY() < -100) {
					recyclePoolItem(this);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
			
		};

	}

	@Override
	protected void onHandleRecycleItem(final Sprite pItem) {
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
