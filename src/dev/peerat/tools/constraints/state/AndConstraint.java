package dev.peerat.tools.constraints.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AndConstraint{
	
	private Set<Constraint> constraints;
	
	public AndConstraint(Constraint...constraints){
		this.constraints = new HashSet<>(Arrays.asList(constraints));
	}
	
	public Set<Constraint> getConstraints(){
		return this.constraints;
	}
}
