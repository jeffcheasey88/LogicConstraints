package dev.peerat.tools.constraints.state;

import dev.peerat.parser.java.value.Value;

public class ConstantConstraint extends ConstraintElement{
	
	private Value value;
	
	public ConstantConstraint(Value value){
		this.value = value;
	}
	
	public Value getValue(){
		return this.value;
	}

}
