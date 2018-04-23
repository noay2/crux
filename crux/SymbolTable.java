package crux;
import java.util.Map;
import java.util.LinkedHashMap;
public class SymbolTable {
    
    
    
    public SymbolTable()
    {
        
    }
    
    public SymbolTable(SymbolTable Parent)
    {
        this.parent = Parent;
        this.depth = Parent.depth +1;
    }
    
    public Symbol lookup(String name) throws SymbolNotFoundError
    {
        
        
        Symbol acquired = this.symbol_map.get(name);
        if (acquired != null)
        {
            return acquired;
        }
        else if (this.parent != null)
        {
            return this.parent.lookup(name);
        }
        
        throw new SymbolNotFoundError(name);
    }
       
    public Symbol insert(String name) throws RedeclarationError
    {
        
        
        Symbol acquired = this.symbol_map.get(name);
        if (acquired != null)
        {throw new RedeclarationError(acquired);}
        

        acquired =new Symbol(name);
        this.symbol_map.put(name, acquired);
        return acquired;
        
    }
    

    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (this.parent !=null)
            sb.append(parent.toString());
        
        String indent = new String();
        for (int i = 0; i < depth; i++) {
            indent += "  ";
        }
        
        for (Symbol s: this.symbol_map.values())
        {
            sb.append(indent + s.toString() + "\n");
        }

        return sb.toString();
    }
    
    public Map<String,Symbol> symbol_map = new LinkedHashMap<String,Symbol>();
    public SymbolTable parent = null;
    public int depth;
}

class SymbolNotFoundError extends Error
{
    private static final long serialVersionUID = 1L;
    private String name;
    
    SymbolNotFoundError(String name)
    {
        this.name = name;
    }
    
    public String name()
    {
        return name;
    }
}

class RedeclarationError extends Error
{
    private static final long serialVersionUID = 1L;

    public RedeclarationError(Symbol sym)
    {
        super("Symbol " + sym + " being redeclared.");
    }
}
