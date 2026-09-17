package dev.peerat.tools.constraints;

import static dev.peerat.parser.java.visitor.JavaVisitor.*;
import static dev.peerat.parser.visitor.Visitors.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dev.peerat.parser.java.ClassBase;
import dev.peerat.parser.java.Variable;
import dev.peerat.tools.constraints.state.Constraint;
import dev.peerat.tools.constraints.state.ConstraintState;

public class ConstraintBuilderContext{
	
	private ClassBase clazz;
	private String type;
	private String variableName;
	private ConstraintState state;
	
	public ConstraintBuilderContext(ClassBase clazz, String type){
		this.clazz = clazz;
		this.type = type;
		this.state = new ConstraintState();
	}
	
	public Variable getVariable(String name){
		return clazz.visit(classBase().oneChild(collect(variable().name(seq(name))))).toElement();
	}
	
	public String getVariableName(){
		return this.variableName;
	}
	
	public void setVariableName(String name){
		this.variableName = name;
	}
	
	public void addConstraint(Variable variable, Constraint constraint){
		this.state.addConstraint(variable, constraint);
	}
	
	public List<Constraint> getConstraints(Variable variable){
		return state.getConstraints(variable);
	}
	
}
