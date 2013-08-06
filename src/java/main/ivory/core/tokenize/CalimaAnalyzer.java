package ivory.core.tokenize;

import static java.lang.Character.UnicodeBlock.ARABIC;
import static org.apache.solr.analysis.WordDelimiterFilter.GENERATE_NUMBER_PARTS;
import static org.apache.solr.analysis.WordDelimiterFilter.GENERATE_WORD_PARTS;
import static org.apache.solr.analysis.WordDelimiterFilter.SPLIT_ON_NUMERICS;
import ivory.core.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.UMDStandardFilter;
import org.apache.solr.analysis.WordDelimiterFilter;

public class CalimaAnalyzer extends ivory.core.tokenize.Tokenizer {
  private static final Logger LOG = Logger.getLogger(CalimaAnalyzer.class);
  private static Map<String, String> knownStems;

  static {
    LOG.setLevel(Level.WARN);
  }

  @Override
  public void configure(Configuration conf) {
    configure(conf, null);
  }

  @Override
  public void configure(Configuration conf, FileSystem fs) {
    // read stopwords from file (stopwords will be empty set if file does not exist or is empty)
    String stopwordsFile = conf.get(Constants.StopwordList);
    stopwords = readInput(fs, stopwordsFile);
    String stemmedStopwordsFile = conf.get(Constants.StemmedStopwordList);
    stemmedStopwords = readInput(fs, stemmedStopwordsFile);
    isStopwordRemoval = !stopwords.isEmpty();

    if (knownStems == null) {
      LOG.info("Loading Known Stems ...");
      knownStems = new HashMap<String, String>();
      try {
        FSDataInputStream fis = fs.open(new Path(conf.get(Constants.TokenizerData)));
        InputStreamReader isr = new InputStreamReader(fis, "UTF8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
          line = line.trim();
          if (!line.isEmpty()) {
            String key_value[] = line.split(":");
            if (!key_value[0].equals(key_value[1]))
              knownStems.put(key_value[0], key_value[1]);
          }
        }
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      LOG.info("Loaded " + knownStems.size() + " Known Stems ...");
    }

    LOG.warn("Stopword removal is " + isStopwordRemoval + "; number of stopwords: "
        + stopwords.size() + "; stemmed: " + stemmedStopwords.size());
  }

  @Override
  public String[] processContent(String text) {
    text = Normalizer.normalize(CalimaNormalizer.normalize(preNormalize(text).toLowerCase()),
        Form.NFKC).trim();
    StringBuilder sb = new StringBuilder();

    for (String token : text.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")) {
      sb.append(token).append(" ");
    }

    String tokenized = sb.toString().trim();

    sb = new StringBuilder();

    for (String token : tokenized.split(" +")) {
      sb.append(stem(token)).append(" ");
    }
    return sb.toString().trim().split(" ");
  }

  public String[] processContent2(String text) {
    text = preNormalize(text);

    final Tokenizer source = new UMDCharTokenizer(new StringReader(text));
    TokenStream tokenStream = new UMDStandardFilter(source);
    int flags = SPLIT_ON_NUMERICS | GENERATE_NUMBER_PARTS | GENERATE_WORD_PARTS;
    tokenStream = new WordDelimiterFilter(tokenStream, flags, null);
    tokenStream = new LowerCaseFilter(tokenStream);
    tokenStream = new CalimaNormalizationFilter(tokenStream);
    String tokenized = postNormalize(streamToString(tokenStream));
    tokenized = Normalizer.normalize(tokenized, Form.NFKC).replace('ی', 'ي').replace("ھ", "ه");

    StringBuilder finalTokenized = new StringBuilder();
    for (String token : tokenized.split(" ")) {
      if (isStopwordRemoval() && isDiscard(false, token)) {
        continue;
      }

      finalTokenized.append(token + " ");
    }
    String stemmedTokenized = finalTokenized.toString().trim();
    stemmedTokenized = stem(stemmedTokenized);
    return stemmedTokenized.split(" ");
  }

  @Override
  public String stem(String token) {
    if (token.matches(".*[a-zA-Z0-9۱-۹]+.*")) {
      return token.toLowerCase();
    } else {
      token = token.replaceAll("(.+?)\\1{2,}", "$1");
      StringBuilder sb = new StringBuilder();
      for (char c : token.toCharArray()) {
        if (Character.UnicodeBlock.of(c) == ARABIC) {
          sb.append(c);
        } else {
          break;
        }
      }
      token = sb.toString();
      if (token.isEmpty()) {
        return token;
      } else {
        String stem = knownStems.get(token);
        if (stem == null) {
          return token;
        }
        return stem;
      }
    }
  }
}