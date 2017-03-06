package sixProgram;

public class Program1 {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int count = 0;
        int limitation = 15; // stop condition
        function1(count, limitation);
    }

    public static void function1(int count, int limitation) {
        System.out.println("The function1 fired! count = " + count);

        if (count <= limitation)
            function2(++count,limitation);
    }

    public static void function2(int count, int limitation) {
        System.out.println("The function2 fired! count = " + count);
        if (count <= limitation)
            function3(++count,limitation);
    }

    public static void function3(int count, int limitation) {
        System.out.println("The function3 fired! count = " + count);
        if (count <= limitation)
            function4(++count,limitation);
    }

    public static void function4(int count, int limitation) {
        System.out.println("The function4 fired! count = " + count);
        if (count <= limitation)
            function1(++count,limitation);
    }
}
