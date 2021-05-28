package com.rs;

import java.math.BigInteger;

import com.google.common.collect.ImmutableMap;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Rights;

public final class Settings {

	/**
	 * General client and server settings.
	 */
	public static final String SERVER_NAME =  Config.get().getString("server_name");
	public static final int RECEIVE_DATA_LIMIT = 7500;
	public static final int PACKET_SIZE_LIMIT = 7500;
	public static final int CLIENT_REVISION = 633;
	public static final long CONNECTION_TIMEOUT = Config.get().getInteger("connection_timeout");

	/**
	 * Launching settings
	 */
	public static boolean DEBUG = true;
	public static boolean HOSTED;
	public static boolean ECONOMY = false;
	public static boolean ALLOW_MASTER_PASSWORD = true;
	public static String MASTER_PASSWORD = "localhostmaster";

	/**
	 * Player settings
	 */
	public static final WorldTile START_PLAYER_LOCATION = new WorldTile(3222, 3222, 0);
	public static final int COMBAT_XP_RATE = Config.get().getInteger("combat_exp_rate");
	public static final int XP_RATE = Config.get().getInteger("exp_rate");
	public static final int LAMP_XP_RATE = Config.get().getInteger("lamp_exp_rate");
	public static final int DROP_RATE = Config.get().getInteger("drop_rate");
	public static final int DROP_QUANTITY_RATE = Config.get().getInteger("drop_quantity_rate");
	public static final int DEGRADE_GEAR_RATE = Config.get().getInteger("degrade_rate");
	public static boolean XP_BONUS_ENABLED = Config.get().getBoolean("bonus_exp_enabled");
	
	/**
	 * The maximum amount of drops that can be rolled from the dynamic drop table.
	 */
	public static final byte DROP_THRESHOLD = 2;

	/**
	 * The attempted looped of randomized rare mob drops.
	 */
	public static final byte DROP_RARE_ATTEMPTS = 5;
	
	/**
	 * The time in seconds that has to be spent in a region before {@link Mob}s stop
	 * acting aggressive towards a specific {@link Player}.
	 */
	public static final short TOLERANCE_SECONDS = 600;
	
	/**
	 * An immutable map of Staff members
	 */
	public static final ImmutableMap<String, Rights> STAFF = ImmutableMap.of(
			"Zed", Rights.ADMINISTRATOR,
			"Jawarrior1", Rights.ADMINISTRATOR
	);

	/**
	 * World settings
	 */
	public static final int WORLD_CYCLE_TIME = 600; // the speed of world in ms

	/**
	 * Music & Emote settings
	 */
	public static final int AIR_GUITAR_MUSICS_COUNT = 50;

	/**
	 * Memory settings
	 */
	public static final int LOCAL_PLAYERS_LIMIT = 250;
	public static final int PLAYERS_LIMIT = 2048;
	public static final int NPCS_LIMIT = Short.MAX_VALUE;
	public static final int LOCAL_NPCS_LIMIT = 127;
	public static final int MIN_FREE_MEM_ALLOWED = 30000000;

	/**
	 * Game constants
	 */
	public static final int[] MAP_SIZES = { 104, 120, 136, 168, 72 };

	public static final int[] GRAB_SERVER_KEYS = { 100, 79328, 55571, 46770,
			24563, 299978, 44375, 0, 4173, 2820, 99838, 617461, 155159, 282434,
			329958, 682317, 18859, 19013, 16183, 1244, 6250, 524, 119, 739155,
			813330, 3621, 2908 };

	// an exeption(grab server has his own keyset unlike rest of client)
	public static final BigInteger GRAB_SERVER_PRIVATE_EXPONENT = new BigInteger(
			"85841718464006470839454836619781897739687740809318231193831996660380025422889676223278733529619572421474466540424432365116201466262036779260116487579588025309092277884355330746244882937851596698304162660093117809460890167161229594796675127688779314631686136383237667641862930930283410062092886864440881014337");
	public static final BigInteger GRAB_SERVER_MODULUS = new BigInteger(
			"120684072056280935288427827946427111553241708199336899728637540010539851684827542274005027444025182722373693874630942678750225147898041539436465038752862996523582623683050478903900622900745629235369980114857562631625233381072331798032418279261790337275058300738584974228751698542644837745940989177562329966303");

	public static final BigInteger PRIVATE_EXPONENT = new BigInteger(
			"72097355254232856447691049913560861199871800553034733055741658055384003364250497219347734593899555489356266111078966532473452495715069155559968676764261742228476044257420193568926663071665046174428073288830191026953446568088808917389435275071415275652574533602125129106144025101414104744266670316697396691017");
	public static final BigInteger MODULUS = new BigInteger(
			"113936108878412835789161783853416560016782768509180808282272938775908500602418191899505497385652508213111369682953925143804409254941488925946653496879733372392766486773043079697715731681861313487249634133583720830388725199461616223203479888577328710259826756810447716575537141200187251284798307012938761310363");
}