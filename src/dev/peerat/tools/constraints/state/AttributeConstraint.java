package dev.peerat.tools.constraints.state;

import dev.peerat.parser.java.Variable;

public class AttributeConstraint{
	
	private AttributeConstraint parent;
	private Variable variable;
	
	public AttributeConstraint(Variable variable){
		this(null, variable);
	}
	
	public AttributeConstraint(AttributeConstraint parent, Variable variable){
		this.parent = parent;
		this.variable = variable;
	}
	
	public AttributeConstraint getParent(){
		return this.parent;
	}
	
	public Variable getVariable(){
		return this.variable;
	}
}
