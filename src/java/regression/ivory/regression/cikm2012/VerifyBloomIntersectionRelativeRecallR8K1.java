package ivory.regression.cikm2012;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class VerifyBloomIntersectionRelativeRecallR8K1 {
  @Test public void runRegression() throws Exception {
    RelativeRecallUtil.runRegression(8, 1, 74);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(VerifyBloomIntersectionRelativeRecallR8K1.class);
  }
}
