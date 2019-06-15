package org.diplomska.jpeg.processor.image.step;

import org.diplomska.jpeg.encoder.JpegEncoderTestUtil;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.JpegImageBlockProcessor;
import org.diplomska.jpeg.processor.image.step.WriteJpegInJfif;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/07/18
 * @author         Ales Kunst
 */
public class WriteJpegFileInJfifTest {

   /** Jpeg image context of test png */
   private JpegImageContext fTestBlacWhitePngJpegImageContext;

   /** Test png jpeg context */
   private JpegImageContext fTestPngJpegImageContext;

   //~--- set methods ---------------------------------------------------------

   /**
    * Setup.
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      fTestBlacWhitePngJpegImageContext = JpegEncoderTestUtil.createTestBlackWhitePngJpegImageContext();
      fTestPngJpegImageContext          = JpegEncoderTestUtil.createTestPngJpegImageContext();
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Tear down.
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Test execute.
    *
    */
   @Test
   public void testExecute() {
      JpegImageBlockProcessor jpegImageProcessor =
         JpegEncoderTestUtil.runUntilWriteJfif(fTestBlacWhitePngJpegImageContext);
      WriteJpegInJfif writeJpegInJfif =
         (WriteJpegInJfif) jpegImageProcessor.getJpegImageProcessorStep(JpegEncoderTestUtil.WRITE_JFIF_STEP);
      ByteArrayOutputStream jfifByteArray = (ByteArrayOutputStream) writeJpegInJfif.getOutStream();

      Assert.assertEquals(-1, jfifByteArray.toByteArray()[0]);
   }

   /**
    * Test get Length bytes.
    *
    */
   @Test
   public void testGetLengthBytes() {
      JpegImageBlockProcessor jpegImageProcessor =
         JpegEncoderTestUtil.runUntilWriteJfif(fTestBlacWhitePngJpegImageContext);
      WriteJpegInJfif writeJpegInJfif =
         (WriteJpegInJfif) jpegImageProcessor.getJpegImageProcessorStep(JpegEncoderTestUtil.WRITE_JFIF_STEP);

      Assert.assertArrayEquals(new byte[] { 0, 15 }, writeJpegInJfif.getLengthBytes(15));
      Assert.assertArrayEquals(new byte[] { 0, (byte) 255 }, writeJpegInJfif.getLengthBytes(255));
      Assert.assertArrayEquals(new byte[] { 1, 0 }, writeJpegInJfif.getLengthBytes(256));
      Assert.assertArrayEquals(new byte[] { 2, 0 }, writeJpegInJfif.getLengthBytes(512));
   }
}
