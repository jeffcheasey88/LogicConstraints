package dev.peerat.tools.constraints;

public interface SelfConstraints<T> extends ConstraintAction{

	void check(T value);
	
}
