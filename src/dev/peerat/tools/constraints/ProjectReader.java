package dev.peerat.tools.constraints;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import dev.peerat.loaders.parser.Consumer;
import dev.peerat.loaders.parser.ProjectLoader;
import dev.peerat.loaders.parser.ProjectSupplier;
import dev.peerat.loaders.parser.ReadOrder;

public class ProjectReader implements dev.peerat.loaders.parser.ProjectReader{
	
	private static ProjectReader READER = new ProjectReader();

	public static ProjectReader getInstance(){
		return READER;
	}
	
	static {
		
		ProjectLoader.getLoader().register(READER, ReadOrder.HIGHT);
		
	}
	
	private Set<File> projects;
	
	private ProjectReader(){
		this.projects = new HashSet<>();
	}
	
	public void addProject(File file){
		this.projects.add(file);
	}

	
	@Override
	public Consumer<ProjectSupplier> getProjectSupplier(){
		return loader -> {
			ConstraintReader constraintReader = new ConstraintReader();
			for(File project : projects){
				constraintReader.readProject(loader.getProject(project));
			}
		};
	}

}