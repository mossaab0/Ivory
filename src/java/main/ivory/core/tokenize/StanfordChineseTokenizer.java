package ivory.core.tokenize;

import ivory.core.Constants;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;

public class StanfordChineseTokenizer extends Tokenizer {
  private static final Logger LOG = Logger.getLogger(StanfordChineseTokenizer.class);

  CRFClassifier<CoreLabel> classifier;
  DocumentReaderAndWriter<CoreLabel> readerWriter;

  public StanfordChineseTokenizer() {
    super();
  }

  @Override
  public void configure(Configuration conf) {
    FileSystem fs;
    try {
      fs = FileSystem.get(conf);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    configure(conf, fs);
  }

  @Override
  public void configure(Configuration conf, FileSystem fs) {
    Properties props = new Properties();
    props.setProperty("sighanCorporaDict", conf.get(Constants.TokenizerData));
    props.setProperty("serDictionary", conf.get(Constants.TokenizerData) + "/dict-chris6.ser.gz");
    props.setProperty("inputEncoding", "UTF-8");
    props.setProperty("sighanPostProcessing", "true");

    try {
      classifier = new CRFClassifier<CoreLabel>(props);
      FSDataInputStream in = fs.open(new Path(conf.get(Constants.TokenizerData) + "/ctb"));
      classifier.loadClassifier(in, props);
      classifier.flags.setProperties(props);
      readerWriter = classifier.makeReaderAndWriter();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Tokenizer not configured properly!");
    }
  }

  @Override
  public String[] processContent(String text) {
    String[] tokens = null;
    try {
      text = postNormalize(preNormalize(text).toLowerCase()); // normalization for non-Chinese
                                                              // characters
      StringWriter sw = new StringWriter();
      for (List<CoreLabel> doc : classifier.classify(text)) {
        classifier.writeAnswers(doc, new PrintWriter(sw), readerWriter);
      }
      tokens = sw.toString().trim().split("\\s+");
    } catch (IOException e) {
      LOG.info("Problem in tokenizing Chinese");
      e.printStackTrace();
    }
    if (vocab == null) {
      return tokens;
    } else {
      StringBuilder finalTokenized = new StringBuilder();
      for (String token : tokens) {
        if (vocab.get(token) <= 0) {
          continue;
        }
        finalTokenized.append(token + " ");
      }
      return finalTokenized.toString().trim().split("\\s+");
    }
  }

  @Override
  public String removeBorderStopWords(String tokenizedText) {
    return tokenizedText;
  }

  @Override
  public boolean isStopWord(String token) {
    return false;
  }
}