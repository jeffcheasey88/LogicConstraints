package dev.peerat.tools.constraints.state;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev.peerat.parser.java.Variable;

public class ConstraintState{
	
	private Map<Variable, List<Constraint>> map;
	
	public ConstraintState(){
		this.map = new HashMap<>();
	}
	
	public void addConstraint(Variable variable, Constraint constraint){
		this.map.computeIfAbsent(variable, key -> new LinkedList<>()).add(constraint);
	}
	
	public List<Constraint> getConstraints(Variable variable){
		return map.get(variable);
	}

}
