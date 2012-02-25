package me.Todkommt.ThumbsApply.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import me.Todkommt.ThumbsApply.ThumbsApply;

public class TAClassHandler {

	public static List<Class<?>> loadClasses(File dir) throws Exception
	{
		List<Class<?>> modules = new ArrayList<Class<?>>();
		for (File file : dir.listFiles()) {
			if(!file.getName().endsWith(".jar"))
			{
				continue;
			}
			URLClassLoader loader = new URLClassLoader(new URL[] { file.toURI().toURL() }, ThumbsApplyModule.class.getClassLoader());
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));
			Class<?> clazz = Class.forName("me.Todkommt.ThumbsApply.modules." + name, false, loader);
			Object object = clazz.newInstance();
			if (!(object instanceof ThumbsApplyModule)) {
				ThumbsApply.instance.log.info("Not a ThumbsApply module: " + clazz.getSimpleName());
				continue;
			}
			modules.add(object.getClass());
			ThumbsApply.instance.log.info("Loaded ThumbsApply module: " + object.getClass().getSimpleName());
		}
		return modules;
	}
	
}
