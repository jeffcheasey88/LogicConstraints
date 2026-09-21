package dev.peerat.tools.constraints;

import java.io.File;

import dev.peerat.loaders.parser.ProjectLoader;

public class Example{

	public static void main(String[] args) throws Exception{
		ProjectReader.getInstance().addProject(new File("./src/dev/peerat/tools/constraints/Example.java"));
		ProjectLoader.getLoader().load();
	}

	public static void exampleUsage(MyObject obj){
		obj.bothPresent = false;
	}

	class MyObject{

		private Integer a;
		private Integer b;
		private boolean bothPresent;

	}

	class MyRepository{
		
		int countInstanceOfBothPresent(){ return 0;}
		
	}
	
	class MyObjectConstraints implements SelfConstraints<MyObject>, EnvironmentalConstraints{

		@Override
		public void check(MyObject value){
			if (value.a == null && value.b == null)
				error("Can't be both null");
			if (value.a != null && value.b != null) {
				if (value.b <= value.a)
					error("b need to be heigher than a");
				if (!value.bothPresent)
					reState(() -> {
						value.bothPresent = true;
					});
			}
		}
		
		public void check(MyObject value, MyRepository repository){
			if(value.bothPresent && repository.countInstanceOfBothPresent() > 0) error(new RuntimeException("It can only be one MyObject with both value present !"));
		}

	}

}
