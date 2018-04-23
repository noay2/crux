package mips;

import java.util.regex.Pattern;
import java.util.HashMap;


import ast.*;
import types.*;

public class CodeGen implements ast.CommandVisitor {
    
    
    public HashMap<String, Integer> argument_size = new HashMap<String, Integer> ();
    public String return_p;
    private StringBuffer errorBuffer = new StringBuffer();
    private TypeChecker tc;
    private Program program;
    private ActivationRecord currentFunction;

    public CodeGen(TypeChecker tc)
    {
        this.tc = tc;
        this.program = new Program();
        argument_size.put("func.printFloat", 4);
        argument_size.put("func.printInt", 4);
        argument_size.put("func.printBool", 4);
        argument_size.put("func.readInt", 0);
        argument_size.put("func.readFloat", 0);
        argument_size.put("func.println", 0);
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }

    private class CodeGenException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        public CodeGenException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    public boolean generate(Command ast)
    {
        try {
            currentFunction = ActivationRecord.newGlobalFrame();
            ast.accept(this);
            return !hasError();
        } catch (CodeGenException e) {
            return false;
        }
    }
    
    public Program getProgram()
    {
        return program;
    }

    @Override
    public void visit(ExpressionList node) {
        for (Expression expression : node) {
            expression.accept(this);
        }
    }

    @Override
    public void visit(DeclarationList node) {
        for (Declaration declaration : node) {
            declaration.accept(this);
        }
    }

    @Override
    public void visit(StatementList node) {
        for (Statement statement : node)
        {
            statement.accept(this);
            if (statement instanceof Call)
            {
                Type returnval = ((FuncType) ((Call) statement) .function().type()).returnType();
                if( returnval instanceof BoolType || returnval instanceof IntType || returnval instanceof FloatType)
                {
                    program.popInt("$t0");
                }

            }
        }
    }

    @Override
    public void visit(AddressOf node) {
        currentFunction.getAddress(program, "$t0" , node.symbol());
        program.pushInt("$t0");
    }

    @Override
    public void visit(LiteralBool node) {
        program.appendInstruction("addi $t0, $0, "+  (String) (node.value() == LiteralBool.Value.TRUE ? "1" :"0"));
        program.pushInt("$t0");
    }

    @Override
    public void visit(LiteralFloat node) {
        program.appendInstruction("li.s $f4, " + node.value());
        program.pushFloat("$f4");
    }

    @Override
    public void visit(LiteralInt node) {
        program.appendInstruction("addi $t0, $0, "+ node.value());
        program.pushInt("$t0");
    }

    @Override
    public void visit(VariableDeclaration node) {
        currentFunction.add(program, node);
    }

    @Override
    public void visit(ArrayDeclaration node) {
        currentFunction.add(program, node);
    }

    @Override
    public void visit(FunctionDefinition node) {
        String name = node.symbol().name().equals("main")? node.symbol().name(): "func."+ node.symbol().name();
	return_p = program.newLabel();
        int location = program.appendInstruction(name +":");
        ActivationRecord old_func = currentFunction;



        currentFunction = new ActivationRecord(node, old_func);
        argument_size.put(name, currentFunction.offset);
        node.body().accept(this);
        program.insertPrologue((location + 1), currentFunction.stackSize());


	
	program.appendInstruction(return_p + ":");
        Type returnval = ((FuncType) ( node) .function().type()).returnType();
        if (returnval instanceof IntType || returnval instanceof BoolType || returnval instanceof FloatType )
        {program.popInt("$v0");}
        


        if (!name.equals("main"))
        {program.appendEpilogue(currentFunction.stackSize());
        currentFunction = old_func;
        }
        else
        {program.appendExitSequence();}
        
           
    }

