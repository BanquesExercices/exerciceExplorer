/*
 * 04/23/2019
 *
 * Maxence Miguel-Brebion 
 *
 * Latex Parser : improve code and format
 * 
 * 
 */
package TextEditor.Tex;
import java.io.IOException;
%%

%public
%class LatexParserImpl
%unicode
%extends LatexParserBase
%type boolean

%{

	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public LatexParserImpl() {
            super();
	}

        
 @Override
    public void startParsing() {
        yybegin(YYINITIAL);
        try {
            yylex();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
            

%}


Letter					= ([azertyuiopmlkjhgfdsqwxcvbnAZERTYUIOPMLKJHGFDSQWXCVBNéèêëàâùûüöôç@ÉïîÀ])
Digit					= ([0-9])
Number                                   =  {Digit}+("."{Digit}*)?
LetterOrUnderscore		        = ({Letter}|[_])
AnyChar					= ({LetterOrUnderscore}|{Digit}|[\-])
Appost                                   = (['])
Whitespace				= ([ \t\f])


URLSubDelim				= ([#><\^?\!\&\(\)\*\+:\.,\/;=\[\]\\])
OpenBracket                              =([\{])
CloseBracket                              =([\}])
RAnyChar                                 = ({AnyChar} | {OpenBracket} | {CloseBracket} | {URLSubDelim}  )
RAnyCharWB                                 = ({AnyChar} |  {URLSubDelim} | (\\\{) | (\\\}) )

Sentence                                 = ({RAnyChar} | {Whitespace} |{Appost} )
SentenceWB                                 = ({RAnyCharWB} | {Whitespace} |{Appost} )

BLOCKbegin = ("\\begin{"{AnyChar}+"}")("\n")?
BLOCKend = ("\\end{"{AnyChar}+"}")("\n")?
ADDQbegin = ("\\addQ{")("\n")?
MLEbegin = ("\\eq{")("\n")?
ADDQloginBegin = ("\\addQ["{AnyChar}+"]{")
TCOLSAbegin = ("\\tcols{"{Number}"}{"{Number}"}{")

%state BLOCK
%state ADDQ
%state ADDA
%state MLE
%state TCOLSA
%state TCOLSB

%%

<YYINITIAL, ADDQ, ADDA,BLOCK,MLE,TCOLSA,TCOLSB>{

{MLEbegin}                                  {startEq();}

{BLOCKbegin}                                {
                                             // find block name in \begin{name}
                                             String name = yytext().substring(7, yytext().length()-2);
                                             // start new block
                                                this.startBlock(name);
                                              }

{BLOCKend}                                   {
                                             // end previous block
                                             this.endBlock();
                                              }

{TCOLSAbegin}                               {this.startTcolsA();}

("%"{Sentence}+)                         {this.currentLine += yytext();}

([\\]{AnyChar}+)			           {this.currentLine+=yytext(); }
("$"{Sentence}+"$")                         {this.currentLine+=yytext(); }
{Whitespace}                                {this.addWhiteSpace();}
{Appost}                                    {this.currentLine+=yytext();}
{RAnyCharWB}+                               {this.currentLine+=yytext();}
("\n")                                      {this.endLine();}

}

<YYINITIAL> {

        {ADDQbegin}                                 { this.startADDQ(); }
        {OpenBracket}                               {this.currentLine+=yytext();}
        {CloseBracket}                              {this.currentLine+=yytext();}      
        "\\titreExercice{"{SentenceWB}*"}"           { }

        "\\partie{"{SentenceWB}*"}"                  { }

        "\\sousPartie{"{SentenceWB}*"}"              { }
	
	<<EOF>>				             { return false; }

	/* Catch any other (unhandled) characters and flag them as identifiers. */
	
        .							{ System.err.println("not catched : " + yytext()); }

}





/* equation */
<BLOCK>{        
       {OpenBracket}                                     {   addOpenBracket();}
       {CloseBracket}                                    {   addCloseBracket(); }
        <<EOF>>					        { return false; }
        .					        { System.err.println("not catched : " + yytext()); }
}


/* question */
<ADDQ>{
       
       ({CloseBracket} ({Whitespace} | "\n")* {OpenBracket}({Whitespace} | "\n")*) { bracketCount--; endADDQ() ; bracketCount++;}
       {OpenBracket}                                     { addOpenBracket();}
       {CloseBracket}                                    { addCloseBracket();}
        <<EOF>>					        { return false; }
        .					        { System.err.println("not catched : " + yytext()); }
}

/* answer */
<ADDA>{
       {OpenBracket}                                     {   addOpenBracket();}
       {CloseBracket}({Whitespace}|"\n")*                {   endADDA();}
        <<EOF>>					        { return false;  }
        .					        { System.err.println("not catched : " + yytext());  }
}


/* equation */
<MLE>{
       {OpenBracket}                                     {   addOpenBracket();}
       {CloseBracket}({Whitespace}|"\n")*                {   endEq();}
        <<EOF>>					        {   return false;  }
        .					        {   System.err.println("not catched : " + yytext());  }
}


/* multicols */
<TCOLSA>{
       {OpenBracket}                                     {   addOpenBracket();}
       ({CloseBracket} ({Whitespace} | "\n")* {OpenBracket}({Whitespace} | "\n")*) { bracketCount--; endTcolsA() ; bracketCount++;}
        <<EOF>>					        {   return false;  }
        .					        {   System.err.println("not catched : " + yytext());  }
}

<TCOLSB>{
       {OpenBracket}                                     {   addOpenBracket();}
       {CloseBracket}({Whitespace}|"\n")*                {   endTcolsB();}
        <<EOF>>					        { return false;  }
        .					        { System.err.println("not catched : " + yytext());  }
}


