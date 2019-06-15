package org.diplomska.jpeg.processor.image.step;

import org.diplomska.jpeg.encoder.JpegEncoderTestUtil;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.JpegImageBlockProcessor;
import org.diplomska.jpeg.processor.image.step.HuffmanCodingStep;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 11.jul. 2015
 * @author         Ales Kunst
 */
public class HuffmanCodingStepTest {

   /** Jpeg image context of test png */
   private JpegImageContext fTestBlacWhitePngJpegImageContext;

   /** Field description */
   private JpegImageContext fTestPngJpegImageContext;

   //~--- set methods ---------------------------------------------------------

   /**
    * Test setup method
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
    * Method description
    *
    */
   @Test
   public void testGetBitsArray() {
      JpegImageBlockProcessor jpegImageProcessor =
         JpegEncoderTestUtil.runUntilHuffmanCoding(fTestBlacWhitePngJpegImageContext);
      HuffmanCodingStep huffmanCodingStep =
         (HuffmanCodingStep) jpegImageProcessor.getJpegImageProcessorStep(JpegEncoderTestUtil.HUFFMAN_CODING_STEP);
      byte[] bitRepresentation = huffmanCodingStep.getBitsArray(fTestBlacWhitePngJpegImageContext);

      // TODO Kunst: Write tests!
//    Assert.assertArrayEquals(getZigZagBitsArrayOfTestBlackWhitePngImage(), bitRepresentation);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetBitsArray_01() {
      JpegImageBlockProcessor jpegImageProcessor = JpegEncoderTestUtil.runUntilHuffmanCoding(fTestPngJpegImageContext);
      HuffmanCodingStep       huffmanCodingStep  =
         (HuffmanCodingStep) jpegImageProcessor.getJpegImageProcessorStep(JpegEncoderTestUtil.HUFFMAN_CODING_STEP);
      byte[] bitRepresentation = huffmanCodingStep.getBitsArray(fTestPngJpegImageContext);
      long   bitArrayLength    = JpegEncoderTestUtil.getBitArrayLengthForHuffmanEncoding(fTestPngJpegImageContext);

      Assert.assertEquals(bitArrayLength, bitRepresentation.length);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   private byte[] getZigZagBitsArrayOfTestBlackWhitePngImage() {
      ByteArrayOutputStream returnByteArrayOutStream = new ByteArrayOutputStream();

      // byte[]                eobArray                 = { 0, 0, 0, 0, 0, 0, 0, 0 };
      byte[] yLumaZigZagBits_0_0    = { 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
      byte[] cbChromaZigZagBits_0_0 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      byte[] crChromaZigZagBits_0_0 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      byte[] yLumaZigZagBits_0_1    = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
      byte[] cbChromaZigZagBits_0_1 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      byte[] crChromaZigZagBits_0_1 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

      try {

         // eob after is not needed because the whole array has only 0
         returnByteArrayOutStream.write(yLumaZigZagBits_0_0);
         returnByteArrayOutStream.write(cbChromaZigZagBits_0_0);
         returnByteArrayOutStream.write(crChromaZigZagBits_0_0);
         returnByteArrayOutStream.write(yLumaZigZagBits_0_1);
         returnByteArrayOutStream.write(cbChromaZigZagBits_0_1);
         returnByteArrayOutStream.write(crChromaZigZagBits_0_1);
      } catch (IOException e) {
         Assert.assertTrue(false);
      }

      // return
      return returnByteArrayOutStream.toByteArray();
   }
}
