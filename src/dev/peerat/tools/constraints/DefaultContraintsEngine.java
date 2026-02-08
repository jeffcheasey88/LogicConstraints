package dev.peerat.tools.constraints;

import static dev.peerat.parser.java.visitor.JavaVisitor.*;

import java.util.List;

import dev.peerat.parser.java.ClassBase;
import dev.peerat.parser.java.Function;
import dev.peerat.parser.java.JavaProject;
import dev.peerat.parser.java.Variable;
import dev.peerat.tools.codegen.engine.ElementEngine;

public class DefaultContraintsEngine{
	
	public void configure(ElementEngine engine){
		
		engine.rule("apply", JavaProject.class, project -> {
			List<ClassBase> models = project.visit(collect(allClass().isNotExtend().hasNoImplementation())).toList();
			
			for(ClassBase model : models){
				engine.task("search rules", project, model);
			}
			
			return null;
		});
		
		engine.rule("search rules", JavaProject.class, ClassBase.class, (project, model) -> {
			
			List<Function> selfRules = project.visit(
					allClass()
					.oneImplementation(seq("SelfConstraints<"+model.getName().getName().getValue()+">"))
					.oneChild(
							collect(
									function()
									.type(seq("void"))
									.name(seq("check"))
									.countParameter(1, variable())
									.oneParameter(variable().type(seq(model.getName().getName().getValue())))
									)
					)).toList();
			
			for(Function func : selfRules) engine.task("self declared", func);
			
			return null;
		});
		
		engine.rule("self declared", Function.class, function -> {
			Variable variable = function.getParameters().get(0);
			ConstraintBuilderContext builder = new ConstraintBuilderContext(variable.getType().getName().getValue());
			engine.context(builder);
			
			builder.setVariableName(variable.getName().getValue());
			
			return function;
		});
		
		
	}

}
