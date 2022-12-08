/*
* Implementazione di un Parser a Discesa Ricorsiva
* secondo la seguente Grammatica G = (N, T, S, P)
*
* N = {Program, Stmt, Expr, Program1, Expr1, Expr2, Relop}
* T = {ID, IF, THEN, ELSE, LT, GT, LE, GE, NE, EQ, NUMBER, ;, ASSIGN, WHILE, DO, EOF}
* S
* P = {
*       S -> Program EOF
*
*       Program -> Stmt Program1
*       Program1 -> ; Stmt Program1 | ɛ
*
*       Stmt -> IF Expr THEN Stmt ELSE Stmt | ID ASSIGN Expr | DO Stmt WHILE Expr
*
*       Expr -> Expr2 Expr1
*       Expr1 -> Relop Expr2 Expr1 | ɛ
*       Expr2 -> ID | NUMBER
*
*       Relop -> LT | GT | LE | GE | NE | EQ
*     }
* */

public class Parser {

    private static final boolean DEBUG = false;
    private Lexer lexer; //instance of the lexical analyzer
    private Token currentToken; //the current token being parsed
    private int operators[] = {sym.LE, sym.LT, sym.GE, sym.GT, sym.EQ, sym.NE};

    public Parser(String filePath){
        lexer = new Lexer();
        lexer.initialize(filePath);
    }

    public void printStringTable(){
        lexer.printStringTable();
    }

