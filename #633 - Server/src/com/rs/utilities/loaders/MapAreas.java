package com.rs.utilities.loaders;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.rs.game.WorldTile;
import com.rs.utilities.Logger;
import com.rs.utilities.RandomUtils;
import com.rs.utilities.Utility;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.SneakyThrows;
import lombok.Synchronized;

public final class MapAreas {

	private final static Object2ObjectArrayMap<Integer, int[]> mapAreas = new Object2ObjectArrayMap<Integer, int[]>();
	private final static String PACKED_PATH = "data/map/packedMapAreas.ma";
	private final static Object lock = new Object();

	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedMapAreas();
		else
			loadUnpackedMapAreas();
	}

	public static final boolean isAtArea(String areaName, WorldTile tile) {
		return isAtArea(Utility.getNameHash(areaName), tile);
	}

	public static final boolean isAtArea(int areaNameHash, WorldTile tile) {
		int[] coordsList = mapAreas.get(areaNameHash);
		if (coordsList == null)
			return false;
		int index = 0;
		while (index < coordsList.length) {
			if (tile.getPlane() == coordsList[index] && tile.getX() >= coordsList[index + 1]
					&& tile.getX() <= coordsList[index + 2] && tile.getY() >= coordsList[index + 3]
					&& tile.getY() <= coordsList[index + 4])
				return true;
			index += 5;
		}
		return false;
	}

	public static final void removeArea(int areaNameHash) {
		mapAreas.remove(areaNameHash);
	}

	public static final void addArea(int areaNameHash, int[] coordsList) {
		mapAreas.put(areaNameHash, coordsList);
	}

	@Synchronized("lock")
	public static final int getRandomAreaHash() {
		while (true) {
			long id = RandomUtils.random(Integer.MAX_VALUE) + RandomUtils.random(Integer.MAX_VALUE);
			id -= Integer.MIN_VALUE;
			if (id != -1 && !mapAreas.containsKey((int) id))
				return (int) id;
		}
	}

	@SneakyThrows(Throwable.class)
	private static void loadUnpackedMapAreas() {
		Logger.log("MapAreas", "Packing map areas...");
		BufferedReader in = new BufferedReader(new FileReader("data/map/unpackedMapAreas.txt"));
		DataOutputStream out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
		while (true) {
			String line = in.readLine();
			if (line == null)
				break;
			if (line.startsWith("//"))
				continue;
			String[] splitedLine = line.split(" - ", 2);
			String areaName = splitedLine[0];
			String[] splitedCoords = splitedLine[1].split(" ");
			int[] coordsList = new int[splitedCoords.length];
			if (coordsList.length < 5) {
				in.close();
				out.close();
				throw new RuntimeException("Invalid list for area line: " + line);
			}
			for (int i = 0; i < coordsList.length; i++)
				coordsList[i] = Integer.parseInt(splitedCoords[i]);
			int areaNameHash = Utility.getNameHash(areaName);
			if (mapAreas.containsKey(areaNameHash))
				continue;
			out.writeInt(areaNameHash);
			out.writeByte(coordsList.length);
			for (int i = 0; i < coordsList.length; i++)
				out.writeShort(coordsList[i]);
			mapAreas.put(areaNameHash, coordsList);
		}
		in.close();
		out.flush();
		out.close();
	}

	private static void loadPackedMapAreas() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int areaNameHash = buffer.getInt();
				int[] coordsList = new int[buffer.get() & 0xff];
				for (int i = 0; i < coordsList.length; i++)
					coordsList[i] = buffer.getShort() & 0xffff;
				mapAreas.put(areaNameHash, coordsList);
			}
			channel.close();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MapAreas() {

	}
}
