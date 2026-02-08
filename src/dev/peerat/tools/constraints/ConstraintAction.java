package dev.peerat.tools.constraints;

public interface ConstraintAction{
	
	default void error(){}
	
	default void error(String message){}
	
	default void error(Exception exception){}
	
	default void reState(Runnable runnable) {}

}
