package types;

import java.util.HashMap;
import ast.*;

import java.util.List;
import crux.Symbol;


public class TypeChecker implements CommandVisitor {
    
    private HashMap<Command, Type> typeMap;
    private StringBuffer errorBuffer;
    public Symbol function = null;
    public HashMap <Command, Integer> statement_blocks = new HashMap<Command, Integer>();
    public Command                current_statement_block;

    /* Useful error strings:
     *
     * "Function " + func.name() + " has a void argument in position " + pos + "."
     * "Function " + func.name() + " has an error in argument in position " + pos + ": " + error.getMessage()
     *
     * "Function main has invalid signature."
     *
     * "Not all paths in function " + currentFunctionName + " have a return."
     *
     * "IfElseBranch requires bool condition not " + condType + "."
     * "WhileLoop requires bool condition not " + condType + "."
     *
     * "Function " + currentFunctionName + " returns " + currentReturnType + " not " + retType + "."
     *
     * "Variable " + varName + " has invalid type " + varType + "."
     * "Array " + arrayName + " has invalid base type " + baseType + "."
     */

    public TypeChecker()
    {
        typeMap = new HashMap<Command, Type>();
        errorBuffer = new StringBuffer();
    }

    private void reportError(int lineNum, int charPos, String message)
    {
        errorBuffer.append("TypeError(" + lineNum + "," + charPos + ")");
        errorBuffer.append("[" + message + "]" + "\n");
    }

    private void put(Command node, Type type)
    {
        if (type instanceof ErrorType) {
            reportError(node.lineNumber(), node.charPosition(), ((ErrorType)type).getMessage());
        }
        typeMap.put(node, type);
    }
    
    public Type getType(Command node)
    {
        return typeMap.get(node);
    }
    
    public Type getType(Expression node)
    {
        return typeMap.get(node);
    }
    
    public boolean check(Command ast)
    {
        ast.accept(this);
        return !hasError();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }

    @Override
    public void visit(ExpressionList node) {
        TypeList type_list = new TypeList();
        for (Expression expression: node)
        {
            expression.accept(this);
            type_list.append(getType(expression));
        }
        put(node, type_list);
        
    }

    @Override
    public void visit(DeclarationList node) {
        for (Declaration declaration : node)
        {declaration.accept(this);}
    }

    @Override
    public void visit(StatementList node) {
        for (Statement statement : node)
        { statement.accept(this);}
    }

    @Override
    public void visit(AddressOf node) {
        put(node,  new AddressType(node.symbol().type()) );

    }

    @Override
    public void visit(LiteralBool node) {
        put(node, new BoolType());
    }

    @Override
    public void visit(LiteralFloat node) {
         put(node, new FloatType());
    }

    @Override
    public void visit(LiteralInt node) {
        put(node, new IntType());
    }

    @Override
    public void visit(VariableDeclaration node) {
        Type type = node.symbol().type();
        
        if(     ( (type instanceof ErrorType)   || (type instanceof VoidType)  ))

        {reportError(node.lineNumber(), node.charPosition(), "Variable "
                     + node.symbol().name() + " has invalid type " + type + ".");}
    }

    @Override
    public void visit(ArrayDeclaration node) {
        Type type = getarraybase(node.symbol().type());
        
        if(     ( (type instanceof ErrorType)   || (type instanceof VoidType)  ))
        
        
        {reportError(node.lineNumber(), node.charPosition(), "Array "
                     + node.symbol().name() + " has invalid base type " + type + ".");}
    }
    
    
    public Type getarraybase(Type type)
    {

        if ( (type instanceof ArrayType))
        {return getarraybase(   ((ArrayType) type).base()   );}
        else
        {return type;}
    }

