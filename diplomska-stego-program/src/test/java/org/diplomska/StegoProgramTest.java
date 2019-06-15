package org.diplomska;

import org.junit.Assert;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Unit test for simple App.
 */
public class StegoProgramTest {

   /**
    * Method description
    *
    */
   @Test
   public void getMsg() {
      StegoProgram app = new StegoProgram(null);

      Assert.assertNotNull(app);
   }
}
