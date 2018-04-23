package mips;

import java.util.HashMap;

import crux.Symbol;
import types.*;

public class ActivationRecord
{
    public int offset = 0;
    private static int fixedFrameSize = 2*4;
    private ast.FunctionDefinition func;
    private ActivationRecord parent;
    private int stackSize;
    private HashMap<Symbol, Integer> locals;
    private HashMap<Symbol, Integer> arguments;
    
    public static ActivationRecord newGlobalFrame()
    {
        return new GlobalFrame();
    }
    
    protected static int numBytes(Type type)
    {
    	if (type instanceof BoolType)
    		return 4;
        if (type instanceof IntType)
            return 4;
        if (type instanceof FloatType)
            return 4;
        if (type instanceof ArrayType) {
            ArrayType aType = (ArrayType)type;
            return aType.extent() * numBytes(aType.base());
        }
        throw new RuntimeException("No size known for " + type);
    }
    
    protected ActivationRecord()
    {
        this.func = null;
        this.parent = null;
        this.stackSize = 0;
        this.locals = null;
        this.arguments = null;
    }
    
    public ActivationRecord(ast.FunctionDefinition fd, ActivationRecord parent)
    {
        this.func = fd;
        this.parent = parent;
        this.stackSize = 0;
        this.locals = new HashMap<Symbol, Integer>();
        
        // map this function's parameters
        this.arguments = new HashMap<Symbol, Integer>();
        for (int i=fd.arguments().size()-1; i>=0; --i) {
            Symbol arg = fd.arguments().get(i);
            arguments.put(arg, offset);
            offset += numBytes(arg.type());
        }
    }
    
    public String name()
    {
        return func.symbol().name();
    }
    
    public ActivationRecord parent()
    {
        return parent;
    }
    
    public int stackSize()
    {
        return stackSize;
    }
    
    public void add(Program prog, ast.VariableDeclaration var)
    {
	locals.put(var.symbol(), stackSize);
        stackSize += numBytes(var.symbol().type());
        
    }
    
    public void add(Program prog, ast.ArrayDeclaration array)
    {
        
	locals.put(array.symbol(), stackSize);
        stackSize += numBytes(array.symbol().type());

    }
    
    public void getAddress(Program prog, String reg, Symbol sym)
    {
        if (locals.containsKey(sym))
        {
            prog.appendInstruction("addi " + reg + " , $fp, " + (-( locals.get(sym)  + fixedFrameSize)));
        }
        else if (arguments.containsKey(sym))
        {
            prog.appendInstruction("addi " + reg + " , $fp, " +   arguments.get(sym));
        }
        else
        {
            parent.getAddress(prog, reg, sym);
        }
    }
}

class GlobalFrame extends ActivationRecord
{
    public GlobalFrame()
    {
    }
    
    private String mangleDataname(String name)
    {
        return "cruxdata." + name;
    }
    
    @Override
    public void add(Program prog, ast.VariableDeclaration var)
    {
        prog.appendData( "cruxdata." +var.symbol().name() + ": .space " + numBytes(var.symbol().type()));
        
    }    
    
    @Override
    public void add(Program prog, ast.ArrayDeclaration array)
    {
        prog.appendData( "cruxdata." +array.symbol().name() + ": .space " + numBytes(array.symbol().type()));
    }
        
    @Override
    public void getAddress(Program prog, String reg, Symbol sym)
    {
        prog.appendInstruction("la " + reg +" , " + "cruxdata." + sym.name());
    }
}
