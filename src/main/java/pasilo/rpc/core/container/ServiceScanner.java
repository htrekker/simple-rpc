package pasilo.rpc.core.container;

import lombok.extern.slf4j.Slf4j;
import pasilo.rpc.core.annotation.RpcService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
public class ServiceScanner {

	private String getClassFullName(String classFileName, String packagePath) {
		String className = classFileName.substring(0, classFileName.length()-6);

		String[] paths = packagePath.split("/");


		String packageName = String.join(".", paths);
		return packageName + "." + className;
	}

	private Class<?> loadClassFile(File classPath, String packageName) throws ClassNotFoundException {
		String classFullName = getClassFullName(classPath.getName(), packageName);
		log.info("loading class file: {}.", classFullName);

		Class<?> clz = Class.forName(classFullName);
		if (clz.isAnnotationPresent(RpcService.class)) {
			return clz;
		}
		return null;
	}

	private List<Class<?>> loadDirectory(File dir, String packageName) throws ClassNotFoundException {
		List<Class<?>> clzs = new ArrayList<>();
		FilenameFilter filter = (file, name) -> name.endsWith(".class") || file.isDirectory();
		File[] files = dir.listFiles(filter);

		for (File file : files) {
			if (file.isDirectory()) {
				String subPackageName = String.format("%s.%s", packageName, file.getName());
				clzs.addAll(loadDirectory(file, subPackageName));
			} else {
				Class<?> clz = loadClassFile(file, packageName);
				if (clz != null){
					clzs.add(clz);
				}
			}
		}

		return clzs;
	}

	public List<Class<?>> scanPackage(String packageName) throws IOException, ClassNotFoundException {
		Enumeration<URL> reources = ClassLoader.getSystemResources(packageName.replace(".", "/"));

		List<Class<?>> clzs = new ArrayList<>();
		while (reources.hasMoreElements()) {
			URL url = reources.nextElement();
			File filePath = new File(url.getPath().replace("%20", " ")); // in case for space

			// if don't exists
			if (!filePath.exists()) {
				throw new FileNotFoundException(packageName+" does not exists.");
			}

			if (filePath.isFile()) {
				Class<?> clz = loadClassFile(filePath, packageName);
				clzs.add(clz);
			} else if (filePath.isDirectory()) {
				List<Class<?>> clz = loadDirectory(filePath, packageName);
				clzs.addAll(clz);
			}
		}

		return clzs;
	}

	public static void main(String[] args) throws IOException {
		ServiceScanner scanner = new ServiceScanner();
		try {
			List<Class<?>> clzs = scanner.scanPackage("pasilo.rpc.service.impl");
			System.out.println(clzs);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
