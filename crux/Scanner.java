package crux;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Scanner implements Iterable<Token> {
    public static String studentName = "Noah Djenguerian";
    public static String studentID = "26957883";
    public static String uciNetID = "ndjengue";
    
    private int lineNum = 1;  // current line count
    private int charPos = 0;  // character offset for current line
    private int nextChar= 0; // contains the next char (-1 == EOF)
    private Reader input;
    
    

    
    
    Scanner(Reader reader)
    {
        
        this.input = reader;
        this.getChar();
    }
    
    
    
    public class InfoString
    {
        public int stringLine;
        public int stringPos;
        public String string_buffer = "";
        
        InfoString()
        {}
    }
    
    
    private InfoString makeString()
    {
        InfoString infostring = new Scanner.InfoString();
        infostring.stringLine= this.lineNum;
        infostring.stringPos = this.charPos;
        

        
        while(true)
        {

            //comment out
            if ((char) this.nextChar == '/' )
            {
                if (infostring.string_buffer != "")
                {return infostring;}
                
                int nextnext = 0;
                try{ nextnext =  this.input.read();} catch(IOException i){}
                this.charPos +=1;
                if( (char) nextnext == '/')
                {
                    moveToNextLine();
                    adjustNewLine();
                    getChar();
                    infostring.stringPos = this.charPos;
                    infostring.stringLine= this.lineNum;
                }
                else
                {
                    infostring.string_buffer += (char) this.nextChar;
                    this.nextChar = nextnext;
                    return infostring;
                }
            }
        
        

        
            //special character
            else if (this.isSpecialChar("" + (char) this.nextChar ))
            {

                if (infostring.string_buffer != "")
                {return infostring;}
                
                int nextnext = 0;
                try{ nextnext =  this.input.read();} catch(IOException i){}
                this.charPos +=1;
                if( this.isSpecialChar("" + ((char) this.nextChar) + ((char) nextnext)))
                {

                    infostring.string_buffer += ""+((char) this.nextChar)  + ((char) nextnext);
                    this.getChar();
                    return infostring;
                }
                else
                {
                    infostring.string_buffer = "" + (char) this.nextChar;
                    this.nextChar = nextnext;
                    return infostring;
                }
            }
    
            
            
            //eof
            else if (this.nextChar == -1)
            {
                
                
                return infostring;}
            
            
            
            //newline
            else if ( this.nextChar == 10 )
            {
                if (infostring.string_buffer != "")
                {return infostring;}
                
                
                adjustNewLine();
                this.getChar();
                infostring.stringLine= this.lineNum;
                infostring.stringPos = this.charPos;
            }
        
            
            //whitespace or horizontal tab
            else if (((char) this.nextChar == ' ' )|| this.nextChar == 9 )
            {
                if (infostring.string_buffer != "" )
                {return infostring;}
    
                this.getChar();
                infostring.stringLine= this.lineNum;
                infostring.stringPos = this.charPos;
            }

            
            //invalid character
            else if  ( "ERROR".equals((Token.traverse_through_kinds(infostring.string_buffer + (char) this.nextChar)).toString()))
            {
                if (infostring.string_buffer != "")
                {return infostring;}
                
                infostring.string_buffer = "" + (char) this.nextChar;
                this.getChar();
                return infostring;
                
            }
            
            //normal char
            else
            {
                infostring.string_buffer += (char) this.nextChar;
                this.getChar();
            }
        }
        
    }
    private void getChar()
    {
        if (this.nextChar == -1){return;}
        try
        {
            this.nextChar =  this.input.read();
        }
        
        catch(IOException i)
        {
            this.nextChar =  -1;
        }
        this.charPos +=1;
        
    }
    private void moveToNextLine()
    {
        
        while(! ( this.nextChar == 10 || this.nextChar ==-1  ) )
        {
            getChar();
        }

        
        
    }
    
    
    
    private void adjustNewLine()
    {
        if(this.nextChar ==-1){return;}
        this.nextChar = 0;
        this.lineNum +=1;
        this.charPos = 0;
    }
    
    private boolean isSpecialChar( String str)
    {
        String [] special_chars = new String[] {"(",")","{","}","[","]","+","-","*","/",">=","<=","!=","==",">","<","=",",",";",":","::","!"};
        for (String s: special_chars){
            
            if (str.equals( s))
            {
                
                return true;}}
        return false;
        
    }
    
    
    
    /* Invariants:
     *  1. call assumes that nextChar is already holding an unread character
     *  2. return leaves nextChar containing an untokenized character
     */
    
    public Iterator<Token> iterator()
    {
        return new TokenIterator();
    }
    public class TokenIterator implements Iterator<Token>
    {
        public boolean hasNext()
        {
            return true;
        }
        public Token next()
        {
            InfoString new_info = Scanner.this.makeString();
            Token token = new Token(new_info.string_buffer,
                                    new_info.stringLine, new_info.stringPos);
            return token;
        }
    }

}
