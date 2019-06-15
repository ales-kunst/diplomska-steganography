package org.diplomska.jpeg.huffman;

import org.diplomska.jpeg.encoder.JpegEncoderTestUtil;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.huffman.HuffmanElement;
import org.diplomska.jpeg.huffman.HuffmanOutputStructure;
import org.diplomska.jpeg.huffman.JpegHuffmanEncoderWithOptimizedTables;
import org.diplomska.jpeg.processor.JpegImageBlockProcessor;
import org.diplomska.jpeg.processor.image.step.HuffmanCodingStep;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/07/13
 * @author         Ales Kunst
 */
public class JpegHuffmanEncoderWithOptimizedTablesTest {

   /** Field description */
   private JpegImageContext fTestBlackWhitePngJpegImageContext;

   /** Field description */
   private JpegImageContext fTestPngJpegImageContext;

   //~--- set methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      fTestBlackWhitePngJpegImageContext = JpegEncoderTestUtil.createTestBlackWhitePngJpegImageContext();
      fTestPngJpegImageContext           = JpegEncoderTestUtil.createTestPngJpegImageContext();
   }

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
   public void testEncode() {
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder         =
         runGatheringOfByteFrequenciesForTestBlackWhitePng();
      HuffmanOutputStructure                huffmanOutputStructure =
         huffmanEncoder.encode(getZigZagBitsArrayOfTestBlackWhitePngImage());
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGatherByteFrequencies() {
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder = runGatheringOfByteFrequenciesForTestBlackWhitePng();

      // TODO Kunst: Write tests!
//    Assert.assertEquals(3, huffmanEncoder.getHuffmanElements().size());
//    Assert.assertEquals(6, huffmanEncoder.getHuffmanElementByValue((byte) 0).getFrequency());
//    Assert.assertEquals(2, huffmanEncoder.getHuffmanElementByValue((byte) 6).getFrequency());
//    Assert.assertEquals(1, huffmanEncoder.getHuffmanElementByValue((byte) 8).getFrequency());
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGatherByteFrequencies_01() {
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder  = runGatheringOfByteFrequenciesForTestPng();
      List<HuffmanElement>                  byteFrequencies = huffmanEncoder.getHuffmanElements();

      // TODO Kunst: Write tests!
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGenerateHuffmanCodes() {
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder                  = runGatheringOfByteFrequenciesForTestPng();
      List<HuffmanElement>                  huffmanElements                 =
         huffmanEncoder.getHuffmanElementsWithGeneratedCodeLengths();
      List<HuffmanElement>                  huffmanElementsWithHuffmanCodes =
         huffmanEncoder.generateHuffmanCodes(huffmanElements);

      // TODO Kunst: Write tests!
//    Assert.assertEquals(0, huffmanElementsWithHuffmanCodes.get(0).getHuffmanCode());
//    Assert.assertEquals(2, huffmanElementsWithHuffmanCodes.get(1).getHuffmanCode());
//    Assert.assertEquals(3, huffmanElementsWithHuffmanCodes.get(2).getHuffmanCode());
//    Assert.assertEquals(4, huffmanElementsWithHuffmanCodes.get(3).getHuffmanCode());
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetMaxCodeLength() {
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder = runGatheringOfByteFrequenciesForTestPng();

      // List<HuffmanElement>      byteFrequencies = huffmanEncoder.getHuffmanElements();
      int maxCodeLength = huffmanEncoder.getMaxCodeLength(huffmanEncoder.getHuffmanElementsWithGeneratedCodeLengths());

      // TODO Kunst: Write tests!
//    Assert.assertEquals(8, maxCodeLength);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testShortenCodeLengths() {
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder         = runGatheringOfByteFrequenciesForTestPng();
      List<HuffmanElement>                  huffmanElements        =
         huffmanEncoder.getHuffmanElementsWithGeneratedCodeLengths();
      List<HuffmanElement>                  changedHuffmanElements = huffmanEncoder.shortenCodeLengths(huffmanElements,
                                                                                                       6);
      int                                   maxCodeLength          =
         huffmanEncoder.getMaxCodeLength(changedHuffmanElements);

      // TODO Kunst: Write tests!
//    Assert.assertEquals(6, maxCodeLength);
//
//    // [[ [1, 63, 2] 63 ],
//    Assert.assertEquals(2, changedHuffmanElements.get(0).getCodeLength());
//    Assert.assertEquals(1, changedHuffmanElements.get(0).getValue().intValue());
//
//    // [ [0, 17, 3] 17 ],
//    Assert.assertEquals(3, changedHuffmanElements.get(1).getCodeLength());
//    Assert.assertEquals(0, changedHuffmanElements.get(1).getValue().intValue());
//
//    // [ [2, 23, 3] 23 ],
//    Assert.assertEquals(3, changedHuffmanElements.get(2).getCodeLength());
//    Assert.assertEquals(2, changedHuffmanElements.get(2).getValue().intValue());
//
//    // [ [3, 20, 3] 20 ],
//    Assert.assertEquals(3, changedHuffmanElements.get(3).getCodeLength());
//    Assert.assertEquals(3, changedHuffmanElements.get(3).getValue().intValue());
//
//    // [ [4, 11, 4] 11 ],
//    Assert.assertEquals(4, changedHuffmanElements.get(4).getCodeLength());
//    Assert.assertEquals(4, changedHuffmanElements.get(4).getValue().intValue());
//
//    // [ [17, 15, 6] 15 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(5).getCodeLength());
//    Assert.assertEquals(17, changedHuffmanElements.get(5).getValue().intValue());
//
//    // [ [-111, 5, 6] 5 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(6).getCodeLength());
//    Assert.assertEquals(-111, changedHuffmanElements.get(6).getValue().intValue());
//
//    // [ [6, 5, 6] 5 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(7).getCodeLength());
//    Assert.assertEquals(6, changedHuffmanElements.get(7).getValue().intValue());
//
//    // [ [113, 4, 6] 4 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(8).getCodeLength());
//    Assert.assertEquals(113, changedHuffmanElements.get(8).getValue().intValue());
//
//    // [ [5, 8, 6] 8 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(9).getCodeLength());
//    Assert.assertEquals(5, changedHuffmanElements.get(9).getValue().intValue());
//
//    // [ [33, 3, 6] 3 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(10).getCodeLength());
//    Assert.assertEquals(33, changedHuffmanElements.get(10).getValue().intValue());
//
//    // [ [7, 2, 6] 2 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(11).getCodeLength());
//    Assert.assertEquals(7, changedHuffmanElements.get(11).getValue().intValue());
//
//    // [ [81, 3, 6] 3 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(12).getCodeLength());
//    Assert.assertEquals(81, changedHuffmanElements.get(12).getValue().intValue());
//
//    // [ [49, 3, 6] 3 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(13).getCodeLength());
//    Assert.assertEquals(49, changedHuffmanElements.get(13).getValue().intValue());
//
//    // [ [18, 1, 6] 1 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(14).getCodeLength());
//    Assert.assertEquals(18, changedHuffmanElements.get(14).getValue().intValue());
//
//    // [ [51, 2, 6] 2 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(15).getCodeLength());
//    Assert.assertEquals(51, changedHuffmanElements.get(15).getValue().intValue());
//
//    // [ [-63, 2, 6] 2 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(16).getCodeLength());
//    Assert.assertEquals(-63, changedHuffmanElements.get(16).getValue().intValue());
//
//    // [ [20, 2, 6] 2 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(17).getCodeLength());
//    Assert.assertEquals(20, changedHuffmanElements.get(17).getValue().intValue());
//
//    // [ [82, 2, 6] 2 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(18).getCodeLength());
//    Assert.assertEquals(82, changedHuffmanElements.get(18).getValue().intValue());
//
//    // [ [97, 1, 6] 1 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(19).getCodeLength());
//    Assert.assertEquals(97, changedHuffmanElements.get(19).getValue().intValue());
//
//    // [ [65, 1, 6] 1 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(20).getCodeLength());
//    Assert.assertEquals(65, changedHuffmanElements.get(20).getValue().intValue());
//
//    // [ [54, 1, 6] 1 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(21).getCodeLength());
//    Assert.assertEquals(54, changedHuffmanElements.get(21).getValue().intValue());
//
//    // [ [-78, 1, 6] 1 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(22).getCodeLength());
//    Assert.assertEquals(-78, changedHuffmanElements.get(22).getValue().intValue());
//
//    // [ [-127, 1, 6] 1 ],
//    Assert.assertEquals(6, changedHuffmanElements.get(23).getCodeLength());
//    Assert.assertEquals(-127, changedHuffmanElements.get(23).getValue().intValue());
//
//    // [ [116, 1, 6] 1 ]]
//    Assert.assertEquals(6, changedHuffmanElements.get(24).getCodeLength());
//    Assert.assertEquals(116, changedHuffmanElements.get(24).getValue().intValue());
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

   //~--- methods -------------------------------------------------------------

   /**
    *
    *
    *
    * @return
    */
   private JpegHuffmanEncoderWithOptimizedTables runGatheringOfByteFrequenciesForTestBlackWhitePng() {
      JpegImageBlockProcessor jpegImageProcessor =
         JpegEncoderTestUtil.runUntilHuffmanCoding(fTestBlackWhitePngJpegImageContext);
      HuffmanCodingStep huffmanCodingStep =
         (HuffmanCodingStep) jpegImageProcessor.getJpegImageProcessorStep(JpegEncoderTestUtil.HUFFMAN_CODING_STEP);
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder =
         (JpegHuffmanEncoderWithOptimizedTables) huffmanCodingStep.getHuffmanEncoder();

      return huffmanEncoder;
   }

   /**
    *
    *
    *
    * @return
    */
   private JpegHuffmanEncoderWithOptimizedTables runGatheringOfByteFrequenciesForTestPng() {
      JpegImageBlockProcessor jpegImageProcessor = JpegEncoderTestUtil.runUntilHuffmanCoding(fTestPngJpegImageContext);
      HuffmanCodingStep       huffmanCodingStep  =
         (HuffmanCodingStep) jpegImageProcessor.getJpegImageProcessorStep(JpegEncoderTestUtil.HUFFMAN_CODING_STEP);
      JpegHuffmanEncoderWithOptimizedTables huffmanEncoder =
         (JpegHuffmanEncoderWithOptimizedTables) huffmanCodingStep.getHuffmanEncoder();

      return huffmanEncoder;
   }
}
