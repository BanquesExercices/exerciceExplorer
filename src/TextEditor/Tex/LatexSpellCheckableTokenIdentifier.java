/*
 * 03/19/2014
 *
 * DefaultSpellCheckableTokenIdentifier.java - Identifies comment tokens to
 * be spell checked.
 * 
 * This library is distributed under the LGPL.  See the included
 * SpellChecker.License.txt file for details.
 */
package TextEditor.Tex;

import TextEditor.Tex.Coloring.LatexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.spell.SpellCheckableTokenIdentifier;


/**
 * all plain text (except latex commands) is spell-checked
 *
 * @author Maxence Miguel-Brebion
 * @version 1.0
 */
public class LatexSpellCheckableTokenIdentifier
		implements SpellCheckableTokenIdentifier {


	/**
	 * The default implementation of this method does nothing; this token
	 * identifier does not have state.
	 */
	@Override
	public void begin() {
	}


	/**
	 * The default implementation of this method does nothing; this token
	 * identifier does not have state.
	 */
	@Override
	public void end() {
	}


	/**
	 *
	 */
	@Override
	public boolean isSpellCheckable(Token t) {
		return (t.isIdentifier() || t.isComment() || t.getType()==LatexTokenMaker.TOKEN_TITLE || t.getType()==LatexTokenMaker.TOKEN_SUB_PART || t.getType()==LatexTokenMaker.TOKEN_PART)  && t.getLexeme().toLowerCase().equals(t.getLexeme()) ;
	}


}
