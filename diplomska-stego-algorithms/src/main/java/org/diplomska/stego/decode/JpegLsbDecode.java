package org.diplomska.stego.decode;

import org.apache.commons.io.FileUtils;

import org.diplomska.stego.DataContext;
import org.diplomska.stego.StegoDecodingAlgorithm;
import org.diplomska.util.BitBuffer;
import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.JpegImageConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/15
 * @author         Enter your name here...
 */
public class JpegLsbDecode implements StegoDecodingAlgorithm, JpegImageConstants {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegLsbDecode.class);

   //~--- fields --------------------------------------------------------------

   /** Field description */
   int bitPosition;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   public JpegLsbDecode() {
      bitPosition = 0;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void decode(DataContext aDataContext, OutputStream aOutputStream) throws Exception {
      int       imageWidth  = aDataContext.getImageWidth();
      int       imageHeight = aDataContext.getImageHeight();
      BitBuffer messageSize = new BitBuffer(BitNumbering.MSB_FIRST);
      BitBuffer message     = new BitBuffer(BitNumbering.MSB_FIRST);

      bitPosition = 0;

      // writeCoefficientsIntoFile(aDataContext, "D:/temp/tmp/Diplomska/Stego_Program/coefficients_decode_before.log");
      // Gets size of message. It is 32 bits long
      for (int row = 0; row < imageHeight; row++) {
         for (int column = 0; column < imageWidth; column++) {
            boolean isBlockFirsRow     = (row % 8) == 0;
            boolean isBlockFirstColumn = (column % 8) == 0;
            boolean isDcElem           = isBlockFirsRow && isBlockFirstColumn;

            if (!isDcElem && (bitPosition < SIZE_LENGTH_IN_BITS)) {
               int yLumaValue    = aDataContext.getImageData(row, column, Y_LUMA);
               int cbChromaValue = aDataContext.getImageData(row, column, CB_CHROMA);
               int crChromaValue = aDataContext.getImageData(row, column, CR_CHROMA);

               decodeFromYLuma(messageSize, SIZE_LENGTH_IN_BITS, row, column, yLumaValue);
               decodeFromCbChroma(messageSize, SIZE_LENGTH_IN_BITS, row, column, cbChromaValue);
               decodeFromCrChroma(messageSize, SIZE_LENGTH_IN_BITS, row, column, crChromaValue);
            }

            if (bitPosition == SIZE_LENGTH_IN_BITS) {
               break;
            }
         }    // for column

         if (bitPosition == SIZE_LENGTH_IN_BITS) {
            break;
         }
      }       // for row

      int wholeMessageSize = ByteBuffer.wrap(messageSize.toByteArray()).order(ByteOrder.BIG_ENDIAN).getInt();

      bitPosition = 0;

      // Get whole message with size also
      for (int row = 0; row < imageHeight; row++) {
         for (int column = 0; column < imageWidth; column++) {
            boolean isBlockFirsRow     = (row % 8) == 0;
            boolean isBlockFirstColumn = (column % 8) == 0;
            boolean isDcElem           = isBlockFirsRow && isBlockFirstColumn;

            if (!isDcElem && (bitPosition < wholeMessageSize)) {
               int yLumaValue    = aDataContext.getImageData(row, column, Y_LUMA);
               int cbChromaValue = aDataContext.getImageData(row, column, CB_CHROMA);
               int crChromaValue = aDataContext.getImageData(row, column, CR_CHROMA);

               decodeFromYLuma(message, wholeMessageSize, row, column, yLumaValue);
               decodeFromCbChroma(message, wholeMessageSize, row, column, cbChromaValue);
               decodeFromCrChroma(message, wholeMessageSize, row, column, crChromaValue);
            }

            if (bitPosition == wholeMessageSize) {
               break;
            }
         }

         if (bitPosition == wholeMessageSize) {
            break;
         }
      }

      int    messageLength = (wholeMessageSize / Byte.SIZE) - SIZE_LENGTH_IN_BYTES;
      byte[] rawMessage    = new byte[messageLength];

      System.arraycopy(message.toByteArray(), SIZE_LENGTH_IN_BYTES, rawMessage, 0, messageLength);
      aOutputStream.write(rawMessage);
   }    // decode

   /**
    * Method description
    *
    *
    * @param aBitBuffer
    * @param aMaxSize
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   private void decodeFromCbChroma(BitBuffer aBitBuffer, int aMaxSize, int aRow, int aColumn, int aValue) {
      byte cbChromaValue = (byte) aValue;

      if ((cbChromaValue != 0) && (bitPosition < aMaxSize)) {
         byte value = (byte) (cbChromaValue & 0x01);

         aBitBuffer.put(value);
         bitPosition++;

         // logging(cbChromaValue);
      }
   }

   /**
    * Method description
    *
    *
    * @param aBitBuffer
    * @param aMaxSize
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   private void decodeFromCrChroma(BitBuffer aBitBuffer, int aMaxSize, int aRow, int aColumn, int aValue) {
      byte crChromaValue = (byte) aValue;

      if ((crChromaValue != 0) && (bitPosition < aMaxSize)) {
         byte value = (byte) (crChromaValue & 0x01);

         aBitBuffer.put(value);
         bitPosition++;

         // logging(crChromaValue);
      }
   }

   /**
    * Method description
    *
    *
    * @param aBitBuffer
    * @param aMaxSize
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   private void decodeFromYLuma(BitBuffer aBitBuffer, int aMaxSize, int aRow, int aColumn, int aValue) {
      byte yLumaValue = (byte) aValue;

      if ((yLumaValue != 0) && (bitPosition < aMaxSize)) {
         byte value = (byte) (yLumaValue & 0x01);

         aBitBuffer.put(value);
         bitPosition++;

         // logging(yLumaValue);
      }
   }

   /**
    * Method description
    *
    *
    * @param aValue
    */
   private void logging(int aValue) {
      if ((bitPosition <= 32) && (aValue != 0)) {
         System.out.println(Integer.toBinaryString(aValue));
      }
   }

   /**
    * Method description
    *
    *
    *
    * @param aDataContext
    * @param aFilename
    * @throws IOException
    */
   private void writeCoefficientsIntoFile(DataContext aDataContext, String aFilename) throws IOException {
      StringBuffer array = new StringBuffer();
      File         file  = new File(aFilename);

      for (int row = 0; row < aDataContext.getImageHeight(); row++) {
         for (int column = 0; column < aDataContext.getImageWidth(); column++) {
            int yLumaValue    = aDataContext.getImageData(row, column, Y_LUMA);
            int cbChromaValue = aDataContext.getImageData(row, column, CB_CHROMA);
            int crChromaValue = aDataContext.getImageData(row, column, CR_CHROMA);

            array.append(String.format("%d %d %d ", yLumaValue, cbChromaValue, crChromaValue));
         }

         array.append("\n");
      }

      FileUtils.writeByteArrayToFile(file, array.toString().getBytes());
   }
}
