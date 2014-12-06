package com.game.sheepleap;

import java.util.Random;

public class GameSettings {

	public static boolean hippieMode = false;
	public static float effectVolume = .8f;
	public static float musicVolume = .8f;
	public static Random random = new Random();
	
	
	
	public static final short CATEGORYBIT_SHEEP = 1;
	public static final short CATEGORYBIT_BODYPART = 2;

	public static final short MASKBITS_SHEEP = CATEGORYBIT_SHEEP + CATEGORYBIT_BODYPART;
	public static final short MASKBITS_BODYPART = CATEGORYBIT_SHEEP;

}
