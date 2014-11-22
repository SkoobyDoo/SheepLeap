package com.game.sheepleap.entities;

public interface ThoughtfulEntity {

	// allows a class to perform regular logic calculations
	// time elapsed is the amount of time since the last game tick.
	public void think(float timeElapsed);
}
