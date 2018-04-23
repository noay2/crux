package crux;
import java.lang.Character;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Token {
	
	public static enum Kind {
                
        //Noah's ADDING THIS STUFF
		AND("and"),
		OR("or"),
		NOT("not"),
        LET("let"),
        VAR("var"),
        ARRAY("array"),
        FUNC("func"),
        IF("if"),
        ELSE("else"),
        WHILE("while"),
        TRUE("true"),
        FALSE("false"),
        RETURN("return"),
                
		//special
        OPEN_PAREN("("),
        CLOSE_PAREN(")"),
        OPEN_BRACE("{"),
        CLOSE_BRACE("}"),
        OPEN_BRACKET("["),
        CLOSE_BRACKET("]"),
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
        GREATER_EQUAL(">="),
        LESSER_EQUAL("<="),
        NOT_EQUAL("!="),
        EQUAL("=="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        ASSIGN("="),
        COMMA(","),
        SEMICOLON(";"),
        COLON(":"),
        CALL("::"),
                
                
        //non-static and weird
		IDENTIFIER(),
		INTEGER(),
		FLOAT(),
		ERROR(),
		EOF("");
		
		
                
                
		private String default_lexeme;
		
		Kind()
		{
			default_lexeme = null;
		}
		
		Kind(String lexeme)
		{
			default_lexeme = lexeme;
		}
		
		public boolean hasStaticLexeme()
		{
			return default_lexeme != null;
		}
                
        public String string_content()
        {
            return this.default_lexeme;
        }
		

	}
	
	private int lineNum;
	private int charPos;
	Kind kind;
	private String lexeme = "";
	
	
	// OPTIONAL: implement factory functions for some tokens, as you see fit
	/*           
	public static Token EOF(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.EOF;
		return tok;
	}
	*/

	//private Token(int lineNum, int charPos)
	//{
	//	this.lineNum = lineNum;
	//	this.charPos = charPos;
	//	
	//	// if we don't match anything, signal error
	//	this.kind = Kind.ERROR;
	//	this.lexeme = "No Lexeme Given";
	//}
	
	public Token(String lexeme, int lineNum, int charPos)
	{

		this.lineNum = lineNum;
		this.charPos = charPos;
        this.lexeme = lexeme;
        this.kind = traverse_through_kinds(lexeme);
		
		

	}
	
	public int lineNumber()
	{
		return lineNum;
	}
	
	public int charPosition()
	{
		return charPos;
	}
	
	public String lexeme()
	{
        return this.lexeme;
	}
    
    public Kind kind()
    {
        return this.kind;
    }
    
    public boolean is(Kind kind)
    {
        return this.kind == kind;
    }
	
	public String toString()
	{

        
        String str_kind = this.kind.toString();
        String extra_stuff = "";
        if("ERROR".equals(str_kind))
        {extra_stuff = "(Unexpected character: " + this.lexeme()+")";}
        else if("IDENTIFIER".equals(str_kind) || "INTEGER".equals(str_kind ) || "FLOAT".equals(str_kind))
        {extra_stuff = "(" + this.lexeme()+")";}
        
		return str_kind +extra_stuff+ "(lineNum:" + this.lineNum + ',' +" charPos:" + this.charPos +')';
	}
        
        
    public static Kind traverse_through_kinds(String lex_str)
    {
        for (Kind kind: Kind.values())
            if(kind.hasStaticLexeme())
            {
                if( lex_str.equals(kind.string_content()))
                {return kind;}
            }
            else
            {
                    if (kind == Kind.IDENTIFIER )
                    {
                        if (is_identifier(lex_str))
                        {return kind;}
                    }
        
                    else if (kind == Kind.INTEGER)
                    {
                        if (is_integer(lex_str))
                        {return kind;}
                    }
        
                    else if (kind == Kind.FLOAT)
                    {
                        if (is_float(lex_str))
                        {return kind;}
                    }
            }
        
            return Kind.ERROR;
                    
                    
            
        }
        
        public static boolean is_identifier(String lex_str)
        {
            
            if (! (lex_str.length() >0 && ( Character.isLetter(lex_str.charAt(0)) || lex_str.charAt(0) == '_')))
                return false;
            for (int i = 1; i<lex_str.length(); i++)
                if ( ! ( Character.isLetter(lex_str.charAt(i)) || lex_str.charAt(i) == '_' || ( lex_str.charAt(i) >= '0' && lex_str.charAt(i)  <= '9')))
                    return false;
            return true;
    
        }
        
        public static boolean is_integer(String lex_str)
        {
            if (! (lex_str.length() >0 && ( lex_str.charAt(0) >= '0' && lex_str.charAt(0)  <= '9')))
                return false;
            for (int i = 1; i<lex_str.length(); i++)
                if ( ! ( ( lex_str.charAt(i) >= '0' && lex_str.charAt(i)  <= '9')))
                    return false;
            return true;        }

        public static boolean is_float(String lex_str)
        {
            if (! (lex_str.length() >0 && ( lex_str.charAt(0) >= '0' && lex_str.charAt(0)  <= '9')))
                return false;
            
            int period_break = 0;
            
            for (int i = 1; i<lex_str.length(); i++)
                if (lex_str.charAt(i) == '.')
                {period_break = i;
                    break;}
                else if ( ! (  lex_str.charAt(i) >= '0' && lex_str.charAt(i)  <= '9'))
                return false;
            
            if (period_break ==0)
                return false;
            
            for (int i = period_break +1; i< lex_str.length();i++)
                if ( ! ( lex_str.charAt(i) >= '0' && lex_str.charAt(i)  <= '9'))
                    return false;
            return true;   
        }        
        
	

}
