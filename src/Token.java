public class Token {

    private int tokenCode;// this is a token identifier: it could also be an integer
    private String lexeme;

    public Token(int tokenCode, String lexeme){
        this.tokenCode = tokenCode;
        this.lexeme = lexeme;
    }

    public Token(int tokenCode){
        this.tokenCode = tokenCode;
        this.lexeme = null;
    }

    public int getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(int tokenCode) {
        this.tokenCode = tokenCode;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public boolean sameTokenType(int type){
        if(type == this.tokenCode){
            return true;
        }

        return false;
    }

    public boolean equals(Token obj) {
        if(obj.lexeme != null){
            if((obj.tokenCode == this.tokenCode) && obj.lexeme.equals(this.lexeme)){
                return true;
            }
        } else {
            if(obj.tokenCode == this.tokenCode){
                return true;
            }
        }

        return false;
    }

    public String toString(){
        return lexeme ==null? "<"+ tokenCode +">" : "<"+ tokenCode +", \""+ lexeme +"\">";
    }
}
