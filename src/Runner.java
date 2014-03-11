
public class Runner {
	public static void main(String[] args) {
		String[] paths = {"C:\\Users\\Dmytro_Veres@epam.com\\workspace\\ClassLoaderTest\\bin", "C:\\Users\\Dmytro_Veres@epam.com\\workspace\\ClassLoaderTest\\src"};
		TestModuleClassLoader loader = new TestModuleClassLoader(paths);
		while(true){
			try {
				Class testModuleClass = Class.forName("TestModule", true, loader);
				Object test = testModuleClass.newInstance();
				System.out.println(test.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
