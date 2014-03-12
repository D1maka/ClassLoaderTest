package com.epam.loader

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The class loads class files from the classPath paths,
 * if the java file is changed during the execution, class
 * loader compiles and loads a new version. Load of classes
 * that contain "Object" or "Constructor" is delegated to 
 * parent class loader.
 * 
 * @author Dmytro_Veres
 *
 */
public class TestModuleClassLoader extends ClassLoader {

	private String[] classPaths;

	public TestModuleClassLoader(String[] paths) {
		classPaths = paths;
	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		Class resultClass = null;
		if (name.contains("Object") || name.contains("Constructor")) {
			resultClass = super.getSystemClassLoader().loadClass(name);
		} else {
			resultClass = findClass(name);
		}
		return resultClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class resultClass = null;

		File javaFile = findFile(name, ".java");
		File classFile = findFile(name, ".class");
		if(javaFile != null) {
			try {
				if (isNewerVersion(javaFile, classFile)) {
					if (!compile(javaFile)) {
						throw new ClassNotFoundException("Compilation fails");
					}
				}
				byte[] sourceBytes = loadFileAsBytes(classFile);
				resultClass = defineClass(name, sourceBytes, 0, sourceBytes.length);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			throw new ClassNotFoundException("Java file was not found");
		}

		return resultClass;
	} 
	
	/**
	 * @param name - file name
	 * @param extension - file extension
	 * @return if the file is found in one of the class paths - returns file instance, if file not found - return null
	 */
	private File findFile(String name, String extension) {
		File file = null;
		for (String path : classPaths) {
			file = new File((new File(path).getAbsolutePath()
					+ File.separatorChar
					+ name.replace('.', File.separatorChar) + extension));
			if (file.exists()) {
				return file;
			}
		}

		return file;
	}

	/**
	 * @param javaFile - File with source java code
	 * @return if the compilation process was successful - returns true, false otherways
	 * @throws IOException if execution of compilation terminates
	 */
	private boolean compile(File javaFile) throws IOException {
		Process p = Runtime.getRuntime().exec("javac " + javaFile);

		try {
			p.waitFor();
		} catch (InterruptedException ie) {

		}

		int ret = p.exitValue();
		return ret == 0;
	}

	/**
	 * @param javaFile - Java file
	 * @param classFile - Class file
	 * @return true if exists a newer version of the source code file
	 */
	private boolean isNewerVersion(File javaFile, File classFile) {
		if (!javaFile.exists()) {
			return false;
		}
		if (!classFile.exists()
				|| javaFile.lastModified() > classFile.lastModified()) {
			return true;
		}

		return false;
	}

	/**
	 * @param file
	 * @return file as an array of bytes
	 * @throws IOException if anything happens during reading from file or it's closing
	 */
	private byte[] loadFileAsBytes(File file) throws IOException {
		byte[] sourceBytes = new byte[(int) file.length()];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(sourceBytes, 0, sourceBytes.length);
		} catch (FileNotFoundException fnfe) {
			// This exception is not going to appear. File has already passed test before.
			fnfe.printStackTrace();
		} finally {
			fis.close();
		}

		return sourceBytes;
	}
}
