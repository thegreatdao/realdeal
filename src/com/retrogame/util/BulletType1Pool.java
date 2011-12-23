package com.retrogame.util;

import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.pool.GenericPool;

import com.retrogame.sprite.bullet.BulletType1;

public class BulletType1Pool extends GenericPool<BulletType1> {

	private TextureRegion textureRegion;

	public BulletType1Pool(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}
	
	@Override
	protected BulletType1 onAllocatePoolItem() {
		return new BulletType1(Integer.MAX_VALUE, Integer.MAX_VALUE, textureRegion.deepCopy(), false) {

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
	protected void onHandleRecycleItem(BulletType1 pItem) {
		pItem.clearEntityModifiers();
		pItem.clearUpdateHandlers();
		pItem.reset();
		super.onHandleRecycleItem(pItem);
	}
	
}
