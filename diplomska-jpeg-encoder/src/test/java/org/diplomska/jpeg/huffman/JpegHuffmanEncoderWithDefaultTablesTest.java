package org.diplomska.jpeg.huffman;

import org.diplomska.jpeg.huffman.HuffmanElement;
import org.diplomska.jpeg.huffman.HuffmanOutputStructure;
import org.diplomska.jpeg.huffman.JpegHuffmanEncoderWithDefaultTables;

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
 * @version        Enter version here..., 15/07/23
 * @author         Enter your name here...
 */
public class JpegHuffmanEncoderWithDefaultTablesTest {

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
   public void testEncode() {
      JpegHuffmanEncoderWithDefaultTables jpegHuffencoder        = new JpegHuffmanEncoderWithDefaultTables();
      HuffmanOutputStructure              huffmanOutputStructure =
         jpegHuffencoder.encode(getZigZagBitsArrayOfTestBlackWhitePngImage());

      Assert.assertEquals(48, huffmanOutputStructure.getHuffmanEncodedBitsArray().length);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testInitializeHuffmanElementsCollections() {
      JpegHuffmanEncoderWithDefaultTables jpegHuffencoder = new JpegHuffmanEncoderWithDefaultTables();

      jpegHuffencoder.initializeHuffmanElementsCollections();
      checkLumaDcHuffmanElements(jpegHuffencoder);
      checkLumaAcHuffmanElements(jpegHuffencoder);

      // checkChromaDcHuffmanElements(jpegHuffencoder);
      checkChromaAcHuffmanElements(jpegHuffencoder);
   }

   /**
    * Method description
    *
    *
    *
    * @param aJpegHuffencoder
    */
   private void checkChromaAcHuffmanElements(JpegHuffmanEncoderWithDefaultTables aJpegHuffencoder) {
      List<HuffmanElement> chromaAcElements = aJpegHuffencoder.getChromaAcHuffmanElements();
      int                  index            = 0;

      for (short huffmanElementValue : getRefChromaAcHuffmanTableValues()) {
         byte refByteHuffmanElementValue = (byte) huffmanElementValue;
         byte actByteHuffmanElementValue = chromaAcElements.get(index).getValue();

         Assert.assertEquals(refByteHuffmanElementValue, actByteHuffmanElementValue);
         index++;
      }
   }

   /**
    *    Method description
    *
    *
    *    @param aJpegHuffencoder
    */
   private void checkLumaAcHuffmanElements(JpegHuffmanEncoderWithDefaultTables aJpegHuffencoder) {
      List<HuffmanElement> lumaAcElements = aJpegHuffencoder.getLumaAcHuffmanElements();
      int                  index          = 0;

      for (short huffmanElementValue : getRefLumaAcHuffmanTableValues()) {
         byte refByteHuffmanElementValue = (byte) huffmanElementValue;
         byte actByteHuffmanElementValue = lumaAcElements.get(index).getValue();

         Assert.assertEquals(refByteHuffmanElementValue, actByteHuffmanElementValue);
         index++;
      }
   }

   /**
    * Method description
    *
    *
    *
    * @param aJpegHuffencoder
    */
   private void checkLumaDcHuffmanElements(JpegHuffmanEncoderWithDefaultTables aJpegHuffencoder) {
      List<HuffmanElement> lumaDcElements = aJpegHuffencoder.getLumaDcHuffmanElements();
      int                  index          = 0;

      for (short huffmanElementValue : getRefLumaDcHuffmanTableValues()) {
         byte refByteHuffmanElementValue = (byte) huffmanElementValue;
         byte actByteHuffmanElementValue = lumaDcElements.get(index).getValue();

         Assert.assertEquals(refByteHuffmanElementValue, actByteHuffmanElementValue);
         index++;
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   private short[] getRefChromaAcHuffmanTableValues() {
      return new short[] { 0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41, 0x51, 0x07, 0x61,
                           0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91, 0xA1, 0xB1, 0xC1, 0x09, 0x23, 0x33,
                           0x52, 0xF0, 0x15, 0x62, 0x72, 0xD1, 0x0A, 0x16, 0x24, 0x34, 0xE1, 0x25, 0xF1, 0x17, 0x18,
                           0x19, 0x1A, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x43, 0x44,
                           0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63,
                           0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A,
                           0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97,
                           0x98, 0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4,
                           0xB5, 0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8, 0xC9, 0xCA,
                           0xD2, 0xD3, 0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE2, 0xE3, 0xE4, 0xE5, 0xE6, 0xE7,
                           0xE8, 0xE9, 0xEA, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA };
   }

   /**
    *    Method description
    *
    *
    *    @return
    */
   private short[] getRefLumaAcHuffmanTableValues() {
      return new short[] { 0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06, 0x13, 0x51, 0x61,
                           0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xA1, 0x08, 0x23, 0x42, 0xB1, 0xC1, 0x15, 0x52,
                           0xD1, 0xF0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0A, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x25,
                           0x26, 0x27, 0x28, 0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x43, 0x44, 0x45,
                           0x46, 0x47, 0x48, 0x49, 0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63, 0x64,
                           0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0x83,
                           0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99,
                           0x9A, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6,
                           0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3,
                           0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE1, 0xE2, 0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8,
                           0xE9, 0xEA, 0xF1, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA };
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private short[] getRefLumaDcHuffmanTableValues() {
      return new short[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B };
   }

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
