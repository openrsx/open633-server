package com.rs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Tyluur <itstyluur@gmail.com>
 * @author Dennis
 * @since Sep 8, 2013
 */
public class Config {

	/**
	 * Loading the properties
	 */
	public void load() {
		try {
			properties.load(new FileReader("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Changes the config value 
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("deprecation")
	public void changeConfig(String key, String value) {
		properties.setProperty(key, value);
		try {
			properties.save(new FileOutputStream(new File("config.properties")), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		load();
	}

	/**
	 * Gets a String value from the configuration file
	 *
	 * @param key
	 *            The key to search for
	 * @return
	 */
	public String getString(String key) {
		return (String) properties.get(key);
	}

	/**
	 * Gets an integer value from the configuration file
	 *
	 * @param key
	 *            The key to search for
	 * @return
	 */
	public Integer getInteger(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

	public boolean getBoolean(String string) {
		return Boolean.parseBoolean(properties.getProperty(string));
	}

	/**
	 * The getter
	 *
	 * @return
	 */
	public static Config get() {
		return INSTANCE;
	}

	/**
	 * The instance of the properties
	 */
	private final Properties properties = new Properties();

	/**
	 * The instance of this class.
	 */
	private static final Config INSTANCE = new Config();

}
