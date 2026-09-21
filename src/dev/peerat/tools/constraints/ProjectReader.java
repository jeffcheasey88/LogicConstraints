package dev.peerat.tools.constraints;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import dev.peerat.loaders.parser.Consumer;
import dev.peerat.loaders.parser.ProcessorService;
import dev.peerat.loaders.parser.ProjectLoader;
import dev.peerat.loaders.parser.ProjectOperationType;
import dev.peerat.loaders.parser.ProjectSupplier;
import dev.peerat.loaders.parser.ReadOrder;
import dev.peerat.loaders.parser.processors.AnnotationProcessor;
import dev.peerat.loaders.parser.processors.Processor;

public class ProjectReader implements dev.peerat.loaders.parser.ProjectReader, ProcessorService{
	
	private static ProjectReader READER = new ProjectReader();

	public static ProjectReader getInstance(){
		return READER;
	}
	
	static {
		
		Processor.getInstance().addService(READER);
		AnnotationProcessor.registerAnnotations("dev.peerat.tools.constraints.ConstraintProject");
		
	}
	
	private Set<File> projects;
	private Set<String> namedProjects;
	
	private ProjectReader(){
		this.projects = new HashSet<>();
		this.namedProjects = new HashSet<>();
	}
	
	public void addProject(File file){
		this.projects.add(file);
	}
	
	public void addProject(String name){
		this.namedProjects.add(name);
	}
	
	@Override
	public Consumer<ProjectSupplier> getProjectSupplier(){
		return loader -> {
			ConstraintReader constraintReader = new ConstraintReader();
			for(File project : projects){
				constraintReader.readProject(loader.getProject(project));
			}
			for(String projectName : namedProjects){
				constraintReader.readProject(loader.getProject(projectName));
			}
		};
	}

	@Override
	public void process(Processor processor, ProjectLoader loader) throws Exception{
		loader.register(READER, ProjectOperationType.UPDATE, ReadOrder.HIGHT);
	}

}