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

import java.lang.Character.UnicodeBlock;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Normalizer for Arabic.
 * <p>
 * Normalization is done in-place for efficiency, operating on a termbuffer.
 * <p>
 * Normalization is defined as:
 * <ul>
 * <li>Normalization of hamza with alef seat to a bare alef.
 * <li>Normalization of teh marbuta to heh
 * <li>Normalization of dotless yeh (alef maksura) to yeh.
 * <li>Removal of Arabic diacritics (the harakat)
 * <li>Removal of tatweel (stretching character).
 * </ul>
 * 
 */
public class CalimaNormalizer {

  public static final char ALEF = '\u0627';
  public static final char ALEF_MADDA = '\u0622';
  public static final char ALEF_HAMZA_ABOVE = '\u0623';
  public static final char ALEF_HAMZA_BELOW = '\u0625';
  public static final char YEH = '\u064A';
  public static final char DOTLESS_YEH = '\u0649';
  public static final char TEH_MARBUTA = '\u0629';
  public static final char HEH = '\u0647';
  public static final char TATWEEL = '\u0640';
  public static final char FATHATAN = '\u064B';
  public static final char DAMMATAN = '\u064C';
  public static final char KASRATAN = '\u064D';
  public static final char FATHA = '\u064E';
  public static final char DAMMA = '\u064F';
  public static final char KASRA = '\u0650';
  public static final char SHADDA = '\u0651';
  public static final char SUKUN = '\u0652';
  public static final char ZERO = '\u0660';
  public static final char ONE = '\u0661';
  public static final char TWO = '\u0662';
  public static final char THREE = '\u0663';
  public static final char FOUR = '\u0664';
  public static final char FIVE = '\u0665';
  public static final char SIX = '\u0666';
  public static final char SEVEN = '\u0667';
  public static final char EIGHT = '\u0668';
  public static final char NINE = '\u0669';
  public static final char SPACE = ' ';

  /**
   * Normalize an input buffer of Arabic text
   * 
   * @param s input buffer
   * @param len length of input buffer
   * @return length of input buffer after normalization
   */
  public int normalize(char s[], int len) {

    for (int i = 0; i < len; i++) {
      switch (s[i]) {
      case ALEF_MADDA:
      case ALEF_HAMZA_ABOVE:
      case ALEF_HAMZA_BELOW:
        s[i] = ALEF;
        break;
      case DOTLESS_YEH:
      case 'ی':
        s[i] = YEH;
        break;
      case TEH_MARBUTA:
      case 'ھ':
        s[i] = HEH;
        break;
      case ZERO:
        s[i] = '0';
        break;
      case ONE:
        s[i] = '1';
        break;
      case TWO:
        s[i] = '2';
        break;
      case THREE:
        s[i] = '3';
        break;
      case FOUR:
        s[i] = '4';
        break;
      case FIVE:
        s[i] = '5';
        break;
      case SIX:
        s[i] = '6';
        break;
      case SEVEN:
        s[i] = '7';
        break;
      case EIGHT:
        s[i] = '8';
        break;
      case NINE:
        s[i] = '9';
        break;
      case TATWEEL:
      case KASRATAN:
      case DAMMATAN:
      case FATHATAN:
      case FATHA:
      case DAMMA:
      case KASRA:
      case SHADDA:
      case SUKUN:
        len = delete(s, i, len);
        i--;
        break;
      default:
        break;
      }
    }

    return len;
  }

  public static String normalize(String old) {
    StringBuilder sb = new StringBuilder();

    for (char c : old.toCharArray()) {
      switch (c) {
      case ALEF_MADDA:
      case ALEF_HAMZA_ABOVE:
      case ALEF_HAMZA_BELOW:
        sb.append(ALEF);
        break;
      case DOTLESS_YEH:
        sb.append(YEH);
        break;
      case TEH_MARBUTA:
        sb.append(HEH);
        break;
      case ZERO:
        sb.append('0');
        break;
      case ONE:
        sb.append('1');
        break;
      case TWO:
        sb.append('2');
        break;
      case THREE:
        sb.append('3');
        break;
      case FOUR:
        sb.append('4');
        break;
      case FIVE:
        sb.append('5');
        break;
      case SIX:
        sb.append('6');
        break;
      case SEVEN:
        sb.append('7');
        break;
      case EIGHT:
        sb.append('8');
        break;
      case NINE:
        sb.append('9');
        break;
      case TATWEEL:
      case KASRATAN:
      case DAMMATAN:
      case FATHATAN:
      case FATHA:
      case DAMMA:
      case KASRA:
      case SHADDA:
      case SUKUN:
        break;
      default:
        if (isTokenChar(c))
          sb.append(c);
        else
          sb.append(SPACE);
        break;
      }
    }

    return sb.toString();
  }

  public static boolean isTokenChar(char c) {
    UnicodeBlock ub = Character.UnicodeBlock.of(c);
    if (ub == BASIC_LATIN) {
      return Character.isLetterOrDigit(c);
    } else
      return ub == ARABIC || ub == ARABIC_PRESENTATION_FORMS_A || ub == ARABIC_PRESENTATION_FORMS_B
          || ub == ALPHABETIC_PRESENTATION_FORMS || ub == LATIN_1_SUPPLEMENT
          || ub == LATIN_EXTENDED_A || ub == LATIN_EXTENDED_B || ub == LATIN_EXTENDED_ADDITIONAL;
  }

  public static int delete(char s[], int pos, int len) {
    if (pos < len)
      System.arraycopy(s, pos + 1, s, pos, len - pos - 1);

    return len - 1;
  }
}
