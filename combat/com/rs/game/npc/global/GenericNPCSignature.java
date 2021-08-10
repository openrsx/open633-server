package com.rs.game.npc.global;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GenericNPCSignature {

	public int[] npcId();
	
	public byte mapAreaNameHash() default -1;
	
	public boolean canBeAttackFromOutOfArea();
	
	public boolean isSpawned();
}