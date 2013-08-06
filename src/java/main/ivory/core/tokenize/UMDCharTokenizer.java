package ivory.core.tokenize;

import static java.lang.Character.UnicodeBlock.ALPHABETIC_PRESENTATION_FORMS;
import static java.lang.Character.UnicodeBlock.ARABIC;
import static java.lang.Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A;
import static java.lang.Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B;
import static java.lang.Character.UnicodeBlock.BASIC_LATIN;
import static java.lang.Character.UnicodeBlock.LATIN_1_SUPPLEMENT;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_A;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL;
import static java.lang.Character.UnicodeBlock.LATIN_EXTENDED_B;

import java.io.Reader;
import java.lang.Character.UnicodeBlock;

import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.AttributeSource.State;

public class UMDCharTokenizer extends CharTokenizer {

  public UMDCharTokenizer(Reader input) {
    super(input);
  }

  public UMDCharTokenizer(AttributeSource source, Reader input) {
    super(source, input);
  }

  public UMDCharTokenizer(AttributeFactory factory, Reader input) {
    super(factory, input);
  }

  @Override
  protected boolean isTokenChar(char c) {
    UnicodeBlock ub = Character.UnicodeBlock.of(c);
    if (ub == BASIC_LATIN) {
      return Character.isLetterOrDigit(c);
    } else
      return ub == ARABIC || ub == ARABIC_PRESENTATION_FORMS_A || ub == ARABIC_PRESENTATION_FORMS_B
          || ub == ALPHABETIC_PRESENTATION_FORMS || ub == LATIN_1_SUPPLEMENT
          || ub == LATIN_EXTENDED_A || ub == LATIN_EXTENDED_B || ub == LATIN_EXTENDED_ADDITIONAL;
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
