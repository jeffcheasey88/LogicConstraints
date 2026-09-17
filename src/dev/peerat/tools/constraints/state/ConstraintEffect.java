package dev.peerat.tools.constraints.state;

import java.util.List;

import dev.peerat.parser.java.JavaElement;

public abstract class ConstraintEffect{
	
	public abstract List<JavaElement> apply(String baseVariableName, String variableName);
	
}