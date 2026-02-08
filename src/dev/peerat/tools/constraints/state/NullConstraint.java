package dev.peerat.tools.constraints.state;

public class NullConstraint extends Constraint{
	
	private AttributeConstraint attribute;
	
	public NullConstraint(AttributeConstraint attribute){
		this.attribute = attribute;
	}
	
	public AttributeConstraint getAttribute(){
		return this.attribute;
	}

}
