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

import com.rs.utilities.LogUtility;
import com.rs.utilities.LogUtility.LogType;
import com.rs.utilities.TextUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.SneakyThrows;

public class Censor {

	private final static ObjectArrayList<String> censoredWords = new ObjectArrayList<String>();
	private final static String PACKED_PATH = "data/packedCensoredWords.e";
	private final static String UNPACKED_PATH = "data/unpackedCensoredWords.txt";

	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedCensoredWords();
		else
			loadUnpackedCensoredWords();
	}

	public static String getFilteredMessage(String message) {
		message = message.toLowerCase();
		for (String word : censoredWords) {
			if (message.contains(word)) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < word.length(); i++)
					sb.append("*");
				message = message.replace(word, sb.toString());
			}
		}
		return TextUtils.fixChatMessage(message);
	}

	@SneakyThrows(Throwable.class)
	private static void loadPackedCensoredWords() {
		RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
		FileChannel channel = in.getChannel();
		ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
		while (buffer.hasRemaining())
			censoredWords.add(readAlexString(buffer));
		channel.close();
		in.close();
	}

	private static void loadUnpackedCensoredWords() {
		LogUtility.log(LogType.INFO, "Packing censored words...");
		try {
			BufferedReader in = new BufferedReader(new FileReader(UNPACKED_PATH));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//") || line.startsWith("*"))
					continue;
				writeAlexString(out, line);
				censoredWords.add(line);
			}
			in.close();
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String readAlexString(ByteBuffer buffer) {
		int count = buffer.get() & 0xff;
		byte[] bytes = new byte[count];
		buffer.get(bytes, 0, count);
		return new String(bytes);
	}

	public static void writeAlexString(DataOutputStream out, String string) throws IOException {
		byte[] bytes = string.getBytes();
		out.writeByte(bytes.length);
		out.write(bytes);
	}
}
