/* The following code was generated by JFlex 1.4.1 on 25/09/2020 09:55 */

/*
 * 04/23/2019
 *
 * Maxence Miguel-Brebion 
 *
 * Latex Parser : improve code and format
 * 
 * 
 */
package TextEditor.Tex.Indenting;
import java.io.IOException;

/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1
 * on 25/09/2020 09:55 from the specification file
 * <tt>/Users/mbrebion/NetBeansProjects/ExerciceExplorer/src/TextEditor/Tex/Indenting/LatexIndenter.flex</tt>
 */
public class LatexIndenterImpl extends LatexIndenterBase {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int BLOCK_A = 2;
  public static final int YYINITIAL = 0;
  public static final int BLOCK_Q = 1;
  public static final int BRACKET_BLOCK = 3;

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\5\1\12\1\0\1\5\23\0\1\5\3\6\1\41\1\40"+
    "\1\6\1\4\2\6\1\26\2\6\1\1\1\3\1\6\12\2\6\6"+
    "\3\1\1\35\1\1\1\43\12\1\1\46\1\23\1\24\10\1\1\20"+
    "\1\11\1\21\1\6\1\1\1\0\1\36\1\13\1\30\1\22\1\14"+
    "\1\34\1\15\1\1\1\16\2\1\1\32\1\1\1\17\1\31\1\37"+
    "\1\25\1\42\1\33\1\27\1\45\2\1\1\44\2\1\1\7\1\6"+
    "\1\10\1\6\61\0\1\1\17\0\1\1\10\0\1\1\26\0\1\1"+
    "\1\0\1\1\4\0\5\1\2\0\2\1\4\0\1\1\1\0\1\1"+
    "\2\0\1\1\1\0\2\1\uff03\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\4\0\2\1\1\2\1\1\1\3\2\1\1\4\1\5"+
    "\1\2\1\6\1\1\1\3\1\1\2\0\7\1\1\7"+
    "\2\0\1\6\1\1\5\0\11\1\6\0\3\1\1\0"+
    "\1\10\7\1\6\0\1\1\1\0\12\1\10\0\1\1"+
    "\2\0\10\1\7\0\1\11\2\1\1\0\2\1\4\0"+
    "\1\10\1\1\4\0\1\1\3\0\1\12\2\0\1\13"+
    "\1\0\1\1\2\0\1\12\4\0\1\1\3\0\1\1"+
    "\4\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[150];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\47\0\116\0\165\0\116\0\234\0\303\0\352"+
    "\0\303\0\u0111\0\u0138\0\116\0\116\0\u015f\0\u0186\0\u01ad"+
    "\0\u015f\0\u01d4\0\303\0\u01fb\0\u0222\0\u0249\0\u0270\0\u0297"+
    "\0\u02be\0\u02e5\0\u030c\0\u0111\0\u0333\0\u015f\0\u035a\0\u0381"+
    "\0\u03a8\0\u03cf\0\u03f6\0\u041d\0\u0444\0\u046b\0\u0492\0\u04b9"+
    "\0\u04e0\0\u0507\0\u052e\0\u0555\0\u057c\0\u05a3\0\u05ca\0\u05f1"+
    "\0\u0618\0\u063f\0\u0666\0\u068d\0\u06b4\0\u06db\0\u0702\0\u0729"+
    "\0\u0750\0\u0777\0\u079e\0\u07c5\0\u07ec\0\u0813\0\u083a\0\u0861"+
    "\0\u0888\0\u08af\0\u08d6\0\u08fd\0\u0924\0\u094b\0\u0972\0\u0999"+
    "\0\u09c0\0\u09e7\0\u0a0e\0\u0a35\0\u0a5c\0\u0a83\0\u0aaa\0\u0ad1"+
    "\0\u0af8\0\u0b1f\0\u0b46\0\u0b6d\0\u0b94\0\u0bbb\0\u0be2\0\u0c09"+
    "\0\u0c30\0\u0c57\0\u0c7e\0\u0ca5\0\u0ccc\0\u0cf3\0\u0d1a\0\u0d41"+
    "\0\u0d68\0\u0d8f\0\u0db6\0\u0ddd\0\u0e04\0\u0e2b\0\u0e52\0\u0e79"+
    "\0\u0ea0\0\u0ec7\0\u0eee\0\u0f15\0\u0f3c\0\u0f63\0\u0f8a\0\u0fb1"+
    "\0\u0fd8\0\u0fff\0\u1026\0\u104d\0\u1074\0\u109b\0\u10c2\0\u10e9"+
    "\0\u1110\0\u1137\0\u115e\0\u1185\0\u11ac\0\u11d3\0\u11fa\0\u1221"+
    "\0\u1248\0\u126f\0\u1296\0\u12bd\0\u12e4\0\u130b\0\u1332\0\u1359"+
    "\0\u1380\0\u13a7\0\u13ce\0\u13f5\0\u141c\0\u1443\0\u146a\0\u1491"+
    "\0\u14b8\0\u14df\0\u1506\0\u152d\0\u1554\0\u157b";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[150];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\5\3\6\1\5\1\7\1\6\2\5\1\10\1\11"+
    "\25\6\1\12\1\13\5\6\7\5\1\14\1\15\1\5"+
    "\1\0\34\5\47\0\1\5\3\6\1\5\1\16\1\6"+
    "\1\14\1\17\1\20\1\21\25\6\1\12\1\13\5\6"+
    "\1\0\3\6\2\0\1\6\2\0\1\22\1\0\25\6"+
    "\2\0\5\6\5\0\1\23\3\0\1\24\1\23\35\0"+
    "\3\6\2\0\3\6\1\22\1\0\1\25\1\26\6\6"+
    "\1\27\3\6\1\30\3\6\1\31\1\32\2\6\1\33"+
    "\2\0\5\6\1\0\11\34\1\0\25\34\2\0\5\34"+
    "\1\0\11\35\1\0\25\35\2\0\5\35\5\0\1\36"+
    "\2\0\1\17\1\24\1\36\41\0\1\17\1\0\1\37"+
    "\2\0\1\17\35\0\3\6\2\0\3\6\1\22\1\0"+
    "\1\25\1\26\6\6\1\27\3\6\1\40\4\6\1\32"+
    "\3\6\2\0\5\6\1\0\3\6\2\0\3\6\1\22"+
    "\1\0\25\6\2\0\5\6\13\0\1\41\1\42\6\0"+
    "\1\43\3\0\1\44\4\0\1\45\13\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\1\6\1\46\23\6\2\0"+
    "\5\6\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\4\6\1\47\5\6\1\50\12\6\2\0\5\6\1\0"+
    "\3\6\2\0\1\6\2\0\1\22\1\0\11\6\1\51"+
    "\13\6\2\0\5\6\1\0\3\6\2\0\1\6\2\0"+
    "\1\22\1\0\3\6\1\52\11\6\1\53\7\6\2\0"+
    "\5\6\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\16\6\1\54\6\6\2\0\5\6\1\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\3\6\1\55\21\6\2\0"+
    "\5\6\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\23\6\1\56\1\6\2\0\5\6\1\0\11\35\1\0"+
    "\25\35\1\0\1\5\5\35\5\0\1\37\4\0\1\37"+
    "\35\0\3\6\2\0\1\6\2\0\1\22\1\0\15\6"+
    "\1\53\7\6\2\0\5\6\14\0\1\57\51\0\1\60"+
    "\5\0\1\61\45\0\1\62\52\0\1\63\34\0\1\64"+
    "\31\0\3\6\2\0\1\6\2\0\1\22\1\0\2\6"+
    "\1\65\22\6\2\0\5\6\1\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\7\6\1\66\6\6\1\67\6\6"+
    "\2\0\5\6\1\0\3\6\1\0\1\70\1\6\1\71"+
    "\1\0\1\22\1\70\5\6\1\72\17\6\2\0\5\6"+
    "\1\0\3\6\2\0\1\6\1\71\1\0\1\22\1\0"+
    "\5\6\1\73\17\6\2\0\5\6\1\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\14\6\1\74\10\6\2\0"+
    "\5\6\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\16\6\1\75\6\6\2\0\5\6\1\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\25\6\2\0\3\6\1\76"+
    "\1\6\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\2\6\1\77\22\6\2\0\5\6\1\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\25\6\2\0\1\100\4\6"+
    "\15\0\1\101\62\0\1\102\22\0\1\70\1\0\1\71"+
    "\2\0\1\70\5\0\1\103\35\0\1\71\10\0\1\104"+
    "\57\0\1\105\32\0\1\106\32\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\3\6\1\107\21\6\2\0\5\6"+
    "\1\0\3\6\2\0\1\6\1\110\1\0\1\22\1\0"+
    "\25\6\2\0\5\6\1\0\3\6\2\0\1\6\2\0"+
    "\1\22\1\0\4\6\1\111\20\6\2\0\5\6\5\0"+
    "\1\70\1\0\1\71\2\0\1\70\41\0\1\71\4\0"+
    "\1\71\35\0\1\112\1\113\1\6\2\0\1\6\2\0"+
    "\1\22\1\0\5\112\2\6\4\112\1\6\11\112\2\0"+
    "\5\112\1\0\1\114\1\115\1\6\2\0\1\6\2\0"+
    "\1\22\1\0\5\114\2\6\4\114\1\6\11\114\2\0"+
    "\5\114\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\25\6\2\0\1\116\4\6\1\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\17\6\1\117\5\6\2\0\5\6"+
    "\1\0\3\6\2\0\1\6\2\0\1\22\1\0\20\6"+
    "\1\120\4\6\2\0\5\6\1\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\22\6\1\121\2\6\2\0\5\6"+
    "\1\0\3\6\2\0\1\6\2\0\1\22\1\0\14\6"+
    "\1\122\10\6\2\0\5\6\16\0\1\123\47\0\1\124"+
    "\30\0\1\125\1\126\10\0\5\125\2\0\4\125\1\0"+
    "\11\125\2\0\5\125\1\0\1\127\1\130\10\0\5\127"+
    "\2\0\4\127\1\0\11\127\2\0\5\127\32\0\1\131"+
    "\51\0\1\132\12\0\3\6\2\0\1\6\2\0\1\22"+
    "\1\0\4\6\1\133\20\6\2\0\5\6\1\0\1\134"+
    "\1\135\10\0\5\134\2\0\4\134\1\0\11\134\2\0"+
    "\5\134\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\15\6\1\136\7\6\2\0\5\6\1\0\1\112\1\113"+
    "\1\6\2\0\1\6\2\0\1\22\1\0\5\112\1\6"+
    "\1\137\4\112\1\140\11\112\2\0\5\112\1\0\1\112"+
    "\1\113\1\112\2\0\1\6\2\0\1\22\1\0\5\112"+
    "\1\6\1\137\4\112\1\140\11\112\2\0\5\112\1\0"+
    "\1\114\1\115\1\6\2\0\1\6\2\0\1\22\1\0"+
    "\5\114\1\6\1\141\4\114\1\6\11\114\2\0\5\114"+
    "\1\0\1\114\1\115\1\114\2\0\1\6\2\0\1\22"+
    "\1\0\5\114\1\6\1\141\4\114\1\6\11\114\2\0"+
    "\5\114\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\1\6\1\142\23\6\2\0\5\6\1\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\20\6\1\143\4\6\2\0"+
    "\5\6\1\0\3\6\2\0\1\6\2\0\1\22\1\0"+
    "\25\6\2\0\4\6\1\33\1\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\23\6\1\144\1\6\2\0\5\6"+
    "\1\0\3\6\2\0\1\6\2\0\1\22\1\0\3\6"+
    "\1\145\21\6\2\0\5\6\17\0\1\146\57\0\1\147"+
    "\17\0\1\125\1\126\10\0\5\125\1\0\1\70\4\125"+
    "\1\150\11\125\2\0\5\125\1\0\1\125\1\126\1\125"+
    "\7\0\5\125\1\0\1\70\4\125\1\150\11\125\2\0"+
    "\5\125\1\0\1\127\1\130\10\0\5\127\1\0\1\151"+
    "\4\127\1\0\11\127\2\0\5\127\1\0\1\127\1\130"+
    "\1\127\7\0\5\127\1\0\1\151\4\127\1\0\11\127"+
    "\2\0\5\127\33\0\1\152\51\0\1\153\11\0\3\6"+
    "\2\0\1\6\1\154\1\0\1\22\1\0\25\6\2\0"+
    "\5\6\1\0\1\134\1\135\5\0\1\155\2\0\5\134"+
    "\2\0\4\134\1\0\11\134\2\0\5\134\1\0\1\134"+
    "\1\135\1\134\4\0\1\155\2\0\5\134\2\0\4\134"+
    "\1\0\11\134\2\0\5\134\1\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\1\6\1\156\23\6\2\0\5\6"+
    "\1\0\3\6\1\0\1\70\1\6\1\71\1\0\1\22"+
    "\1\70\25\6\2\0\5\6\1\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\6\6\1\137\16\6\2\0\5\6"+
    "\1\0\3\6\2\0\1\6\1\71\1\0\1\22\1\0"+
    "\25\6\2\0\5\6\1\0\3\6\2\0\1\6\2\0"+
    "\1\22\1\0\25\6\2\0\1\6\1\157\3\6\1\0"+
    "\3\6\2\0\1\6\1\160\1\0\1\22\1\0\25\6"+
    "\2\0\5\6\1\0\3\6\2\0\1\6\2\0\1\22"+
    "\1\0\24\6\1\161\2\0\5\6\1\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\1\6\1\162\23\6\2\0"+
    "\5\6\7\0\1\154\53\0\1\163\53\0\1\70\34\0"+
    "\1\71\46\0\1\160\76\0\1\164\10\0\1\165\1\166"+
    "\10\0\5\165\2\0\4\165\1\0\11\165\2\0\5\165"+
    "\12\0\1\155\35\0\3\6\2\0\1\6\1\167\1\0"+
    "\1\22\1\0\25\6\2\0\5\6\1\0\3\6\2\0"+
    "\1\6\2\0\1\22\1\0\25\6\2\0\2\6\1\170"+
    "\2\6\2\0\1\171\45\0\3\6\2\0\1\6\1\172"+
    "\1\0\1\22\1\0\25\6\2\0\5\6\1\0\3\6"+
    "\2\0\1\6\1\173\1\0\1\22\1\0\25\6\2\0"+
    "\5\6\7\0\1\167\46\0\1\172\40\0\1\165\1\166"+
    "\5\0\1\174\2\0\5\165\2\0\4\165\1\0\11\165"+
    "\2\0\5\165\1\0\1\165\1\166\1\165\4\0\1\174"+
    "\2\0\5\165\2\0\4\165\1\0\11\165\2\0\5\165"+
    "\12\0\1\167\35\0\3\6\2\0\1\6\2\0\1\22"+
    "\1\0\1\6\1\175\23\6\2\0\5\6\2\0\1\171"+
    "\1\176\4\0\1\177\37\0\3\200\7\0\5\200\2\0"+
    "\4\200\1\0\11\200\2\0\5\200\1\0\6\173\1\0"+
    "\1\201\1\202\1\0\25\173\2\0\5\173\7\0\1\203"+
    "\2\0\1\204\5\0\1\205\27\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\25\6\2\0\1\206\4\6\2\0"+
    "\1\176\5\0\1\177\45\0\1\207\40\0\3\200\4\0"+
    "\1\210\2\0\5\200\2\0\4\200\1\0\11\200\2\0"+
    "\5\200\12\0\1\201\35\0\7\173\1\211\1\202\1\0"+
    "\25\173\2\0\5\173\1\0\3\203\2\0\1\203\1\0"+
    "\1\212\1\213\1\0\25\203\2\0\5\203\12\0\1\204"+
    "\35\0\3\205\2\0\1\205\2\0\1\214\1\0\6\205"+
    "\1\215\16\205\2\0\5\205\1\0\3\6\2\0\1\6"+
    "\2\0\1\22\1\0\15\6\1\216\7\6\2\0\5\6"+
    "\2\0\1\217\53\0\1\220\40\0\6\173\1\0\1\201"+
    "\1\202\1\201\25\173\2\0\5\173\7\0\1\203\2\0"+
    "\1\204\35\0\3\203\2\0\2\203\1\221\1\213\1\0"+
    "\25\203\2\0\5\203\1\0\3\205\2\0\3\205\1\214"+
    "\1\0\6\205\1\215\16\205\2\0\5\205\1\0\3\205"+
    "\2\0\1\205\1\203\1\0\1\214\1\204\6\205\1\215"+
    "\16\205\2\0\5\205\1\0\3\6\2\0\1\6\2\0"+
    "\1\22\1\0\3\6\1\222\21\6\2\0\5\6\2\0"+
    "\1\217\1\223\4\0\1\163\37\0\3\224\2\0\1\224"+
    "\2\0\1\225\1\0\25\224\2\0\5\224\1\0\3\203"+
    "\2\0\2\203\1\212\1\213\1\204\25\203\2\0\5\203"+
    "\1\0\3\6\2\0\1\6\2\0\1\22\1\0\15\6"+
    "\1\145\7\6\2\0\5\6\2\0\1\223\5\0\1\163"+
    "\37\0\3\224\2\0\1\224\1\0\1\163\1\225\1\0"+
    "\25\224\2\0\5\224\1\0\3\224\2\0\2\224\1\226"+
    "\1\225\1\0\25\224\2\0\5\224\1\0\3\224\2\0"+
    "\1\224\1\167\1\163\1\225\1\0\25\224\2\0\5\224";

