package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/** Normalizes tokens extracted with {@link StandardTokenizer}. */

public final class UMDStandardFilter extends TokenFilter {


  /** Construct filtering <i>in</i>. */
  public UMDStandardFilter(TokenStream in) {
    super(in);
    termAtt = (TermAttribute) addAttribute(TermAttribute.class);
    typeAtt = (TypeAttribute) addAttribute(TypeAttribute.class);
  }

  private static final String APOSTROPHE_TYPE = StandardTokenizerImpl.TOKEN_TYPES[StandardTokenizerImpl.APOSTROPHE];
  private static final String ACRONYM_TYPE = StandardTokenizerImpl.TOKEN_TYPES[StandardTokenizerImpl.ACRONYM];

  // this filters uses attribute type
  private TypeAttribute typeAtt;
  private TermAttribute termAtt;
  
  /** Returns the next token in the stream, or null at EOS.
   * <p>Removes <tt>'s</tt> from the end of words.
   * <p>Removes dots from acronyms.
   */
  public final boolean incrementToken() throws java.io.IOException {
    if (!input.incrementToken()) {
      return false;
    }

    char[] buffer = termAtt.termBuffer();
    final int bufferLength = termAtt.termLength();
    final String type = typeAtt.type();

    if (type == APOSTROPHE_TYPE &&      // remove 's
  bufferLength >= 2 &&
        buffer[bufferLength-2] == '\'' &&
        (buffer[bufferLength-1] == 's' || buffer[bufferLength-1] == 'S')) {
      // Strip last 2 characters off
      termAtt.setTermLength(bufferLength - 2);
    } else if (type == ACRONYM_TYPE) {      // remove dots
      int upto = 0;
      for(int i=0;i<bufferLength;i++) {
        char c = buffer[i];
        if (c != '.')
          buffer[upto++] = c;
      }
      termAtt.setTermLength(upto);
    }

    return true;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();

    if (hasAttributes()) {
      if (currentState == null) {
        computeCurrentState();
      }
      sb.append(currentState.attribute.toString());
    }
    return sb.toString();
  }
}