    public boolean S(){
        if(DEBUG) System.out.println("I'm in St");
        currentToken = lexer.nextToken();

        if(!Program()){
            return false;
        } else {
            if(!EOF()){
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean Program(){
        if(DEBUG) System.out.println("I'm scheduled");

        if(!Stmt()){
            return false;
        } else {//I don't advance to the next token because Stmt() already has
            if(!Program1()){
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean Program1(){
        if(DEBUG) System.out.println("I'm in program1 and the current token is: " + currentToken);
        if(!currentToken.sameTokenType(sym.SEMICOLON)){
            //if i didn't read the ";" then it could be the production Program1 -> ɛ
            return true;
        } else { //has been read ";"
            if(DEBUG) System.out.println("Program1: I have read SEMICOLON");
            currentToken = lexer.nextToken();
            if(DEBUG) System.out.println("Program1: After SEMICOLON the current token is: " + currentToken);
            if(!Stmt()){
                if(DEBUG) System.out.println("I haven't read a Stmt() after that \";\"");
                return false;
            } else {//I read a Stmt()
                if(!Program1()){ //Program1 -> ; Stmt Program1
                    return false;
                }
                return true;
            }
        }
    }

    public boolean Stmt(){
        if(DEBUG) System.out.println("are in statement and the current token is: " + currentToken);

        if(!currentToken.sameTokenType(sym.IF)){
            //if it's not IF, it could be the production Stmt -> ID ASSIGN Expr
            if(!currentToken.sameTokenType(sym.ID)){
                //if it's not ID, it could be the production Stmt -> DO Stmt WHILE Expr
                if(!currentToken.sameTokenType(sym.DO)){
                    return false;
                } else {//i read do
                    if(DEBUG) System.out.println("Stmt: DO");
                    currentToken = lexer.nextToken();
                    if(!Stmt()){
                        return false;
                    } else { //I've read Stmt() but I don't advance to the next token because another case takes care of it
                        if(!currentToken.sameTokenType(sym.WHILE)){
                            return false;
                        } else {//i read while
                            if(DEBUG) System.out.println("Stmt: WHILE");
                            currentToken = lexer.nextToken();
                            if(!Expr()){
                                return false;
                            } else {//I read Expr() and I don't advance to next token because Expr() already did
                                return true;
                            }
                        }
                    }
                } //end of production Stmt -> DO Stmt WHILE Expr

            } else { //Stmt -> ID ASSIGN Expr
                if(DEBUG) System.out.println("Stmt: ID");
                currentToken = lexer.nextToken();
                if(!currentToken.sameTokenType(sym.ASSIGN)){
                    return false;
                } else { //I read ASSIGN (ie <-- )
                    if(DEBUG) System.out.println("Stmt: ASSIGN and the current token is: " + currentToken);
                    currentToken = lexer.nextToken();
                    if(DEBUG) System.out.println("current token after the ASSIGN case prompted for next: " + currentToken);
                    if(!Expr()){
                        return false;
                    } else {//I read Expr() and I don't advance to next token because Expr() already did
                        return true;
                    }
                }
            } //end of production Stmt -> ID ASSIGN Expr

        } else { //i read IF and then go to next token Stmt -> IF Expr THEN Stmt ELSE Stmt
            if(DEBUG) System.out.println("Stmt: IF");
            currentToken = lexer.nextToken();
            if(!Expr()){
                if(DEBUG) System.out.println("Stmt: I did NOT read Expr() after IF");
                return false;
            } else {//I read Expr() but I don't advance to next token because Expr() already did
                if(DEBUG) System.out.println("Stmt: I read Expr() after IF");
                if(!currentToken.sameTokenType(sym.THEN)){
                    if(DEBUG) System.out.println("Current Token: " + currentToken);
                    if(DEBUG) System.out.println("Stmt: I did NOT read THEN after IF");
                    return false;
                } else {//ho letto then
                    if(DEBUG) System.out.println("Stmt: THEN");
                    currentToken = lexer.nextToken();
                    if(!Stmt()){
                        return false;
                    } else {//I've read Stmt() but I don't advance the token because another case is dealing with it
                        if(!currentToken.sameTokenType(sym.ELSE)){
                            return false;
                        } else {//i read else
                            if(DEBUG) System.out.println("Stmt: ELSE");
                            currentToken = lexer.nextToken();
                            if(!Stmt()){
                                return false;
                            } else { //I've read Stmt() but I don't advance the token because another case is dealing with it
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean Expr(){
        if(DEBUG) System.out.println("i am in expr and the current token is: " + currentToken);

        if(!Expr2()) {
            return false;
        }
        else{// I don't advance to the next token because Expr2() already has
            if(!Expr1()){
                return false;
            }
            else{
                return true;
            }
        }

    }

    public boolean Expr1(){
        if(DEBUG) System.out.println("I'm in expr1 - currenttoken: "+currentToken);
        if(!Relop()){
            //I haven't read RELOP but it could be the Production Expr1 -> ɛ
            if(DEBUG) System.out.println("I haven't read relop");
            return true;
        } else { //we found a comparison operator but we don't advance to the next token because Relop() already did
            if(DEBUG) System.out.println("I read relop");
            if(!Expr2()){
                return false;
            } else { //we found ID or NUMBER
                if(!Expr1()){ //Expr1 -> ɛ
                    currentToken = lexer.nextToken();
                    return true;
                } //are in the case of Expr -> ɛ
                return true;
            }
        }
    }

    public boolean Expr2(){
        if(DEBUG) System.out.println("i am in expr2 and the current token is: " + currentToken);
        if(!currentToken.sameTokenType(sym.ID)){
            //I haven't read an ID but it could be the output Expr2 -> NUMBER
            if(!currentToken.sameTokenType(sym.NUMBER)){
                return false;
            } else { //I read a NUMBER and then advance to the next token
                if(DEBUG) System.out.println("Expr2: NUMBER");
                currentToken = lexer.nextToken();
                return true;
            }
        } else { //i read an id and advance to the next token
            if(DEBUG) System.out.println("Expr2: ID");
            currentToken = lexer.nextToken();
            return true;
        }
    }

    public boolean Relop(){
        if(DEBUG) System.out.println("I'm in relop");
        if(DEBUG) System.out.println(currentToken);
        for(int x : operators){
            if(currentToken.sameTokenType(x)) {
                currentToken = lexer.nextToken();
                return true;
            }
        }
        return false;
    }

    public boolean EOF(){
        if(DEBUG) System.out.println("i'm in eof");
        if(!currentToken.sameTokenType(sym.EOF)){
            return false;
        }
        return true;
    }

}