    @Override
    public void visit(FunctionDefinition node) {

        Symbol function_name = node.function();
        function = function_name;
        List<Symbol> arguments = node.arguments();
        StatementList body = node.body();
        current_statement_block = body;
        statement_blocks.put(current_statement_block ,0);
        
        //checking if main
        if( function_name.name().equals("main") &&   (  arguments.size() >0 || !  (   ((FuncType) function_name.type()).returnType() instanceof VoidType )      ))
        {
            reportError(node.lineNumber(), node.charPosition(), "Function main has invalid signature.");
            return;
        }
        
        //checking parameters
        int counter = 0;
        for(Symbol argument: arguments)
        {
            if( argument.type() instanceof VoidType)
            {
                reportError(node.lineNumber(), node.charPosition(),
                            "Function " + node.symbol().name() +" has a void argument in position " + counter + ".");
                return;
            }
            else if (argument.type() instanceof ErrorType)
            {
                String error = (   (ErrorType) argument.type()   ).getMessage();
                reportError(node.lineNumber(), node.charPosition(),
                            "Function " + node.symbol().name() +" has an error in argument in position " + counter + ": " +error);
                return;
            }
            
            counter+=1;
        }
        
        body.accept(this);
        
        //checking end...
        
        if (!(((FuncType) function_name.type()).returnType()                    instanceof VoidType) && statement_blocks.get(current_statement_block) ==0)
        {
            reportError(node.lineNumber(), node.charPosition(),
                        "Not all paths in function " + function.name() + " have a return.");
        }
        

        
    }

    @Override
    public void visit(Comparison node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType(node.leftSide()).compare(getType(node.rightSide())));
    }
    
    @Override
    public void visit(Addition node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType(node.leftSide()).add(getType(node.rightSide())));

        
        
    }
    
    @Override
    public void visit(Subtraction node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType(node.leftSide()).sub(getType(node.rightSide())));
    }
    
    @Override
    public void visit(Multiplication node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType(node.leftSide()).mul(getType(node.rightSide())));

    }
    
    @Override
    public void visit(Division node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType(node.leftSide()).div(getType(node.rightSide())));
    }
    
    @Override
    public void visit(LogicalAnd node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType(node.leftSide()).and(getType(node.rightSide())));
    }

    @Override
    public void visit(LogicalOr node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType(node.leftSide()).or(getType(node.rightSide())));
    }

    @Override
    public void visit(LogicalNot node) {
        node.expression().accept(this);
        put(node, getType(node.expression()).not());
    }
    
    @Override
    public void visit(Dereference node) {
        node.expression().accept(this);
        put(node, getType(node.expression()).deref());
    }

    @Override
    public void visit(Index node) {
        node.base().accept(this);
        node.amount().accept(this);
        put(node, getType(node.base()).index(getType(node.amount())));

        
    }

    @Override
    public void visit(Assignment node) {
        node.source().accept(this);
        node.destination().accept(this);
        put(node, getType(node.destination()).assign(getType(node.source())));
    }

    @Override
    public void visit(Call node) {
        node.arguments().accept(this);
        put(node,  node.function().type().call(getType(node.arguments())));
    }

    @Override
    public void visit(IfElseBranch node) {
        Command old_statement_block = current_statement_block;
        node.condition().accept(this);
        
        
        current_statement_block = node.thenBlock();
        statement_blocks.put(current_statement_block,0);
        node.thenBlock().accept(this);
        int if_return = statement_blocks.get(current_statement_block);
        
        current_statement_block = node.elseBlock();
        statement_blocks.put(current_statement_block,0);
        node.elseBlock().accept(this);
        int else_return = statement_blocks.get(current_statement_block);
        
        
        current_statement_block = old_statement_block;
        if(if_return >0 && else_return >0)
        {statement_blocks.put(current_statement_block     , statement_blocks.get(current_statement_block)+1         );}

        
        Type condition = getType(node.condition());
        if(!condition.equivalent(new BoolType()))
    
        {put(node, new ErrorType("IfElseBranch requires bool condition not " + condition + ".")    );}
    }

    @Override
    public void visit(WhileLoop node) {
        Command old_statement_block = current_statement_block;
        node.condition().accept(this);
        
        current_statement_block = node.body();
        statement_blocks.put(current_statement_block,0);
        node.body().accept(this);
        current_statement_block = old_statement_block;

        
        Type condition = getType(node.condition());
        if(!condition.equivalent(new BoolType()))

        {put(node, new ErrorType("WhileLoop requires bool condition not " + condition + ".")    );}
    
    }

    @Override
    public void visit(Return node) {
        node.argument().accept(this);
        Type returnval = getType(node.argument());
        
        if(                       function == null || !(returnval.equivalent(                  ( (FuncType) function.type()).returnType()             )   )     )
           {
               put(node, new ErrorType("Function " + function.name() + " returns " + ( (FuncType) function.type()).returnType()      + " not " + returnval + "."));
           }
   
        statement_blocks.put(current_statement_block     , statement_blocks.get(current_statement_block)+1         );

    }

    @Override
    public void visit(ast.Error node) {
        put(node, new ErrorType(node.message()));
    }
}
