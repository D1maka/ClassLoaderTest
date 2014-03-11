import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestModuleClassLoader extends ClassLoader {

	private String[] classPaths;

	public TestModuleClassLoader(String[] paths) {
		classPaths = paths;
	}

	public Class loadClass(String name) throws ClassNotFoundException {
		Class resultClass = null;
		if (name.contains("Object") || name.contains("Constructor")) {
			resultClass = super.getSystemClassLoader().loadClass(name);
		} else {
			resultClass = findClass(name);
		}
		return resultClass;
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class resultClass = null;

		File javaFile = findFile(name, ".java");
		File classFile = findFile(name, ".class");
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

		return resultClass;
	}

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

	private boolean compile(File javaFile) throws IOException {
		Process p = Runtime.getRuntime().exec("javac " + javaFile);

		try {
			p.waitFor();
		} catch (InterruptedException ie) {

		}

		int ret = p.exitValue();
		return ret == 0;
	}

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

	private byte[] loadFileAsBytes(File file) throws IOException {
		byte[] sourceBytes = new byte[(int) file.length()];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(sourceBytes, 0, sourceBytes.length);
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
		} finally {
			fis.close();
		}

		return sourceBytes;
	}
}
