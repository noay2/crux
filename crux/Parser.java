package crux;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Iterator;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import types.*;


public class Parser {
    public static String studentName = "Noah Djenguerian";
    public static String studentID = "26957883";
    public static String uciNetID = "ndjengue";
    
    
// Typing System ===================================
    
    private Type tryResolveType(String typeStr)
    {
        return Type.getBaseType(typeStr);
    }
    
    public TypeList param_to_type(List<Symbol>   list1)
    {
        TypeList typelist = new TypeList();
        for(int i = 0; i< list1.size(); ++i)
        {
            
            typelist.append(list1.get(i).type());
        }
        return typelist;
    }
    

// SymbolTable Management ==========================
    private SymbolTable symbolTable = new SymbolTable();
    
    private void initSymbolTable()
    {
        
        Symbol readInt = new Symbol("readInt");
        Symbol readFloat = new Symbol("readFloat");
        Symbol printBool = new Symbol("printBool");
        Symbol printInt = new Symbol("printInt");
        Symbol printFloat = new Symbol("printFloat");
        Symbol println = new Symbol("println");
        
        
        
        readInt.setType(new FuncType(new TypeList(), new IntType()));
        
        readFloat.setType(new FuncType(new TypeList(), new FloatType()));
        
        TypeList bool_input = new TypeList();
        bool_input.append(new BoolType());
        printBool.setType(new FuncType(bool_input, new VoidType()));
        
        TypeList int_input = new TypeList();
        int_input.append(new IntType());
        printInt.setType(new FuncType(int_input, new VoidType()));
        
        TypeList float_input = new TypeList();
        float_input.append(new FloatType());
        printFloat.setType(new FuncType(float_input, new VoidType()));
        
        println.setType(new FuncType(new TypeList(), new VoidType()));
        
        
        
        this.symbolTable.symbol_map.put("readInt", readInt);
        this.symbolTable.symbol_map.put("readFloat", readFloat);
        this.symbolTable.symbol_map.put("printBool",printBool);
        this.symbolTable.symbol_map.put("printInt",printInt);
        this.symbolTable.symbol_map.put("printFloat", printFloat);
        this.symbolTable.symbol_map.put("println",println);
    }
    
    private void enterScope()
    {
        this.symbolTable = new SymbolTable(this.symbolTable);
    }
    
    private void exitScope()
    {
        this.symbolTable = this.symbolTable.parent;
    }

