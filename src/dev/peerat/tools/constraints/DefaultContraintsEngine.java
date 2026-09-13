package dev.peerat.tools.constraints;

import static dev.peerat.parser.java.visitor.JavaVisitor.allClass;
import static dev.peerat.parser.java.visitor.JavaVisitor.allClassBase;
import static dev.peerat.parser.java.visitor.JavaVisitor.collect;
import static dev.peerat.parser.java.visitor.JavaVisitor.function;
import static dev.peerat.parser.java.visitor.JavaVisitor.seq;
import static dev.peerat.parser.java.visitor.JavaVisitor.variable;

import java.util.List;

import dev.peerat.parser.java.ClassBase;
import dev.peerat.parser.java.Function;
import dev.peerat.parser.java.JavaElement;
import dev.peerat.parser.java.JavaProject;
import dev.peerat.parser.java.Variable;
import dev.peerat.parser.java.operation.IfOperation;
import dev.peerat.parser.java.value.BiValue;
import dev.peerat.parser.java.value.MethodCallValue;
import dev.peerat.parser.java.value.StaticValue;
import dev.peerat.parser.java.value.Value;
import dev.peerat.parser.java.value.VariableAccessValue;
import dev.peerat.tools.codegen.engine.ElementEngine;
import dev.peerat.tools.constraints.state.AttributeConstraint;
import dev.peerat.tools.constraints.state.ConstantConstraint;
import dev.peerat.tools.constraints.state.ConstraintElement;
import dev.peerat.tools.constraints.state.EqualsConstraint;
import dev.peerat.tools.constraints.state.NotEqualsConstraint;

public class DefaultContraintsEngine{
	
	public void configure(ElementEngine engine){
		
		engine.rule("apply", JavaProject.class, project -> {
			engine.context(project);
			List<ClassBase> models = project.visit(collect(allClass().isNotExtend().hasNoImplementation())).toList();
			
			for(ClassBase model : models){
				engine.task("search rules", model);
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
		
		engine.rule("self declared", JavaProject.class, Function.class, (project, function) -> {
			Variable variable = function.getParameters().get(0);
			ConstraintBuilderContext builder = new ConstraintBuilderContext(
					project.visit(collect(allClassBase().name(seq(variable.getType().getName().getValue())))).toElement(),
					variable.getType().getName().getValue()
					);
			engine.context(builder);
			
			builder.setVariableName(variable.getName().getValue());
			
			for(JavaElement element : function.getElements()) engine.task("logic", element);
			
			System.out.println(builder.getConstraints());
			
			return function;
		});
		
		engine.rule("logic", IfOperation.class, ConstraintBuilderContext.class, (operation, builder) -> {
			System.out.println("logic "+operation.getCondition());
			engine.element(operation.getCondition());
			return null;
		});
		
		engine.rule("attribute", Value.class, ConstraintBuilderContext.class, (value, builder) -> {
			if(value instanceof StaticValue){
				return new ConstantConstraint(value);
			}
			if(value instanceof VariableAccessValue){
				VariableAccessValue access = (VariableAccessValue)value;
				Value base = access.base();
				if(base instanceof StaticValue){
					StaticValue staticValue = (StaticValue)base;
					if(staticValue.getToken().getValue().equals(builder.getVariableName())){
						return new AttributeConstraint(builder.getVariable(access.getVariable().getValue()));
					}
				}
				return new AttributeConstraint(engine.<AttributeConstraint>task("attribute", base).get(), builder.getVariable(access.getVariable().getValue()));
			}
			if(value instanceof MethodCallValue){
				
			}
			return null;
		});
		
		engine.<BiValue>rule(element -> element instanceof BiValue, "logic", (element, context) -> {
			ConstraintBuilderContext builder = context.getDependency(ConstraintBuilderContext.class);
			
			ConstraintElement left = engine.<ConstraintElement>task("attribute", element.left()).get();
			ConstraintElement right = engine.<ConstraintElement>task("attribute", element.right()).get();
			String action = element.getAction().getValue();
			
			if(action.equals("==")){
				builder.addConstraint(new EqualsConstraint(left, right));
				return;
			}
			
			if(action.equals("!=")){
				builder.addConstraint(new NotEqualsConstraint(left, right));
				return;
			}
		});
		
	}

}
