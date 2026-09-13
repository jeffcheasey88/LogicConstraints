package dev.peerat.tools.constraints;

import static dev.peerat.parser.java.visitor.JavaVisitor.classBase;
import static dev.peerat.parser.java.visitor.JavaVisitor.collect;
import static dev.peerat.parser.java.visitor.JavaVisitor.seq;
import static dev.peerat.parser.java.visitor.JavaVisitor.variable;

import java.util.HashSet;
import java.util.Set;

import dev.peerat.parser.java.ClassBase;
import dev.peerat.parser.java.Variable;
import dev.peerat.tools.constraints.state.Constraint;

public class ConstraintBuilderContext{
	
	private ClassBase clazz;
	private String type;
	private String variableName;
	private Set<Constraint> constraints;
	
	public ConstraintBuilderContext(ClassBase clazz, String type){
		this.clazz = clazz;
		this.type = type;
		this.constraints = new HashSet<>();
	}
	
	public Variable getVariable(String name){
		return clazz.visit(classBase().oneChild(collect(variable().name(seq(name))))).toElement();
	}
	
	public String getVariableName(){
		return this.variableName;
	}
	
	public Set<Constraint> getConstraints(){
		return this.constraints;
	}
	
	public void setVariableName(String name){
		this.variableName = name;
	}
	
	public void addConstraint(Constraint constraint){
		this.constraints.add(constraint);
	}
}
