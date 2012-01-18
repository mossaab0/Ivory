/*
 * Ivory: A Hadoop toolkit for web-scale information retrieval
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package ivory.core.tokenize;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.galagosearch.core.parse.Document;
import org.galagosearch.core.parse.TagTokenizer;
import org.tartarus.snowball.ext.englishStemmer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GalagoTokenizer extends Tokenizer {
  private static final String[] TERRIER_STOP_WORDS = { "a", "abaft", "abafter", "abaftest",
      "about", "abouter", "aboutest", "above", "abover", "abovest", "accordingly", "aer", "aest",
      "afore", "after", "afterer", "afterest", "afterward", "afterwards", "again", "against",
      "aid", "ain", "albeit", "all", "aller", "allest", "alls", "allyou", "almost", "along",
      "alongside", "already", "also", "although", "always", "amid", "amidst", "among", "amongst",
      "an", "and", "andor", "anear", "anent", "another", "any", "anybody", "anyhow", "anyone",
      "anything", "anywhere", "apart", "aparter", "apartest", "appear", "appeared", "appearing",
      "appears", "appropriate", "appropriated", "appropriater", "appropriates", "appropriatest",
      "appropriating", "are", "ares", "around", "as", "ases", "aside", "asides", "aslant",
      "astraddle", "astraddler", "astraddlest", "astride", "astrider", "astridest", "at",
      "athwart", "atop", "atween", "aught", "aughts", "available", "availabler", "availablest",
      "awfully", "b", "be", "became", "because", "become", "becomes", "becoming", "becominger",
      "becomingest", "becomings", "been", "before", "beforehand", "beforehander", "beforehandest",
      "behind", "behinds", "below", "beneath", "beside", "besides", "better", "bettered",
      "bettering", "betters", "between", "betwixt", "beyond", "bist", "both", "but", "buts", "by",
      "by-and-by", "byandby", "c", "cannot", "canst", "cant", "canted", "cantest", "canting",
      "cants", "cer", "certain", "certainer", "certainest", "cest", "chez", "circa", "co",
      "come-on", "come-ons", "comeon", "comeons", "concerning", "concerninger", "concerningest",
      "consequently", "considering", "could", "couldst", "cum", "d", "dday", "ddays", "describe",
      "described", "describes", "describing", "despite", "despited", "despites", "despiting",
      "did", "different", "differenter", "differentest", "do", "doe", "does", "doing", "doings",
      "done", "doner", "dones", "donest", "dos", "dost", "doth", "downs", "downward", "downwarder",
      "downwardest", "downwards", "during", "e", "each", "eg", "eight", "either", "else",
      "elsewhere", "enough", "ere", "et", "etc", "even", "evened", "evenest", "evens", "evenser",
      "evensest", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex",
      "except", "excepted", "excepting", "excepts", "exes", "f", "fact", "facts", "failing",
      "failings", "few", "fewer", "fewest", "figupon", "figuponed", "figuponing", "figupons",
      "five", "followthrough", "for", "forby", "forbye", "fore", "forer", "fores", "forever",
      "former", "formerer", "formerest", "formerly", "formers", "fornenst", "forwhy", "four",
      "fourscore", "frae", "from", "fs", "further", "furthered", "furtherer", "furtherest",
      "furthering", "furthermore", "furthers", "g", "get", "gets", "getting", "go", "gone", "good",
      "got", "gotta", "gotten", "h", "had", "hadst", "hae", "hardly", "has", "hast", "hath",
      "have", "haves", "having", "he", "hence", "her", "hereafter", "hereafters", "hereby",
      "herein", "hereupon", "hers", "herself", "him", "himself", "his", "hither", "hitherer",
      "hitherest", "hoo", "hoos", "how", "how-do-you-do", "howbeit", "howdoyoudo", "however",
      "huh", "humph", "i", "idem", "idemer", "idemest", "ie", "if", "ifs", "immediate",
      "immediately", "immediater", "immediatest", "in", "inasmuch", "inc", "indeed", "indicate",
      "indicated", "indicates", "indicating", "info", "information", "insofar", "instead", "into",
      "inward", "inwarder", "inwardest", "inwards", "is", "it", "its", "itself", "j", "k", "l",
      "latter", "latterer", "latterest", "latterly", "latters", "layabout", "layabouts", "less",
      "lest", "lot", "lots", "lotted", "lotting", "m", "main", "make", "many", "mauger", "maugre",
      "mayest", "me", "meanwhile", "meanwhiles", "midst", "midsts", "might", "mights", "more",
      "moreover", "most", "mostly", "much", "mucher", "muchest", "must", "musth", "musths",
      "musts", "my", "myself", "n", "natheless", "nathless", "neath", "neaths", "necessarier",
      "necessariest", "necessary", "neither", "nethe", "nethermost", "never", "nevertheless",
      "nigh", "nigher", "nighest", "nine", "no", "no-one", "nobodies", "nobody", "noes", "none",
      "noone", "nor", "nos", "not", "nothing", "nothings", "notwithstanding", "nowhere",
      "nowheres", "o", "of", "off", "offest", "offs", "often", "oftener", "oftenest", "oh", "on",
      "one", "oneself", "onest", "ons", "onto", "or", "orer", "orest", "other", "others",
      "otherwise", "otherwiser", "otherwisest", "ought", "oughts", "our", "ours", "ourself",
      "ourselves", "out", "outed", "outest", "outs", "outside", "outwith", "over", "overall",
      "overaller", "overallest", "overalls", "overs", "own", "owned", "owning", "owns", "owt", "p",
      "particular", "particularer", "particularest", "particularly", "particulars", "per",
      "perhaps", "plaintiff", "please", "pleased", "pleases", "plenties", "plenty", "pro",
      "probably", "provide", "provided", "provides", "providing", "q", "qua", "que", "quite", "r",
      "rath", "rathe", "rather", "rathest", "re", "really", "regarding", "relate", "related",
      "relatively", "res", "respecting", "respectively", "s", "said", "saider", "saidest", "same",
      "samer", "sames", "samest", "sans", "sanserif", "sanserifs", "sanses", "saved", "sayid",
      "sayyid", "seem", "seemed", "seeminger", "seemingest", "seemings", "seems", "send", "sent",
      "senza", "serious", "seriouser", "seriousest", "seven", "several", "severaler", "severalest",
      "shall", "shalled", "shalling", "shalls", "she", "should", "shoulded", "shoulding",
      "shoulds", "since", "sine", "sines", "sith", "six", "so", "sobeit", "soer", "soest", "some",
      "somebody", "somehow", "someone", "something", "sometime", "sometimer", "sometimes",
      "sometimest", "somewhat", "somewhere", "stop", "stopped", "such", "summat", "sup", "supped",
      "supping", "sups", "syn", "syne", "t", "ten", "than", "that", "the", "thee", "their",
      "theirs", "them", "themselves", "then", "thence", "thener", "thenest", "there", "thereafter",
      "thereby", "therefore", "therein", "therer", "therest", "thereupon", "these", "they",
      "thine", "thing", "things", "this", "thises", "thorough", "thorougher", "thoroughest",
      "thoroughly", "those", "thou", "though", "thous", "thouses", "three", "thro", "through",
      "througher", "throughest", "throughout", "thru", "thruer", "thruest", "thus", "thy",
      "thyself", "till", "tilled", "tilling", "tills", "to", "together", "too", "toward",
      "towarder", "towardest", "towards", "two", "u", "umpteen", "under", "underneath", "unless",
      "unlike", "unliker", "unlikest", "until", "unto", "up", "upon", "uponed", "uponing", "upons",
      "upped", "upping", "ups", "us", "use", "used", "usedest", "username", "usually", "v",
      "various", "variouser", "variousest", "verier", "veriest", "versus", "very", "via",
      "vis-a-vis", "vis-a-viser", "vis-a-visest", "viz", "vs", "w", "was", "wast", "we", "were",
      "wert", "what", "whatever", "whateverer", "whateverest", "whatsoever", "whatsoeverer",
      "whatsoeverest", "wheen", "when", "whenas", "whence", "whencesoever", "whenever",
      "whensoever", "where", "whereafter", "whereas", "whereby", "wherefrom", "wherein",
      "whereinto", "whereof", "whereon", "wheresoever", "whereto", "whereupon", "wherever",
      "wherewith", "wherewithal", "whether", "which", "whichever", "whichsoever", "while",
      "whiles", "whilst", "whither", "whithersoever", "whoever", "whomever", "whose", "whoso",
      "whosoever", "why", "with", "withal", "within", "without", "would", "woulded", "woulding",
      "woulds", "x", "y", "ye", "yet", "yon", "yond", "yonder", "you", "your", "yours", "yourself",
      "yourselves", "z", "zillion", };

  private final englishStemmer stemmer = new englishStemmer();
  private final Map<String, String> cache = Maps.newHashMap();
  private final Set<String> stopwords = Sets.newHashSet(TERRIER_STOP_WORDS);

  public boolean isStopWord(String word) {
    return stopwords.contains(word);
  }

  public String[] processContent(String text) {
    TagTokenizer tokenizer = new TagTokenizer();
    Document doc = null;

    try {
      doc = tokenizer.tokenize(text);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    List<String> toks = doc.terms;
    List<String> words = Lists.newArrayList();

    for (String tok : toks) {
      if (!stopwords.contains(tok)) {
        words.add(tok);
      }
    }

    for (int i = 0; i < words.size(); i++) {
      String word = words.get(i);

      if (word != null) {
        if (cache.containsKey(word)) {
          words.set(i, cache.get(word));
        } else {
          stemmer.setCurrent(word);
          if (stemmer.stem()) {
            String stem = stemmer.getCurrent();
            words.set(i, stem);
            cache.put(word, stem);
          } else {
            cache.put(word, word);
          }
        }

        if (cache.size() > 50000) {
          cache.clear();
        }
      }
    }

    String[] arr = new String[words.size()];
    return (String[]) words.toArray(arr);
  }

  @Override
  public void configure(Configuration conf) {
  }

  public static void main(String[] args) {
    String text = " this is a the <test> for the teokenizer 101 546 345-543543545436-4656765865865 rgger <xml> ergtre 456435klj345lj34590";

    Tokenizer tokenizer;
    String[] tokens;

    System.out.println("tokenization according to Galago: ");
    tokenizer = new GalagoTokenizer();
    tokens = tokenizer.processContent(text);
    for (String t : tokens) {
      System.out.println(t);
    }
  }

  @Override
  public void configure(Configuration mJobConf, FileSystem fs) {
  }

}
