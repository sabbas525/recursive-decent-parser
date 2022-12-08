import java.io.File;
import java.io.IOException;
import java.io.FileReader;

public class Lexer {

    private static final boolean DEBUG = false;
    private int idSymTab;
    private SymbolTable symbolTable;
    private String buffer = "";
    private int beginLexem;
    private int forward;
    private File input;
    private int state;
    private FileReader fileReader;
    private boolean flag = true;

    public Lexer(){
        beginLexem = 0;
        state = 0;
        idSymTab = 0;
    }

    public Boolean initialize(String filePath) {
        //Check if the input file exists
        //If so, initialize and fill the buffer from the input file
        try {
            input = new File(filePath);
            fileReader = new FileReader(input);
            int i;
            while ((i = fileReader.read()) != -1) {
                buffer += (char) i;
            }
            buffer += "\0";
            if (DEBUG) System.out.println(buffer);

            //create the Symbol Table
            symbolTable = new SymbolTable();

            // Inserting keywords into the stringTable. This choice has been made
            // in order not to build a transition diagram for each keyword.
            // The keywords will be "captured" by the transition diagram and handled accordingly.
            int keywords[] = {sym.IF, sym.THEN, sym.ELSE, sym.DO, sym.WHILE, sym.INT, sym.FLOAT};

            for(int x: keywords) {
                //the toLowerCase() method is called as the keywords must necessarily be
                //lowercase.
                symbolTable.add(idSymTab++, sym.tokens[x].toLowerCase(), new Token(x));
            }
            return true;
        }
        catch(IOException e){
            //the file does not exist
            return false;
        }
    }

