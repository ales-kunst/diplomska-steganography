package org.diplomska.jpeg.encoder;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.block.step.ZigZagAndRlcBlockStep;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;

import java.net.URL;

import javax.imageio.ImageIO;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/06/11
 * @author         Ales Kunst
 */
public class JpegImageBlockTest {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegImageBlockTest.class);

   //~--- fields --------------------------------------------------------------

   /** Jpeg image context of test png */
   private JpegImageContext fTestPngJpegImageContext;

   //~--- set methods ---------------------------------------------------------

   /**
    *    Set up.
    *
    *
    *    @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      URL           testPngUrl   = ClassLoader.getSystemClassLoader().getResource("test.png");
      BufferedImage testPngImage = ImageIO.read(testPngUrl);

      fTestPngJpegImageContext = new JpegImageContext(testPngImage);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    */
   @Test
   public void testConvertToBitsArray() {
      JpegImageBlock jpegImageBlock       = createJpegImageBlock_01();
      byte[]         bitsOfYLumaComponent =
         jpegImageBlock.convertZigZagEncodedArrayToBitsArray(jpegImageBlock.getZigZagRlcEncodedYLumaArray());

      Assert.assertEquals(313, bitsOfYLumaComponent.length);
   }

   /**
    * Test minimum size of byte array to encode rlc encoded array from jpeg image block.
    *
    */
   @Test
   public void testGetMinimumByteArraySize() {
      JpegImageBlock jpegImageBlock = createJpegImageBlock_01();
      int            size           =
         jpegImageBlock.getMinimumByteArraySize(jpegImageBlock.getZigZagRlcEncodedYLumaArray());

      Assert.assertEquals(313, size);
   }

   /**
    * Test method for {@link org.diplomska.jpeg.encoder.JpegEncoder#encode()}.
    */
   @Test
   public void testSplitImageIntoBlocks() {
      int jpegImageBlocksWidth  = fTestPngJpegImageContext.getJpegImageBlocksWidth();
      int jpegImageBlocksHeight = fTestPngJpegImageContext.getJpegImageBlocksHeight();

      assertEquals(3, jpegImageBlocksWidth);
      assertEquals(2, jpegImageBlocksHeight);

      for (int row = 0; row < jpegImageBlocksHeight; row++) {
         for (int column = 0; column < jpegImageBlocksHeight; column++) {
            try {
               assertNotNull(fTestPngJpegImageContext.getJpegImageBlock(column, row));
            } catch (AssertionError ae) {
               LOGGER.error("Jpeg image block [{}, {}]null!", column, row);
            }
         }
      }
   }

   /**
    * Create first jpeg image block.
    *
    * @return
    */
   private JpegImageBlock createJpegImageBlock_01() {
      JpegImageBlock        returnJpegImageBlock  = new JpegImageBlock(8, 8);
      ZigZagAndRlcBlockStep zigZagAndRlcBlockStep = new ZigZagAndRlcBlockStep();
      double[][]            lumaYJpegBlock        = {
         { 44, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCrJpegBlock = {
         { 22, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCbJpegBlock = {
         { 11, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };

      returnJpegImageBlock.setYCrCbMatrixForCalculation(lumaYJpegBlock, chromaCrJpegBlock, chromaCbJpegBlock);
      zigZagAndRlcBlockStep.execute(returnJpegImageBlock);

      return returnJpegImageBlock;
   }
}
