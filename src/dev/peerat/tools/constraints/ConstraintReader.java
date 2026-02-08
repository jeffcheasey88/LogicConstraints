package dev.peerat.tools.constraints;

import dev.peerat.parser.java.JavaProject;
import dev.peerat.tools.codegen.engine.ElementEngine;
import dev.peerat.tools.constraints.state.ConstraintState;

public class ConstraintReader{
	
	private ElementEngine engine;
	
	public ConstraintReader(){
		this.engine = new ElementEngine();
		new DefaultContraintsEngine().configure(engine);
	}
	
	public ElementEngine getEngine(){
		return this.engine;
	}
	
	public ConstraintState readProject(JavaProject project){
		ConstraintState state = new ConstraintState();
		engine.context(state);
		
		engine.<ConstraintState>task("apply", project);
		
		return state;
	}

}
 