    public Token nextToken(){
        //At each call of the lexer (nextToken()) all the variables used are reset
        int zeroCounter = 0; //It is used to count consecutive zeros after the decimal point
        //to make sure that decimal numbers do not end in multiple zeros
        forward = beginLexem;

        if(DEBUG){//Control prints
            System.out.println("Dim. buffer: " + buffer.length());
            System.out.println("Valore forward: " + forward);
        }

        state = 0;
        String lessema = ""; //Matches the recognized lexeme
        char c; //Character read at each iteration

        //We use a flag whose purpose is to indicate the eventual achievement
        //of the end of the file, corresponding to the '\0' character within the string
        while(flag){
            c = buffer.charAt(forward);
            if(c == '\0') {
                flag = false; //flag = false indicates the end of the file
                if (DEBUG) System.out.println("Lexer: Ho raggiunto la fine del file");
            }
            forward++;

            if (DEBUG) { //control prints
                System.out.println("-----------------------");
                System.out.println("Character Read: " + c);
                System.out.println("State: " + state);
                System.out.println("Forward: " + forward);
                System.out.println("Zerocounter:" + zeroCounter);
                System.out.println("-----------------------");
            }

            switch (state) {
                case 0:
                    //RELOP
                    if (c == '<') {
                        state = 1;
                    } else if (c == '=') {
                        state = 5;
                        beginLexem = forward;
                        return new Token(sym.EQ);
                    } else if (c == '>') {
                        state = 6;
                        //ID
                    } else if (Character.isLetter(c)) { //former state 9
                        lessema += c;
                        state = 10;
                        //WHITESPACE
                    } else if (Character.isWhitespace(c)) { //former state 22
                        state = 23;
                        //NUMBERS
                    } else if (Character.isDigit(c)) { //former state  12
                        if(c == '0'){
                            state = 12;
                        } else {
                            state = 13;
                        }
                        lessema += c;
                    }
                    //SEPARATORS
                    else if (c == ';') {
                        beginLexem = forward;
                        return new Token(sym.SEMICOLON);
                    } else if (c == ',') {
                        beginLexem = forward;
                        return new Token(sym.COMMA);
                    } else if (c == '(') {
                        beginLexem = forward;
                        return new Token(sym.LPAR);
                    } else if (c == ')') {
                        beginLexem = forward;
                        return new Token(sym.RPAR);
                    } else if (c == '{') {
                        beginLexem = forward;
                        return new Token(sym.LBRAC);
                    } else if (c == '}') {
                        beginLexem = forward;
                        return new Token(sym.RBRAC);
                    }
                    else if(c != '\0'){
                        beginLexem = forward;
                        lessema+=c;
                        return new Token(sym.ERROR,lessema);
                    }
                    break; //end case 0
                case 1:
                    if (c == '=') {
                        state = 2;
                        beginLexem = forward;
                        return new Token(sym.LE);
                    } else if (c == '>') {
                        state = 3;
                        beginLexem = forward;
                        return new Token(sym.NE);
                    } else if (c == '-') {
                        state = 25;
                    } else {
                        state = 4;
                        retrack();
                        return new Token(sym.LT);
                    } //end case 1
                    break;
                case 25:
                    if (c == '-') {
                        state = 26;
                        beginLexem = forward;
                        return new Token(sym.ASSIGN);
                    }
                case 6:
                    if (c == '=') {
                        state = 7;
                        beginLexem = forward;
                        return new Token(sym.GE);
                    } else {
                        state = 8;
                        retrack();
                        return new Token(sym.GT);
                    }
                    //ID
                case 10:
                    if (Character.isLetterOrDigit(c)) {
                        lessema += c;
                    } else {
                        state = 11;
                        retrack();
                        return installID(lessema);
                    }
                    break;
                //UNSIGNED NUMBERS
                case 12:
                    if(c == '.'){
                        state = 14;
                        lessema += c;
                    } else {
                        state = 20;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    }
                    break;
                case 13:
                    if (c == '.') {
                        state = 14;
                        lessema += c;
                    } else if(c == 'E' || c == 'e'){
                        state = 16;
                        lessema += c;
                    } else if (!Character.isDigit(c)) {
                        state = 20;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    } else { //we are still reading a number
                        lessema += c;
                        //the status is always 13 so it should not be changed
                    }
                    break;
                case 14:
                    if (Character.isDigit(c)) {
                        if(c != '0') {
                            state = 15;
                        }
                        else{
                            zeroCounter++;
                            state = 30;
                        }
                        lessema += c;
                    } else {
                        retrack(); //The retrack method is used twice because after the dot
                        retrack(); //there are successive zeros followed by a character other than a digit
                        //for example "50.00 "

                        //Having read a character that does not match the pattern of any token,
                        //we return the last correct token before the "."
                        String x = lessema.substring(0,lessema.length()-1);
                        return new Token(sym.NUMBER, x);
                    }
                    break;
                case 15:
                    if (Character.isDigit(c)) {
                        if(c == '0') {
                            state = 31;
                            zeroCounter++;
                        }
                        lessema += c;
                    } else if (c == 'E' || c == 'e') {
                        state = 16;
                        lessema += c;
                    } else {
                        state = 21;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    }
                    break;
                case 16:
                    if (Character.isDigit(c)) {
                        state = 18;
                        lessema += c;
                    } else if (c == '+' || c == '-') {
                        state = 17;
                        lessema += c;
                    }
                    break;
                case 17:
                    if (Character.isDigit(c)) {
                        state = 18;
                        lessema += c;
                    }
                    break;
                case 18:
                    if (!Character.isDigit(c)) {
                        state = 19;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    } else { //sto leggendo ancora un numero
                        lessema += c;
                    }
                    break;
                case 30:
                    if(Character.isDigit(c)){
                        if(c != '0') {
                            state = 15;
                            zeroCounter = 0; //zero because otherwise I also consider previous zeros
                        }
                        else{
                            zeroCounter++;
                        }
                        lessema+=c;
                    }
                    else{
                        forward -= zeroCounter;
                        retrack(); //The retrack method is used twice because after the dot
                        retrack(); //a character other than a digit is read
                        String x = lessema.substring(0,lessema.length()-zeroCounter-1);
                        return new Token(sym.NUMBER, x);
                    }
                    break;
                case 31:
                    if(Character.isDigit(c)){
                        if(c != '0') {
                            state = 15;
                            zeroCounter = 0; //zero because otherwise I also consider previous zeros
                        }
                        else{
                            zeroCounter++;
                        }
                        lessema+=c;
                    }
                    else{
                        forward -= zeroCounter;
                        retrack();
                        String x = lessema.substring(0,lessema.length()-zeroCounter);
                        return new Token(sym.NUMBER, x);
                    }
                    break;
                case 23:
                    if (!Character.isWhitespace(c)) {
                        state = 0;
                        retrack();
                    }
                    //else {I'm still reading a ws and so I'm staying in state 23}
                    break;
                default:
                    break;
            } //end switch
        }//end while
        return new Token(sym.EOF);
        //return null;
    }//end method

    private Token installID(String lessema){
        Token token;
        //I check if the recognized lexeme is a keyword or an id already declared
        token = symbolTable.contain(lessema);
        if(token != null){
            return token;
        } else { //if it isn't, then it's a new ID
            token =  new Token(sym.ID, String.valueOf(idSymTab));
            symbolTable.add(idSymTab, lessema, token);
            idSymTab++;
            return token;
        }
    }

    private void retrack(){
        //retracts a character in the file
        forward--;
        beginLexem = forward;
    }

    public void printStringTable(){
        System.out.println("\nSTRING TABLE");
        System.out.println("-----------------------------------------------");
        System.out.println(symbolTable);

    }
}// end class