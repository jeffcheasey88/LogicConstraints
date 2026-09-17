package dev.peerat.tools.constraints.state;

import static dev.peerat.parser.visitor.Visitors.*;
import static dev.peerat.parser.java.visitor.JavaVisitor.*;
import static dev.peerat.parser.java.builder.JavaBuilder.*;

import java.util.LinkedList;
import java.util.List;

import dev.peerat.parser.java.JavaElement;
import dev.peerat.parser.java.builder.JavaBuilder;
import dev.peerat.parser.java.operation.IfOperation;
import dev.peerat.parser.java.value.StaticValue;
import dev.peerat.parser.java.value.Value;
import dev.peerat.tools.refactor.ElementUpdater;

public class Constraint{

	private String baseVariableName;
	private Value condition;
	private List<Constraint> childs;
	private List<ConstraintEffect> effects;
	
	public Constraint(String baseVariableName, Value condition, List<Constraint> childs, List<ConstraintEffect> effects){
		this.baseVariableName = baseVariableName;
		this.condition = condition;
		this.childs = childs;
		this.effects = effects;
	}
	
	public List<JavaElement> check(String variableName){
		List<JavaElement> result = new LinkedList<>();
		
		Value checker = (Value) condition.copyOf();
		List<StaticValue> baseAccess = checker.visit(collect(allStaticValue().token(seq(baseVariableName)))).toList();
		for(StaticValue access : baseAccess) ElementUpdater.replace(access, ofStaticValue(variableName).build());
		IfOperation conditionChecker = JavaBuilder.ofIfOperation(checker).build();
		
		for(ConstraintEffect effect : effects){
			conditionChecker.getElements().addAll(effect.apply(baseVariableName, variableName));
		}
		
		for(Constraint child : childs){
			conditionChecker.getElements().addAll(child.check(variableName));
		}
		
		result.add(conditionChecker);
		return result;
	}
	
}