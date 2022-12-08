public class sym {
    public static final int EOF = 0; //plug: '\0'
    public static final int NUMBER = 1;
    public static final int EQ = 2;
    public static final int LT = 3;
    public static final int LE = 4;
    public static final int GT = 5;
    public static final int GE = 6;
    public static final int NE = 7;
    public static final int ASSIGN = 8;
    public static final int LPAR = 9;
    public static final int RPAR = 10;
    public static final int LBRAC = 11;
    public static final int RBRAC = 12;
    public static final int COMMA = 13;
    public static final int SEMICOLON = 14;
    public static final int ID = 15;
    public static final int IF = 16;
    public static final int THEN = 17;
    public static final int ELSE = 18;
    public static final int WHILE = 19;
    public static final int INT = 20;
    public static final int FLOAT = 21;
    public static final int ERROR = 22;
    public static final int DO = 23;

    public static final String[] tokens = {
            "EOF",
            "NUMBER",
            "EQ",
            "LT",
            "LE",
            "GT",
            "GE",
            "NE",
            "ASSIGN",
            "LPAR",
            "RPAR",
            "LBRAC",
            "RBRAC",
            "COMMA",
            "SEMICOLON",
            "ID",
            "IF",
            "THEN",
            "ELSE",
            "WHILE",
            "INT",
            "FLOAT",
            "ERROR",
            "DO"
    };
}
