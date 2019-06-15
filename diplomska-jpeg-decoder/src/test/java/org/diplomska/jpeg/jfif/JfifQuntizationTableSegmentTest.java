package org.diplomska.jpeg.jfif;

import org.diplomska.jpeg.decoder.ReadJpegInJfif;
import org.diplomska.util.JpegImageUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 24.nov.2015
 * @author         Ales Kunst
 */
public class JfifQuntizationTableSegmentTest extends JfifSegmentTest {

   /** Luminance quantization matrix. */
   private static final int[][] LUMINANCE_QUANTIZATION_MATRIX_RAW = new int[][] {
      { 16, 11, 10, 16, 24, 40, 51, 61 }, { 12, 12, 14, 19, 26, 58, 60, 55 }, { 14, 13, 16, 24, 40, 57, 69, 56 },
      { 14, 17, 22, 29, 51, 87, 80, 62 }, { 18, 22, 37, 56, 68, 109, 103, 77 }, { 24, 35, 55, 64, 81, 104, 113, 92 },
      { 49, 64, 78, 87, 103, 121, 120, 101 }, { 72, 92, 95, 98, 112, 100, 103, 99 }
   };

   /** Chrominance quantization matrix. */
   private static final int[][] CHROMINANCE_QUANTIZATION_MATRIX_RAW = new int[][] {
      { 17, 18, 24, 47, 99, 99, 99, 99 }, { 18, 21, 26, 66, 99, 99, 99, 99 }, { 24, 26, 56, 99, 99, 99, 99, 99 },
      { 47, 66, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 },
      { 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 }
   };

   //~--- set methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
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
   public void testGetQuantizationMatrix() {
      String      filename               = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream = null;

      alpeTriglavJpgInStream = getResourceStream(filename);

      ReadJpegInJfif              readJpegInJfif       = new ReadJpegInJfif(alpeTriglavJpgInStream);
      List<JfifSegment>           jfifSegments         = readJpegInJfif.getJfifSegments(JfifSegmentFactory.DQT_MARKER);
      JfifQuntizationTableSegment quantTableSegment_00 = (JfifQuntizationTableSegment) jfifSegments.get(0);
      JfifQuntizationTableSegment quantTableSegment_01 = (JfifQuntizationTableSegment) jfifSegments.get(1);

      for (int row = 0; row < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {
            int actualLuminanceValue   = quantTableSegment_00.getQuantizationMatrix()[row][column];
            int actualChrominanceValue = quantTableSegment_01.getQuantizationMatrix()[row][column];

            Assert.assertEquals(LUMINANCE_QUANTIZATION_MATRIX_RAW[row][column], actualLuminanceValue);
            Assert.assertEquals(CHROMINANCE_QUANTIZATION_MATRIX_RAW[row][column], actualChrominanceValue);
         }
      }
   }
}
