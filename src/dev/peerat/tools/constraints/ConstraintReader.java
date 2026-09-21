package dev.peerat.tools.constraints;

import static dev.peerat.parser.java.visitor.JavaVisitor.*;
import static dev.peerat.parser.visitor.Visitors.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dev.peerat.parser.java.ClassBase;
import dev.peerat.parser.java.Function;
import dev.peerat.parser.java.FunctionParameter;
import dev.peerat.parser.java.JavaElement;
import dev.peerat.parser.java.JavaProject;
import dev.peerat.parser.java.Parameter;
import dev.peerat.parser.java.Type;
import dev.peerat.parser.java.Variable;
import dev.peerat.parser.java.operation.IfOperation;
import dev.peerat.parser.java.printer.JavaPrinter;
import dev.peerat.parser.java.printer.JavaPrinter.JavaPrintProvider;
import dev.peerat.parser.java.printer.JavaPrinter.Writer;
import dev.peerat.parser.java.value.BiValue;
import dev.peerat.parser.java.value.LambdaValue;
import dev.peerat.parser.java.value.MethodCallValue;
import dev.peerat.parser.java.value.StaticValue;
import dev.peerat.parser.java.value.Value;
import dev.peerat.parser.java.value.VariableAccessValue;
import dev.peerat.tools.constraints.state.Constraint;
import dev.peerat.tools.constraints.state.ConstraintEffect;
import dev.peerat.tools.constraints.state.ConstraintErrorEffect;
import dev.peerat.tools.constraints.state.ConstraintRestateEffect;
import dev.peerat.tools.constraints.state.ConstraintState;

public class ConstraintReader{
	
	
	public ConstraintReader(){
	}
	
	public ConstraintState readProject(JavaProject project){
		ConstraintState state = new ConstraintState();
		
		Map<ClassBase, ConstraintBuilderContext> rules = readRules(project);
		for(Entry<ClassBase, ConstraintBuilderContext> rule : rules.entrySet()){
			apply(project, rule.getKey(), rule.getValue());
		}
		
		return state;
	}
	
