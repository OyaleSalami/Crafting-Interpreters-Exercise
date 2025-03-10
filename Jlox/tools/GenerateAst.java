package com.craftinginterpreters.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst
{
	public static void main(String[] args) throws IOException
	{
		if(args.length != 1)
		{
			System.err.println("Usage: generate_ast <output directory>");
			System.exit(64);
		}

		String outputDir = args[0];
		defineAst(outputDir, "Expr", Arrays.asList(
			"Binary : Expr left, Token operator, Expr right",
			"Grouping : Expr expression",
			"Literal : Object value",
			"Unary : Token operator, Expr right"
		));

		defineAst(outputDir, "Stmt", Arrays.asList(
				"Expression : Expr expression",
				"Print : Expr expression"
		));
	}

	private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException
	{
		String path = outputDir + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");

		writer.println("package com.craftinginterpreters.lox;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("abstract class " + baseName + " \n{");

		defineVisitor(writer, baseName, types);

		//The Classes for the AST
		for(String type : types)
		{
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			defineType(writer, baseName, className, fields);
		}

		//The base accept() method
		writer.println();
		writer.println("\tabstract <R> R accept(Visitor<R> visitor);");

		writer.println("}"); //Closes abstract class
		writer.close();
	}

	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types)
	{
		writer.println("\tinterface Visitor<R> \n\t{");

		for(String type : types)
		{
			String typeName = type.split(":")[0].trim();
			writer.println("\t\tR visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}

		writer.println("\t}\n");
	}

	private static void defineType(PrintWriter writer, String baseName, String className, String fieldList)
	{
		writer.println("\tstatic class " + className + " extends " + baseName + " \n\t{");

		//Constructor
		writer.println("\t\t" + className + "(" + fieldList + ") \n\t\t{");

		//Store parameters in fields
		String[] fields = fieldList.split(", ");
		for(String field : fields)
		{
			String name = field.split(" ")[1];
			writer.println("\t\t\tthis." + name + " = " + name + ";");
		}

		writer.println("\t\t}"); //Closes constructor

		//Visitor pattern
		writer.println();
		writer.println("\t\t@Override");
		writer.println("\t\t<R> R accept(Visitor<R> visitor) \n\t\t{");
		writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
		writer.println("\t\t}");

		//Fields
		writer.println();
		for(String field : fields)
		{
			writer.println("\t\tfinal " + field  + ";");
		}

		writer.println("\t}\n"); //Closes static class
	}
}