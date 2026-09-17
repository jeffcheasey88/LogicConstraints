package dev.peerat.tools.constraints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import dev.peerat.parser.java.JavaFile;
import dev.peerat.parser.java.JavaParser;
import dev.peerat.parser.java.JavaProject;

public class Example{

	public static void main(String[] args) throws Exception{
		ConstraintReader reader = new ConstraintReader();

		JavaProject project = new JavaProject(false);
		JavaParser parser = new JavaParser();
		JavaFile file = new JavaFile();

		BufferedReader fileReader = new BufferedReader(
				new FileReader(new File("./src/dev/peerat/tools/constraints/Example.java")));
		parser.parse(fileReader, file);
		fileReader.close();
		project.addFile(file, true);

		reader.readProject(project);

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