    @Override
    public void visit(Addition node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        
        if (  tc.getType( node.leftSide() )instanceof IntType)
        {
            program.popInt("$t0");
            program.popInt("$t1");
            program.appendInstruction("add $t1, $t1, $t0");
            program.pushInt("$t1");
        }
        
        else
        {
            program.popFloat("$f4");
            program.popFloat("$f5");
            program.appendInstruction("add.s $f5, $f5, $f4");
            program.pushFloat("$f5");
        }    }

    @Override
    public void visit(Subtraction node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        
        if (  tc.getType( node.leftSide() )instanceof IntType)
        {
            program.popInt("$t0");
            program.popInt("$t1");
            program.appendInstruction("sub $t1, $t1, $t0");
            program.pushInt("$t1");
        }
        
        else
        {
            program.popFloat("$f4");
            program.popFloat("$f5");
            program.appendInstruction("sub.s $f5, $f5, $f4");
            program.pushFloat("$f5");
        }
        
        
    }

    @Override
    public void visit(Multiplication node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        
        if (  tc.getType( node.leftSide() )instanceof IntType)
        {
            program.popInt("$t0");
            program.popInt("$t1");
            program.appendInstruction("mul $t1, $t1, $t0");
            program.pushInt("$t1");
        }
        
        else
        {
            program.popFloat("$f4");
            program.popFloat("$f5");
            program.appendInstruction("mul.s $f5, $f5, $f4");
            program.pushFloat("$f5");
        }    }

    @Override
    public void visit(Division node)
    {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        
        if (  tc.getType( node.leftSide() )instanceof IntType)
        {
            program.popInt("$t0");
            program.popInt("$t1");
            program.appendInstruction("div $t1, $t1, $t0");
            program.pushInt("$t1");
        }
        
        else
        {
            program.popFloat("$f4");
            program.popFloat("$f5");
            program.appendInstruction("div.s $f5, $f5, $f4");
            program.pushFloat("$f5");
        }
    }

    @Override
    public void visit(LogicalAnd node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        
        
        program.popInt("$t0");
        program.popInt("$t1");
        program.appendInstruction("and $t1, $t1, $t0");
        program.pushInt("$t1");
        

    }

    @Override
    public void visit(LogicalOr node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        
        
        program.popInt("$t0");
        program.popInt("$t1");
        program.appendInstruction("or $t1, $t1, $t0");
        program.pushInt("$t1");
        

    }
    
    @Override
    public void visit(LogicalNot node)
    {
        node.expression().accept(this);
        program.popInt("$t0");
        program.appendInstruction("not $t0, $t0");
        program.pushInt("$t0");
        
     }

    @Override
    public void visit(Comparison node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        
        if (  tc.getType( node.leftSide() )instanceof IntType)
        {
            program.popInt("$t0");
            program.popInt("$t1");
            
            
            if(node.operation() == Comparison.Operation.LT)
            {program.appendInstruction("slt $t1, $t1, $t0");}
            
            else if(node.operation() == Comparison.Operation.LE)
            {program.appendInstruction("sle $t1, $t1, $t0");}
            
            else if(node.operation() == Comparison.Operation.EQ)
            {program.appendInstruction("seq $t1, $t1, $t0");}
            
            else if(node.operation() == Comparison.Operation.NE)
            {program.appendInstruction("sne $t1, $t1, $t0");}
            
            else if(node.operation() == Comparison.Operation.GE)
            {program.appendInstruction("sge $t1, $t1, $t0");}
            
            else if(node.operation() == Comparison.Operation.GT)
            {program.appendInstruction("sgt $t1, $t1, $t0");}
            
            
            program.pushInt("$t1");

        }
        
        else
        {
            program.popFloat("$f4");
            program.popFloat("$f5");
            
            boolean i;
            if(node.operation() == Comparison.Operation.LT)
            {program.appendInstruction("c.lt.s $f5, $f4");
            i = true;}
            
            else if(node.operation() == Comparison.Operation.LE)
            {program.appendInstruction("c.le.s $f5, $f4");
            i = true;}
            
            else if(node.operation() == Comparison.Operation.EQ)
            {program.appendInstruction("c.eq.s $f5, $f4");
            i = true;}
            
            else if(node.operation() == Comparison.Operation.NE)
            {program.appendInstruction("c.eq.s $f5, $f4");
            i = false;}
            
            else if(node.operation() == Comparison.Operation.GE)
            {program.appendInstruction("c.lt.s  $f5, $f4");
            i = false;}
            
            else if(node.operation() == Comparison.Operation.GT)
            {program.appendInstruction("c.le.s $f5, $f4");
            i = false;}

	   else{i = true;}
            
            String else_state = program.newLabel();
            String end_state =  program.newLabel();
            program.appendInstruction("bc1f "+ else_state);
            program.appendInstruction("addi $t1, " + "$0, " + (i ? "1" : "0") );
            program.appendInstruction("j " +end_state);
            program.appendInstruction(else_state+ ": "   + "addi $t1, " + "$0, " + (i ? "0" : "1" ) );
            program.appendInstruction(end_state + ": ");
            
            program.pushInt("$t1");

        }
    
        
    
    
    
    }

