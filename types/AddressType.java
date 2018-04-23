package types;

public class AddressType extends Type {
    
    private Type base;
    
    public AddressType(Type base)
    {
        this.base = base;
    }
    
    public Type base()
    {
        return base;
    }

    
    @Override
    public String toString()
    {
        return "Address(" + base + ")";
    }
    
    @Override
    public Type deref()
    {return base;}

    @Override
    public Type assign(Type source)
    {
        if(  !base.equivalent(source)  )
        {return super.assign(source);}
        
        else{return null;}
    }
    
    @Override
    public Type index(Type that)
    {
        if (!(base instanceof ArrayType)        ||      !(that instanceof IntType))
        {return super.index(that);}
        
        else{return new AddressType(base.index(that));}
    }

    @Override
    public boolean equivalent(Type that) {
        if (that == null)
            return false;
        if (!(that instanceof AddressType))
            return false;
        
        AddressType aType = (AddressType)that;
        return this.base.equivalent(aType.base);
    }
}
