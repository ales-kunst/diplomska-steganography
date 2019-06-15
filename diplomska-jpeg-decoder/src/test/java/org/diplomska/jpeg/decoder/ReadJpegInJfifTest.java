package org.diplomska.jpeg.decoder;

import org.diplomska.jpeg.jfif.JfifHeaderSegment;
import org.diplomska.jpeg.jfif.JfifQuntizationTableSegment;
import org.diplomska.jpeg.jfif.JfifSegment;
import org.diplomska.jpeg.jfif.JfifSegmentFactory;
import org.diplomska.jpeg.jfif.JfifUnknownSegment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/16
 * @author         Ales Kunst
 */
public class ReadJpegInJfifTest {

   /**
    * Setup
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Teardown
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Method description
    *
    */
   @Test
   public void testGetJfifSegments() {
      String      filename               = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream = null;

      alpeTriglavJpgInStream = getResourceStream(filename);

      ReadJpegInJfif    readJpegInJfif = new ReadJpegInJfif(alpeTriglavJpgInStream);
      List<JfifSegment> jfifSegments   = readJpegInJfif.getJfifSegments(JfifSegmentFactory.DQT_MARKER);

      Assert.assertEquals(2, jfifSegments.size());
      Assert.assertTrue(jfifSegments.get(0) instanceof JfifQuntizationTableSegment);
      Assert.assertTrue(jfifSegments.get(1) instanceof JfifQuntizationTableSegment);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testInitialize() {
      String      filename               = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream = null;

      alpeTriglavJpgInStream = getResourceStream(filename);

      ReadJpegInJfif readJpegInJfif = new ReadJpegInJfif(alpeTriglavJpgInStream);
      JfifSegment    jfifSoiSegment = readJpegInJfif.jfifSegments.get(1);

      Assert.assertEquals(11, readJpegInJfif.jfifSegments.size());
      Assert.assertTrue((readJpegInJfif.jfifSegments.get(0) instanceof JfifUnknownSegment));
      Assert.assertTrue((readJpegInJfif.jfifSegments.get(1) instanceof JfifHeaderSegment));
      Assert.assertTrue(jfifSoiSegment.getContentSize() == 16);
      Assert.assertTrue(jfifSoiSegment.getPureSegmentContents().length == 14);
   }

   /**
    * Test method.
    *
    *
    * @throws IOException
    */
   @Test
   public void testReadFromFile() throws IOException {
      String      filename                   = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream     = null;
      InputStream testAlpeTriglavJpgInStream = null;
      int         readByte                   = 0;
      int         bufferIndex                = 0;

      try {
         alpeTriglavJpgInStream = getResourceStream(filename);

         byte[] bytesReadBuffer = ReadJpegInJfif.readFromFile(alpeTriglavJpgInStream);

         testAlpeTriglavJpgInStream = getResourceStream(filename);

         while ((readByte = testAlpeTriglavJpgInStream.read()) != -1) {
            Assert.assertEquals("Buffer at location [" + bufferIndex + "] is not equal", bytesReadBuffer[bufferIndex],
                                (byte) readByte);
            bufferIndex++;
         }
      } finally {
         alpeTriglavJpgInStream.close();
         testAlpeTriglavJpgInStream.close();
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param filename
    *
    * @return
    */
   private InputStream getResourceStream(String filename) {
      return ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
   }
}
