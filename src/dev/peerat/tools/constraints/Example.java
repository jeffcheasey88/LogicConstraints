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

	class MyObjectConstraints implements SelfConstraints<MyObject>{

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

	}

}
