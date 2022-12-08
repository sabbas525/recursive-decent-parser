# recursive-decent-parser
 The grammar used for the development of the recursive drop descent parser is as follows:
G = (N, T, S, P)
N = {Program, Stmt, Expr, Program1, Expr1, Expr2, Relop}
T = {ID, IF, THEN, ELSE, LT, GT, LE, GE, NE, EQ, NUMBER, ;, ASSIGN, WHILE, DO, EOF}
S
P = {
       S -> Program EOF
       Program -> Stmt Program1
       Program1 -> ; Stmt Program1 | ɛ
       Stmt -> IF Expr THEN Stmt ELSE Stmt | ID ASSIGN Expr | DO Stmt WHILE Expr
       Expr -> Expr2 Expr1
       Expr1 -> Relop Expr2 Expr1 | ɛ
       Expr2 -> ID | NUMBER
       Relop -> LT | GT | LE | GE | NE | EQ
      }
      
  Testing

Test files have been provided to verify the correct functioning of the parser in specific cases. 
The results obtained following the tests carried out are shown in the following table:

Parser Test Output file

file_source1	Valid

file_source2	Invalid

file_source3	Invalid

file_source4	Valid

file_source5	Invalid

file_source6	Valid

file_source7	Invalid

file_source8	Invalid

file_source9	Valid

file_source10	Valid
