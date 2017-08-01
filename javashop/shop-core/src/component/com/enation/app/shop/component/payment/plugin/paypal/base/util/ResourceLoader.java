package com.enation.app.shop.component.payment.plugin.paypal.base.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A class to locate resources and retrieve their contents. To find the resource
 * the class searches the CLASSPATH first, then Resource.class.getResource("/" +
 * name). If the Resource finds a "file:" URL, the file path will be treated as
 * a file. Otherwise, the path is treated as a URL.
 */
public class ResourceLoader {

	// Java classpath system property
	private static final String CLASSPATH = "java.class.path";

	// file scheme
	private static final String FILESCHEME = "file:";

	// InputStream
	private InputStream inputStream;

	// Resource Name
	private String name;

	// File
	private File file;

	// URL
	private URL url;

	/**
	 * ResourceLoader to load the resource specified by name
	 * 
	 * @param name
	 *            Name of the resource to load
	 */
	public ResourceLoader(String name) {
		this.name = name;
	}

	// Returns an input stream to read the resource contents
	public InputStream getInputStream() throws IOException {
		if (inputStream == null) {
			if (!searchClasspath(name) && !searchResourcepath(name)) {
				throw new IOException("Resource '" + name
						+ "' could not be found");
			}
			if (file != null) {
				inputStream = new BufferedInputStream(new FileInputStream(file));
			} else if (url != null) {
				inputStream = new BufferedInputStream(url.openStream());
			}
		}
		return inputStream;
	}

	// Returns true if found
	private boolean searchClasspath(String filename) {
		String classpath = System.getProperty(CLASSPATH, "");
		String[] paths = classpath.split(File.pathSeparator);
		file = searchDirectories(paths, filename);
		return (file != null);
	}

	// Search the paths for the specified file
	private static File searchDirectories(String[] paths, String filename) {
		for (String path : paths) {
			File file = new File(path, filename);
			if (file.exists() && !file.isDirectory()) {
				return file;
			}
		}
		return null;
	}

	// Returns true if found
	private boolean searchResourcepath(String name) {
		String rootName = "/" + name;
		URL res = ResourceLoader.class.getResource(rootName);
		if (res == null) {
			return false;
		}
		// Try converting from a URL to a File.
		File resFile = urlToFile(res);
		if (resFile != null) {
			file = resFile;
		} else {
			url = res;
		}
		return true;
	}

	// Returns a File object if the URL has a file scheme
	private static File urlToFile(URL res) {
		String externalForm = res.toExternalForm();
		if (externalForm.startsWith(FILESCHEME)) {
			try {
				return new File(res.toURI());
			} catch (URISyntaxException e) {
				return new File(externalForm.substring(5));
			}
		}
		return null;
	}

}
