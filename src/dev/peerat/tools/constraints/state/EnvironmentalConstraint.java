package dev.peerat.tools.constraints.state;

import java.util.List;

import dev.peerat.parser.java.JavaElement;
import dev.peerat.parser.java.value.Value;

public class EnvironmentalConstraint extends Constraint{

	public EnvironmentalConstraint(String baseVariableName, Value condition, List<Constraint> childs, List<ConstraintEffect> effects){
		super(baseVariableName, condition, childs, effects);
	}
	
	@Override
	public List<JavaElement> check(String variableName){
		List<JavaElement> elements = super.check(variableName);
		
		return elements;
	}

}
