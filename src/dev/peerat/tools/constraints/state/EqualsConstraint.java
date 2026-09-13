package dev.peerat.tools.constraints.state;

public class EqualsConstraint extends Constraint{
	
	private ConstraintElement left;
	private ConstraintElement right;
	
	public EqualsConstraint(ConstraintElement left, ConstraintElement right){
		this.left = left;
		this.right = right;
	}
	
	public ConstraintElement left(){
		return this.left;
	}
	
	public ConstraintElement right(){
		return this.right;
	}
	
	public ConstraintElement other(ConstraintElement base){
		return base.equals(left) ? left : right;
	}

}
