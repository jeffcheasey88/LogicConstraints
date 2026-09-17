package dev.peerat.tools.constraints.state;

import static dev.peerat.parser.java.builder.JavaBuilder.*;

import java.util.LinkedList;
import java.util.List;

import dev.peerat.parser.java.JavaElement;

public class ConstraintErrorEffect extends ConstraintEffect{

	private String errorMessage;
	
	public ConstraintErrorEffect(String errorMessage){
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}

	@Override
	public List<JavaElement> apply(String baseVariableName, String variableName){
		List<JavaElement> result = new LinkedList<>();
		if(errorMessage != null){
			result.add(ofThrowOperation(ofInstanceValue("RuntimeException", ofStaticValue(errorMessage).build())).build());
		}else{
			result.add(ofThrowOperation(ofInstanceValue("RuntimeException")).build());
		}
		return result;
	}
	
}