package dev.peerat.tools.constraints.state;

import static dev.peerat.parser.java.builder.JavaBuilder.ofStaticValue;
import static dev.peerat.parser.java.visitor.JavaVisitor.allStaticValue;
import static dev.peerat.parser.visitor.Visitors.collect;
import static dev.peerat.parser.visitor.Visitors.seq;

import java.util.LinkedList;
import java.util.List;

import dev.peerat.parser.java.JavaElement;
import dev.peerat.parser.java.value.StaticValue;
import dev.peerat.tools.refactor.ElementUpdater;

public class ConstraintRestateEffect extends ConstraintEffect{

	private List<JavaElement> elements;
	
	public ConstraintRestateEffect(List<JavaElement> elements){
		this.elements = elements;
	}
	
	@Override
	public List<JavaElement> apply(String baseVariableName, String variableName){
		List<JavaElement> results = new LinkedList<>();
		for(JavaElement element : this.elements){
			JavaElement copy = element.copyOf();
			List<StaticValue> baseAccess = copy.visit(collect(allStaticValue().token(seq(baseVariableName)))).toList();
			for(StaticValue access : baseAccess) ElementUpdater.replace(access, ofStaticValue(variableName).build());
			results.add(copy);
		}
		return results;
	}

}
