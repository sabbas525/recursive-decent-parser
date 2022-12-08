public class Compiler {
    private static final boolean DEBUG = true;

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new Exception("File path not present as an argument");
        }

        String filePath = args[0];
        Parser parser = new Parser(filePath);

        boolean isValid = parser.S();
        if (isValid) {
            System.out.println("The input string is valid.");
        } else {
            System.out.println("The input string is invalid.");
        }

        if(DEBUG) parser.printStringTable();
    }
}