    @Override
    public void visit(Dereference node) {
        node.expression().accept(this);

        program.popInt("$t0");
        program.appendInstruction("lw $t0, 0($t0)");
        program.pushInt("$t0");
    }

    @Override
    public void visit(Index node) {
        node.base().accept(this);
        node.amount().accept(this);

	program.popInt("$t1");
	program.popInt("$t0");
	AddressType t = (AddressType) (tc.getType((Expression) node));
	program.appendInstruction("mul $t1, $t1, "+ActivationRecord.numBytes(t.base()));
	program.appendInstruction("add  $t1, $t1, $t0");
	program.pushInt("$t1");

	

    }

    @Override
    public void visit(Assignment node) {
        node.source().accept(this);
        node.destination().accept(this);

        program.popInt("$t1");
        program.popInt("$t0");
        program.appendInstruction("sw $t0, 0($t1)");
        

    }

    @Override
    public void visit(Call node) 
    {
        String name = node.function().name().equals("main")
                        ? node.function().name()
                        : "func."+ node.function().name();
	node.arguments().accept(this);


	program.appendInstruction("jal " + name);
    program.appendInstruction("addi $sp, $sp, " +
                              argument_size.get(name));
    Type returnval = ((FuncType) node.function().type()).returnType();
    if (returnval instanceof IntType || returnval instanceof BoolType || returnval instanceof FloatType)
    {
        program.appendInstruction("subu $sp, $sp, 4");
        program.appendInstruction("sw $v0, 0($sp)");
    }

	
        
    }

    @Override
    public void visit(IfElseBranch node) {
        String else_state = program.newLabel();
        String end_state = program.newLabel();
        
        node.condition().accept(this);
        program.popInt("$t0");
        program.appendInstruction("blez $t0, "+else_state );
        node.thenBlock().accept(this);
        program.appendInstruction( "j " + end_state  );
        program.appendInstruction(else_state + ": " );
        node.elseBlock().accept(this);
        program.appendInstruction(end_state + ": " );
        
    
    
    }

    @Override
    public void visit(WhileLoop node) {
        String update = program.newLabel();
        String end = program.newLabel();
    
        program.appendInstruction(update + ": " );
        node.condition().accept(this);
        program.popInt("$t0");
        program.appendInstruction("blez $t0, "+end );
        node.body().accept(this);
        program.appendInstruction( "j " + update  );
        program.appendInstruction(end + ": " );


        
        
        
        
    }

    @Override
    public void visit(Return node) {
        node.argument().accept(this);
	program.appendInstruction("j " + return_p);
	  

    }

    @Override
    public void visit(ast.Error node) {
        String message = "CodeGen cannot compile a " + node;
        errorBuffer.append(message);
        throw new CodeGenException(message);
    }
}
