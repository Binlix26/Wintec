package sixProgram;

public class Program1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int count = 0;// stop condition for the function chain
		function1(count);
	}

	public static void function1(int count) {
		System.out.println("The function1 fired!" + count);

		if (count <= 15)
			function2(++count);
	}

	public static void function2(int count) {
		System.out.println("The function2 fired!" + count);
		if (count <= 15)
			function3(++count);
	}

	public static void function3(int count) {
		System.out.println("The function3 fired!" + count);
		if (count <= 15)
			function4(++count);
	}

	public static void function4(int count) {
		System.out.println("The function4 fired!" + count);
		if (count <= 15)
			function1(++count);
	}
}
