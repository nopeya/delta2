package javax.common.utils;

import java.io.File;
import java.net.URL;

public class ClassUtils {
	public static void scan(String packageName, Handler handler) {
		URL url = ClassUtils.class.getResource("/" + packageName.replaceAll("\\.", "/"));
		File dir = new File(url.getFile());
		for (File file : dir.listFiles()) {
			String path = packageName + "." + file.getName();
			path = path.replaceAll("^\\.", "");
			if (file.isDirectory()) {
				scan(path, handler);
			} else {
				if (handler.accept(file)) {
					handler.handle(file, path);
				}
			}
		}
	}
	
	public interface Handler {
		public boolean accept(File file);
		public void handle(File file, String className);
	}
}
