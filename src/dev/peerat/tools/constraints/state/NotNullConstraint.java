package dev.peerat.tools.constraints.state;

public class NotNullConstraint extends Constraint{
	
	private AttributeConstraint attribute;
	
	public NotNullConstraint(AttributeConstraint attribute){
		this.attribute = attribute;
	}
	
	public AttributeConstraint getAttribute(){
		return this.attribute;
	}

}