	private void apply(JavaProject project, ClassBase model, ConstraintBuilderContext context){
		List<FunctionParameter> parameters = project.visit(allFunction().someParameter(collect(allParameter().type(seq(model.getName().toString()))))).toList();
		
		for(FunctionParameter parameter : parameters){
			Function function = parameter.getParent(Function.class);
			ClassBase clazz = function.getParent(ClassBase.class);
			if(clazz instanceof dev.peerat.parser.java.Class){
				Type extension = ((dev.peerat.parser.java.Class)clazz).getExtension();
				if(extension != null){
					if(extension.toString().equals("SelfConstraints")) continue;
				}
			}
			
			String parmaterName = parameter.getName().getValue();
			
			List<JavaElement> elements = function.getElements();
			for(int index = 0; index < elements.size(); index++){
				JavaElement element = elements.get(index);
				System.out.println(element);
				if(element instanceof BiValue){
					BiValue biValue = (BiValue)element;
					if(biValue.getAction().getValue().equals("=")){
						Value left = biValue.left();
						if(left instanceof VariableAccessValue){
							VariableAccessValue access = (VariableAccessValue)left;
							Value base = access.base();
							if(base instanceof StaticValue){
								String variableName = ((StaticValue)base).getToken().getValue();
								if(variableName.equals(parmaterName)){
									List<JavaElement> checkers = checkVariableUpdate(model, context, model.visit(classBase().oneChild(collect(variable().name(seq(access.getVariable().getValue()))))).toElement(), variableName);
									elements.addAll(checkers);
									index+=checkers.size();
								}
							}
						}
					}
				}
			}
			try {
				System.out.println(extractJavaCode(function));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<JavaElement> checkVariableUpdate(ClassBase model, ConstraintBuilderContext context, Variable updated, String variableName){
		List<JavaElement> check = new LinkedList<>();
		
		List<Constraint> constraints = context.getConstraints(updated);
		if(constraints != null){
			for(Constraint constraint : constraints){
				check.addAll(constraint.check(variableName));
			}
		}
		
		return check;
	}
	
	private Map<ClassBase, ConstraintBuilderContext> readRules(JavaProject project){
		Map<ClassBase, ConstraintBuilderContext> result = new HashMap<>();
		List<ClassBase> models = project.visit(collect(allClass().isNotExtend().hasNoImplementation())).toList();
		for(ClassBase model : models){
			readSelfConstraints(project, model, result);
			readEnvironmentalConstraints(project, model, result);
		}
		return result;
	}
	
	private void readSelfConstraints(JavaProject project, ClassBase model, Map<ClassBase, ConstraintBuilderContext> result){
		List<Function> selfRules = project.visit(
				allClass()
				.oneImplementation(seq("SelfConstraints<"+model.getName().getName().getValue()+">"))
				.oneChild(
						collect(
								allFunction()
								.type(seq("void"))
								.name(seq("check"))
								.countParameter(1, parameter())
								.oneParameter(parameter().type(seq(model.getName().getName().getValue())))
								)
				)).toList();
		for(Function function : selfRules){
			Parameter variable = function.getParameters().get(0);
			ConstraintBuilderContext builder = result.get(model);
			if(builder == null){
				result.put(model, builder = new ConstraintBuilderContext(
					project.visit(collect(allClassBase().name(seq(variable.getType().getType().getName().getValue())))).toElement()
					));
			}
			
			for(JavaElement element : function.getElements()){
				if(element instanceof IfOperation){
					Constraint constraint = buildConstraint(variable.getName().getValue(), (IfOperation) element);
					for(Variable affected : allAffectedVariable(model, variable.getName().getValue(), (IfOperation) element)){
						builder.addConstraint(affected, constraint);
					}
				}
			}
		}
	}
	
	private void readEnvironmentalConstraints(JavaProject project, ClassBase model, Map<ClassBase, ConstraintBuilderContext> result){
		List<Function> envRules = project.visit(
				allClass()
				.oneImplementation(seq("EnvironmentalConstraints"))
				.oneChild(
						collect(
							allFunction()
							.oneParameter(parameter().type(seq(model.getName().getName().getValue())))
							.validate(func -> func.getParameters().size() > 1)
						)
				)).toList();
		for(Function function : envRules){
			Parameter variable = null;
			List<Parameter> envParameters = new LinkedList<>();
			for(Parameter parameter : function.getParameters()){
				if(variable == null){
					if(parameter.getType().getType().toString().equals(model.getName().getName().getValue())){
						variable = parameter;
						continue;
					}
				}
				envParameters.add(parameter);
			}
			ConstraintBuilderContext builder = result.get(model);
			if(builder == null){
				result.put(model, builder = new ConstraintBuilderContext(
					project.visit(collect(allClassBase().name(seq(variable.getType().getType().getName().getValue())))).toElement()
					));
			}
			
			for(JavaElement element : function.getElements()){
				if(element instanceof IfOperation){
					Constraint constraint = buildConstraint(variable.getName().getValue(), (IfOperation) element);
					for(Variable affected : allAffectedVariable(model, variable.getName().getValue(), (IfOperation) element)){
						builder.addConstraint(affected, constraint);
					}
				}
			}
		}
	}
	
	private Set<Variable> allAffectedVariable(ClassBase model, String variableName, IfOperation ifOperation){
		Set<Variable> result = new HashSet<>();
		List<VariableAccessValue> variableAccess = ifOperation.visit(
				collect(
						allVariableAccessValue()
						.base(allStaticValue().token(seq(variableName)))
				)
				).toList();
		
		for(VariableAccessValue access : variableAccess){
			if(access.base() instanceof StaticValue){
				if(((StaticValue)access.base()).getToken().getValue().equals(variableName)){
					Variable variable = model.visit(classBase().hasChild(collect(variable().name(seq(access.getVariable().getValue()))))).toElement();
					if(variable != null) result.add(variable);
				}
			}
		}
		
		return result;
	}
	
	private Constraint buildConstraint(String variableName, IfOperation operation){
		List<Constraint> childs = new LinkedList<>();
		List<ConstraintEffect> effects = new LinkedList<>();
		buildConstraint(variableName, operation.getElements(), childs, effects);
		return new Constraint(variableName, operation.getCondition(), childs, effects);
	}
	
	private void buildConstraint(String variableName, List<JavaElement> elements, List<Constraint> childs, List<ConstraintEffect> effects){
		for(JavaElement element : elements){
			if(element instanceof IfOperation){
				childs.add(buildConstraint(variableName, (IfOperation) element));
			}else if(element instanceof MethodCallValue){
				MethodCallValue methodCall = (MethodCallValue)element;
				if(methodCall.base() == null){
					if(methodCall.getToken().getValue().equals("error")){
						if(methodCall.getParameters() == null || methodCall.getParameters().isEmpty()){
							effects.add(new ConstraintErrorEffect((String) null));
						}else if(methodCall.getParameters().size() == 1){
							Value parameter = methodCall.getParameters().get(0);
							if(parameter instanceof StaticValue){
								effects.add(new ConstraintErrorEffect(((StaticValue)parameter).getToken().getValue()));
							}else{
								effects.add(new ConstraintErrorEffect(parameter));
							}
						}
					}else if(methodCall.getToken().getValue().equals("reState")){
						LambdaValue parameter = (LambdaValue) methodCall.getParameters().get(0);
						effects.add(new ConstraintRestateEffect(parameter.getElements()));
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	private static JavaPrintProvider printer = JavaPrinter.getProvider();
	private static StringBufferWriter stringBufferWriter = new StringBufferWriter();
	
	public static String extractJavaCode(JavaElement element) throws Exception{
		BufferedWriter buffer = new BufferedWriter(stringBufferWriter);
		Writer writer = new Writer(buffer);
		printer.print(element, writer, "");
		buffer.flush();
		return stringBufferWriter.getBuffer().replace("\r\n", "").replace("\n\r", "").replace("\n", "");
	}
	
	public static class StringBufferWriter extends java.io.Writer{
		
		private String buf;
		
		public String getBuffer(){
			String value = buf;
			this.buf = null;
			return value;
		}

		@Override
		public void close() throws IOException{}

		@Override
		public void flush() throws IOException{}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException{
			String current = new String(cbuf, off, len);
			if(buf == null) buf = current;
			else buf+=current;
		}

	}
	
}
 