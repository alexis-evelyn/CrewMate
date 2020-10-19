package me.alexisevelyn.crewmate;

import me.alexisevelyn.crewmate.exceptions.InvalidBytesException;
import me.alexisevelyn.crewmate.exceptions.InvalidGameCodeException;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GameCodeHelper {
	// https://discord.com/channels/757425025379729459/759066383090188308/765437094582943774
	private static final char[] gameCodeLetters = "QWXRTYLPESDFGHUJKZOCBINMA".toCharArray();

	// https://wiki.weewoo.net/wiki/Game_Codes#Version_2
	private static final byte[] v2Map = new byte[] {0x19, 0x15, 0x13, 0x0a, 0x08, 0x0b, 0x0c, 0x0d, 0x16, 0x0f, 0x10, 0x06, 0x18, 0x17, 0x12, 0x07, 0x00, 0x03, 0x09, 0x04, 0x0e, 0x14, 0x01, 0x02, 0x05, 0x11};

	// https://www.geeksforgeeks.org/bitwise-operators-in-java/
	// https://gist.github.com/alexis-evelyn/f541d27811b62fd987c93cf79ed049a7
	public static String parseGameCode(byte[] gameCodeBytes) throws InvalidBytesException, InvalidGameCodeException {
		if (gameCodeBytes == null)
			throw new InvalidBytesException(Main.getTranslationBundle().getString("gamecode_null_exception"));

		if (gameCodeBytes.length != 4)
			throw new InvalidBytesException(Main.getTranslationBundle().getString("gamecode_invalid_length_exception"));

		// https://stackoverflow.com/a/7619315
		// Don't Reverse Bytes Like `Integer.reverseBytes(gameCodeInteger);`. It's already reversed apparently.
		int gameCodeInteger = gameCodeBytes[0] << 24 | (gameCodeBytes[1] & 0xFF) << 16 | (gameCodeBytes[2] & 0xFF) << 8 | (gameCodeBytes[3] & 0xFF);
		// int gameCodeInteger = ByteBuffer.wrap(gameCodeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt(); // TODO: Confirm LE

		if (gameCodeInteger == 0)
			throw new InvalidGameCodeException(Main.getTranslationBundle().getString("gamecode_invalid_code_exception"));

		// TODO: Figure Out How To Continue Reversing Bytes To Game Code

		return "TODO: Implement Me!!!"; //convertIntToGameCode(gameCodeInteger);
	}

	public static byte[] generateGameCodeBytes(String gameCode) {
		// Game Codes Can Be 4 or 6 Capital Letters Long
		// Technically the client allows numbers in the game code, but it results in an integer 0.

		// Ensure GameCode Is Valid Or Convertable To Valid
		if (!gameCode.matches("([A-Z]|[a-z])+"))
			return new byte[0];

		String fixedCode = gameCode.toUpperCase();

		// 4 Digit Room Codes are just The ASCII Bytes
		if (fixedCode.length() == 4)
			return fixedCode.getBytes();
		else if (fixedCode.length() == 6)
			return generateGameCodeV2(fixedCode);

		return new byte[0];
	}

	private static byte[] generateGameCodeV2(String gameCode) {
		if (gameCode.length() < 6)
			return new byte[0];

		int a = v2Map[gameCode.charAt(0) - 65];
		int b = v2Map[gameCode.charAt(1) - 65];
		int c = v2Map[gameCode.charAt(2) - 65];
		int d = v2Map[gameCode.charAt(3) - 65];
		int e = v2Map[gameCode.charAt(4) - 65];
		int f = v2Map[gameCode.charAt(5) - 65];

		int one = (a + 26 * b) & 0x3FF;
		int two = (c + 26 * (d + 26 * (e + 26 * f)));

		int gameCodeInt = one | ((two << 10) & 0x3FFFFC00) | 0x80000000;

		return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(gameCodeInt).array();
	}

	private static String convertIntToGameCode(int input) {
		int a = input & 0x3FF;
		int b = (input >> 10) & 0xFFFFF;

		StringBuilder gameCode = new StringBuilder();
		gameCode.append(gameCodeLetters[a % 26]);
		gameCode.append(gameCodeLetters[a / 26]);
		gameCode.append(gameCodeLetters[b % 26]);
		gameCode.append(gameCodeLetters[b / 26 % 26]);
		gameCode.append(gameCodeLetters[b / (26 * 26) % 26]);
		gameCode.append(gameCodeLetters[b / (26 * 26 * 26) % 26]);

		System.out.println("Game Code String: " + gameCode);

		return gameCode.toString();
	}
}