    private Symbol tryResolveSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.lookup(name);
        } catch (SymbolNotFoundError e) {
            String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportResolveSymbolError(String name, int lineNum, int charPos)
    {
        String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    private Symbol tryDeclareSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.insert(name);
        } catch (RedeclarationError re) {
            String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportDeclareSymbolError(String name, int lineNum, int charPos)
    {
        String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }    

    
    
    
// Grammar Rule Reporting ==========================================
    private int parseTreeRecursionDepth = 0;
    private StringBuffer parseTreeBuffer = new StringBuffer();
    
    public void enterRule(NonTerminal nonTerminal) {
        String lineData = new String();
        for(int i = 0; i < parseTreeRecursionDepth; i++)
        {
            lineData += "  ";
        }
        lineData += nonTerminal.name();
        //System.out.println("descending " + lineData);
        parseTreeBuffer.append(lineData + "\n");
        parseTreeRecursionDepth++;
    }
    
    private void exitRule(NonTerminal nonTerminal)
    {
        parseTreeRecursionDepth--;
    }
    
    public String parseTreeReport()
    {
        return parseTreeBuffer.toString();
    }
    
// Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();
    
    private String reportSyntaxError(NonTerminal nt)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
    
    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    private class QuitParseException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        public QuitParseException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    private int lineNumber()
    {
        return currentToken.lineNumber();
    }
    
    private int charPosition()
    {
        return currentToken.charPosition();
    }
    
// Parser ==========================================
    private Scanner main_scanner;
    private Iterator<Token> scanner;
    private Token currentToken;
    
    public Parser(Scanner scanner)
    {
        this.main_scanner = scanner;
        this.scanner = scanner.iterator();
        this.currentToken = this.scanner.next();
    }
    
 //   public void parse()
   // {
     //   initSymbolTable();
       // try {
         //   program();
        //} catch (QuitParseException q) {
          //  errorBuffer.append("SyntaxError(" + lineNumber() + "," + charPosition() + ")");
            //errorBuffer.append("[Could not complete parsing.]");
        //}
    //}
    
    
    public ast.Command parse()
    {
        initSymbolTable();
        try {
            return program();
        } catch (QuitParseException q) {
            return new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");
        }
    }
    
// Helper Methods ==========================================
    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }
    
    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind());
    }
    
    private boolean accept(Token.Kind kind)
    {
        if (have(kind)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }
    
    private boolean accept(NonTerminal nt)
    {
        if (have(nt)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }
    
    private boolean expect(Token.Kind kind)
    {
        if (accept(kind))
            return true;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return false;
    }
    
    private boolean expect(NonTerminal nt)
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return false;
    }
    
    
    private Token expectRetrieve(Token.Kind kind)
    {
        Token tok = currentToken;
        if (accept(kind))
            return tok;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
    
    private Token expectRetrieve(NonTerminal nt)
    {
        Token tok = currentToken;
        if (accept(nt))
            return tok;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
    
    
// Grammar Rules =====================================================
    
    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public ast.Expression literal()
    {   int line_number = lineNumber();
        int character_position = charPosition();
        return  ast.Command.newLiteral( expectRetrieve(NonTerminal.LITERAL));
    }
    
    //type
    public Type type()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        return tryResolveType(expectRetrieve(Token.Kind.IDENTIFIER).lexeme());
    }
    
    // designator := IDENTIFIER { "[" expression0 "]" } .
    public ast.Expression designator()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Expression expression = new ast.AddressOf(line_number, character_position,
                                tryResolveSymbol(expectRetrieve((Token.Kind.IDENTIFIER))));
        while (accept(Token.Kind.OPEN_BRACKET))
        {
            expression = new ast.Index(lineNumber(), charPosition(), expression, expression0());
            expect(Token.Kind.CLOSE_BRACKET);
        }
        return expression;
    }
    
    //op0
    public Token  op0()
    {return expectRetrieve(NonTerminal.OP0);}
    
    //op1
    public Token  op1()
    {return expectRetrieve(NonTerminal.OP1);}
    
    //op2
    public Token  op2()
    {return expectRetrieve(NonTerminal.OP2);}
    
    //expression3
    public ast.Expression expression3()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Expression expression = null;
        if (have(Token.Kind.NOT))
        {
            Token token = expectRetrieve(Token.Kind.NOT);
            expression = ast.Command.newExpression(expression3(), token, null);
        }
        else if (accept(Token.Kind.OPEN_PAREN))
        {
            expression = expression0();
            expect(Token.Kind.CLOSE_PAREN);
        }
        else if (have(NonTerminal.DESIGNATOR))
        {expression = new ast.Dereference(line_number, character_position, designator());}
        else if (have(NonTerminal.CALL_EXPRESSION))
        {expression = call_expression();}
        else if (have(NonTerminal.LITERAL))
        {expression = literal();}
        else
        {expression = new ast.Error(line_number, character_position, "Could not complete parsing.");}
    
        return expression;
    }
    
    //expression2
    public ast.Expression expression2()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Expression expression = expression3();
        while (have(NonTerminal.OP2))
        {expression = ast.Command.newExpression(expression, op2(), expression3());}
        return expression;
    }
    
    //expression1
    public ast.Expression expression1()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Expression expression = expression2();
        while (have(NonTerminal.OP1))
        {expression = ast.Command.newExpression(expression, op1(), expression2());}
        return expression;
    }
    
    //expression0
    public ast.Expression expression0()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Expression expression = expression1();
        if (have(NonTerminal.OP0))
        {expression = ast.Command.newExpression(expression, op0(), expression1());}
        return expression;

    }
    
    //call_expression
    public ast.Call call_expression()
    {
        
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.CALL);
        Symbol symbol = tryResolveSymbol(expectRetrieve((Token.Kind.IDENTIFIER)));
        expect(Token.Kind.OPEN_PAREN);
        ast.ExpressionList expression_list = expression_list();
        expect(Token.Kind.CLOSE_PAREN);
        
        return new ast.Call(line_number,character_position,symbol,expression_list);
    }
    
    
    //expression_list
    public ast.ExpressionList expression_list()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.ExpressionList expression_list = new ast.ExpressionList(line_number, character_position);
        if (have(NonTerminal.EXPRESSION0))
        {
            expression_list.add(expression0());
            while(accept(Token.Kind.COMMA))
            {
                expression_list.add(expression0());
            }
        }
        return expression_list;
    }
    
    //parameter
    public Symbol parameter()
    {
        
        Symbol symbol = tryDeclareSymbol(expectRetrieve((Token.Kind.IDENTIFIER)));
        expect(Token.Kind.COLON);
        symbol.setType(type());
        return symbol;
    }
    
    //parameter_list
    public List<Symbol> parameter_list()
    {
        List<Symbol> parameter_list = new ArrayList<Symbol>();
        
        if (have(NonTerminal.PARAMETER))
        {
            parameter_list.add(parameter());
            while(accept(Token.Kind.COMMA))
            {
                parameter_list.add(parameter());
            }
        }
        
        return parameter_list;
    }
    
    //variable_declaration
    public ast.VariableDeclaration variable_declaration()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.VAR);
        Symbol symbol = tryDeclareSymbol(expectRetrieve((Token.Kind.IDENTIFIER)));
        expect(Token.Kind.COLON);
        symbol.setType(type());
        expect(Token.Kind.SEMICOLON);
        return new ast.VariableDeclaration(line_number, character_position, symbol);

    }
    
    //array_declaration
    public ast.ArrayDeclaration array_declaration()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.ARRAY);
        Symbol symbol = tryDeclareSymbol(expectRetrieve((Token.Kind.IDENTIFIER)));
        expect(Token.Kind.COLON);
        symbol.setType(type());
        List<Integer> list = new ArrayList<Integer>();
        expect(Token.Kind.OPEN_BRACKET);
        list.add(Integer.valueOf(expectRetrieve(Token.Kind.INTEGER).lexeme()) );
        expect(Token.Kind.CLOSE_BRACKET);
        while(accept(Token.Kind.OPEN_BRACKET))
        {
            list.add(Integer.valueOf(expectRetrieve(Token.Kind.INTEGER).lexeme()) );
            expect(Token.Kind.CLOSE_BRACKET);
        }
        expect(Token.Kind.SEMICOLON);
        for(int i = list.size() -1; i>=0; --i)
        {
            
            symbol.setType(new ArrayType(list.get(i), symbol.type()));}
        return new ast.ArrayDeclaration (line_number, character_position, symbol);
    }
    
    //function_definition
    public ast.FunctionDefinition function_definition()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.FUNC);
        Symbol symbol = tryDeclareSymbol(expectRetrieve((Token.Kind.IDENTIFIER)));
        expect(Token.Kind.OPEN_PAREN);
        enterScope();
        List<Symbol> parameter_list = parameter_list();
        expect(Token.Kind.CLOSE_PAREN);
        expect(Token.Kind.COLON);
        symbol.setType(new FuncType(param_to_type(parameter_list),type()));
        ast.StatementList statement_list = statement_block();
        exitScope();
        return new ast.FunctionDefinition(line_number, character_position, symbol, parameter_list, statement_list);

    }
    
    //declaration
    public ast.Declaration declaration()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Declaration declaration = null;
        if (have(NonTerminal.VARIABLE_DECLARATION))
        {declaration = variable_declaration();}
        else if (have(NonTerminal.ARRAY_DECLARATION))
        {declaration = array_declaration();}
        else if (have(NonTerminal.FUNCTION_DEFINITION))
        {declaration = function_definition();}
        else{declaration = new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");}
        
        return declaration;
    }
    
    //declaration_list
    public ast.DeclarationList declaration_list()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.DeclarationList declaration_list = new ast.DeclarationList(line_number, character_position);
        while(have(NonTerminal.DECLARATION))
        {declaration_list.add(declaration());}
        return declaration_list;
    }
    
    //assignment_statement
    public ast.Assignment assignment_statement()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.LET);
        ast.Expression designator = designator();
        expect(Token.Kind.ASSIGN);
        ast.Expression expression = expression0();
        expect(Token.Kind.SEMICOLON);
        return new ast.Assignment(line_number, character_position, designator, expression);
    }
    
    //call_statement
    public ast.Call call_statement()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Call statement = call_expression();
        expect(Token.Kind.SEMICOLON);
        return statement;
    }
    
    //if_statement
    public ast.IfElseBranch if_statement()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.IF);
        ast.Expression expression = expression0();
        enterScope();
        ast.StatementList then_statement = statement_block();
        exitScope();
        ast.StatementList else_statement = null;
        if(accept(Token.Kind.ELSE))
        {
            enterScope();
            else_statement = statement_block();
            exitScope();

        }
        else{else_statement = new ast.StatementList( lineNumber(),charPosition());}
        return new ast.IfElseBranch(line_number,character_position,expression,then_statement,else_statement);
    }
    
    //while_statement
    public ast.WhileLoop while_statement()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.WHILE);
        ast.Expression expression = expression0();
        enterScope();
        ast.StatementList statement_list = statement_block();
        exitScope();
        return new ast.WhileLoop(line_number, character_position, expression, statement_list);
    }
    
    //return_statement
    public ast.Return return_statement()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.RETURN);
        ast.Expression expression = expression0();
        expect(Token.Kind.SEMICOLON);
        return new ast.Return(line_number,character_position,expression);
    }
    
    //statement
    public ast.Statement statement()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.Statement statement = null;
        if (have(NonTerminal.VARIABLE_DECLARATION))
        {statement=variable_declaration();}
        else if (have(NonTerminal.ARRAY_DECLARATION))
        {statement=array_declaration();}
        else if (have(NonTerminal.CALL_STATEMENT))
        {statement=call_statement();}
        else if (have(NonTerminal.ASSIGNMENT_STATEMENT))
        {statement=assignment_statement();}
        else if (have(NonTerminal.IF_STATEMENT))
        {statement=if_statement();}
        else if (have(NonTerminal.WHILE_STATEMENT))
        {statement=while_statement();}
        else if (have(NonTerminal.RETURN_STATEMENT))
        {statement=return_statement();}
        else
        {statement = new ast.Error(line_number, character_position, "Could not complete parsing.");}
        return statement;
    }
    
    //statement_list
    public ast.StatementList statement_list()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.StatementList statement_list = new ast.StatementList(line_number, character_position);
        while(have(NonTerminal.STATEMENT))
        {statement_list.add(statement());}
        return statement_list;
    
    }
    
    //statement_block
    public ast.StatementList statement_block()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        expect(Token.Kind.OPEN_BRACE);
        ast.StatementList statement_list = statement_list();
        expect(Token.Kind.CLOSE_BRACE);
        return statement_list;
    }
    
    // program := declaration-list EOF .
    public ast.DeclarationList program()
    {
        int line_number = lineNumber();
        int character_position = charPosition();
        ast.DeclarationList declaration_list = declaration_list();
        expect(Token.Kind.EOF);
        return declaration_list;
    }
    
}



