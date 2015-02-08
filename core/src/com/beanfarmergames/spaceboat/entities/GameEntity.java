package com.beanfarmergames.spaceboat.entities;

import com.beanfarmergames.spaceboat.boat.CollisionRecord;

public interface GameEntity {
    enum EntityType {
        BOAT,
        COW
    };
    
    public EntityType getEntityType();
    
    public boolean canTractor();
    public void hitWithLaser(CollisionRecord cr, long miliseconds);
}
