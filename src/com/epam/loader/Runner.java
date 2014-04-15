package com.epam.loader;
import java.io.IOException;

public class Runner {
	public static void main(String[] args) {
		
		while (true) {
			try {
				TestModuleClassLoader loader = new TestModuleClassLoader();
				Class testModuleClass = Class.forName("com.epam.loader.TestModule", true,
						loader);
				Object test = testModuleClass.newInstance();
				System.out.println(test.toString());
				System.in.read();
				System.in.read();
			} catch (ClassNotFoundException e) {
				System.out.println("Class with such name could not be found");
			} catch (InstantiationException e) {
				System.out.println("Could not create the instance of class");
			} catch (IllegalAccessException e) {
				System.out.println("Don't have access for method toString() of class");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
