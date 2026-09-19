package dev.peerat.tools.constraints.state;

import static dev.peerat.parser.java.builder.JavaBuilder.ofInstanceValue;
import static dev.peerat.parser.java.builder.JavaBuilder.ofStaticValue;
import static dev.peerat.parser.java.builder.JavaBuilder.ofThrowOperation;

import java.util.LinkedList;
import java.util.List;

import dev.peerat.parser.java.JavaElement;
import dev.peerat.parser.java.value.Value;

public class ConstraintErrorEffect extends ConstraintEffect{

	private String errorMessage;
	private Value errorValue;
	
	public ConstraintErrorEffect(String errorMessage){
		this.errorMessage = errorMessage;
	}
	
	public ConstraintErrorEffect(Value errorValue){
		this.errorValue = errorValue;
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	public Value getErrorValue(){
		return this.errorValue;
	}

	@Override
	public List<JavaElement> apply(String baseVariableName, String variableName){
		List<JavaElement> result = new LinkedList<>();
		if(errorMessage != null){
			result.add(ofThrowOperation(ofInstanceValue("RuntimeException", ofStaticValue(errorMessage).build())).build());
		}else if(errorValue != null){
			result.add(ofThrowOperation(ofInstanceValue("RuntimeException", (Value) errorValue.copyOf())).build());
		}else{
			result.add(ofThrowOperation(ofInstanceValue("RuntimeException")).build());
		}
		return result;
	}
	
}