  private static int [] zzUnpackTrans() {
    int [] result = new int[5538];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\10\1\0\1\11\6\1\2\11\5\1\2\0"+
    "\10\1\2\0\2\1\5\0\11\1\6\0\3\1\1\0"+
    "\10\1\6\0\1\1\1\0\12\1\10\0\1\1\2\0"+
    "\10\1\7\0\3\1\1\0\2\1\4\0\2\1\4\0"+
    "\1\1\3\0\1\1\2\0\1\1\1\0\1\1\2\0"+
    "\1\1\4\0\1\1\3\0\1\1\4\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[150];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /* user code: */

	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public LatexIndenterImpl() {
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
            



  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public LatexIndenterImpl(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public LatexIndenterImpl(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 170) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzPushbackPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead < 0) {
      return true;
    }
    else {
      zzEndRead+= numRead;
      return false;
    }
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public boolean yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = zzLexicalState;


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 8: 
          { startBracketBloc(yytext());
          }
        case 12: break;
        case 11: 
          { this.startBlock();
          }
        case 13: break;
        case 3: 
          { this.endLine();
          }
        case 14: break;
        case 5: 
          { addCloseBracket();
          }
        case 15: break;
        case 9: 
          { this.endBlock();
          }
        case 16: break;
        case 2: 
          { this.addWhiteSpace();
          }
        case 17: break;
        case 10: 
          { this.currentLine+=yytext().trim(); this.endLine(); this.endLine();
          }
        case 18: break;
        case 7: 
          { this.currentLine += yytext();
          }
        case 19: break;
        case 4: 
          { addOpenBracket();
          }
        case 20: break;
        case 1: 
          { this.currentLine+=yytext();
          }
        case 21: break;
        case 6: 
          { endBracketBlock() ;
          }
        case 22: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            switch (zzLexicalState) {
            case YYINITIAL: {
              this.endLine(); return false;
            }
            case 151: break;
            case BLOCK_Q: {
              return false;
            }
            case 152: break;
            case BRACKET_BLOCK: {
              return false;
            }
            case 153: break;
            default:
            }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
