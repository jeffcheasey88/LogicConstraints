package dev.peerat.tools.constraints;

public class ConstraintBuilderContext{
	
	private String type;
	private String variableName;

	public ConstraintBuilderContext(String type){
		this.type = type;
	}
	
	public void setVariableName(String name){
		this.variableName = name;
	}
}
