package dev.peerat.tools.constraints.state;

import java.util.HashMap;
import java.util.Map;

import dev.peerat.parser.java.Variable;

public class ConstraintState{
	
	private Map<Variable, Constraint> map;
	
	public ConstraintState(){
		this.map = new HashMap<>();
	}

}
