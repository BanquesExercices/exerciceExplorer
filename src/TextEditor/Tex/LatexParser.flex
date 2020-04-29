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


URLSubDelim				= ([#><\^?\!\&\(\)\*\+:\.,\/;=\[\]\\~|\"])
OpenBracket                              =([\{])
CloseBracket                              =([\}])
RAnyChar                                 = ({AnyChar} | {OpenBracket} | {CloseBracket} | {URLSubDelim}  )
RAnyCharWB                                 = ({AnyChar} |  {URLSubDelim} | (\\\{) | (\\\}) )

Sentence                                 = ({RAnyChar} | {Whitespace} |{Appost} )
SentenceWB                                 = ({RAnyCharWB} | {Whitespace} |{Appost} )


/* BLOCKbegin may catch various options enclosed in brackets */
BLOCKbegin = ("\\begin{"{AnyChar}+"}") ("["{RAnyCharWB}*"]")? ("{"{RAnyCharWB}*"}")* ("\n")+
BLOCKend = ("\\end{"{AnyChar}+"}")("\n")*
ADDQbegin = ("\\addQ{")("\n"|{Whitespace})*
MLEbegin = ("\\eq{")("\n"|{Whitespace})*
ADDQloginBegin = ("\\addQ["{AnyChar}+"]{")
TCOLSbegin = ("\\tcols{"{Number}"}{"{Number}"}{")("\n")*
ENONCEbegin = ("\\enonce{")("\n")*

%state BLOCK_Q
%state BLOCK_A
%state BRACKET_BLOCK


%%

<YYINITIAL, BRACKET_BLOCK>{

{MLEbegin}                                 {startBracketBloc(yytext());}
{TCOLSbegin}                               {startBracketBloc(yytext());}
{ENONCEbegin}                              {startBracketBloc(yytext());}
{ADDQbegin}                                {startBracketBloc(yytext());}
{ADDQloginBegin}                           {startBracketBloc(yytext());}

{BLOCKbegin}                                {this.startBlock();}
{BLOCKend}                                  {this.endBlock();}


("%"{Sentence}+)                            {this.currentLine += yytext(); }

([\\]{AnyChar}+)			           {this.currentLine+=yytext(); }
("$"{Sentence}+"$")                         {this.currentLine+=yytext(); }
{Whitespace}                                {this.addWhiteSpace();}
{Appost}                                    {this.currentLine+=yytext();}
{RAnyCharWB}+                               {this.currentLine+=yytext();}
("\n")                                      {this.endLine();}
}

<YYINITIAL> {


        {OpenBracket}                                    {this.currentLine+=yytext();}
        {CloseBracket}                                   {this.currentLine+=yytext();}      
        "\\titreExercice{"{SentenceWB}*"}"("\n")?        {this.currentLine+=yytext().trim(); this.endLine(); }
        "\\partie{"{SentenceWB}*"}"("\n")?               {this.currentLine+=yytext().trim(); this.endLine(); }
        "\\sousPartie{"{SentenceWB}*"}"("\n")?           {this.currentLine+=yytext().trim(); this.endLine(); }
	
	<<EOF>>                                         { this.endLine(); return false; }

	/* Catch any other (unhandled) characters and flag them as identifiers. */
	
        .							{ System.err.println("not catched : " + yytext()); }

}



<BLOCK_Q>{        
       {OpenBracket}                                     {   addOpenBracket();}
       {CloseBracket}                                    {   addCloseBracket();}
        <<EOF>>					        { return false; }
        .					        { System.err.println("not catched : " + yytext()); }
}



<BRACKET_BLOCK>{
       
       (\n|{Whitespace})* ({CloseBracket} ({Whitespace} | "\n")* {OpenBracket}?({Whitespace} | "\n")*) {  endBracketBlock() ; }
       {OpenBracket}                                     { addOpenBracket();}
        <<EOF>>					        { return false; }
        .					        { System.err.println("not catched : " + yytext()); }
}


