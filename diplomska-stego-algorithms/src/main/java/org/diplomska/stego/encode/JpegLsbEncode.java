package org.diplomska.stego.encode;

import org.apache.commons.io.FileUtils;

import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.stego.DataContext;
import org.diplomska.stego.StegoEncoderStatistics;
import org.diplomska.stego.StegoEncodingAlgorithm;
import org.diplomska.util.BitWalker;
import org.diplomska.util.JpegImageEncoderUtils;
import org.diplomska.util.JpegImageUtils;
import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.JpegImageConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.Map;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/14
 * @author         Enter your name here...
 */
public class JpegLsbEncode implements StegoEncodingAlgorithm, JpegImageConstants {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegLsbEncode.class);

   //~--- fields --------------------------------------------------------------

   /** Field description */
   int bitPosition;

   /** Field description */
   JpegImageContext jpegImageContext;

   /** Field description */
   private int numOfBitsInMessage;

   /** Field description */
   boolean log;

   /** Field description */
   private StegoEncoderStatistics stegoEncoderStatistics;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aJpegImageContext
    */
   public JpegLsbEncode(JpegImageContext aJpegImageContext) {
      jpegImageContext       = aJpegImageContext;
      stegoEncoderStatistics = new StegoEncoderStatistics(aJpegImageContext);
      initializeEstimatedCapacity(stegoEncoderStatistics);
      log = true;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void encode(DataContext aDataContext, InputStream aMessage) throws Exception {
      int                  imageWidth         = jpegImageContext.getImageWidth();
      int                  imageHeight        = jpegImageContext.getImageHeight();
      int                  capacity           = stegoEncoderStatistics.getEstimatedCapacityInBits();
      byte[]               sizeBytes          = new byte[SIZE_LENGTH_IN_BYTES];
      byte[]               messageBytes       = prepareMessageInputStream(aMessage);
      ByteArrayInputStream messageInputStream = new ByteArrayInputStream(messageBytes);
      BitWalker            bitWalker          = new BitWalker(messageInputStream, BitNumbering.MSB_FIRST);

      System.arraycopy(messageBytes, 0, sizeBytes, 0, 4);
      numOfBitsInMessage = ByteBuffer.wrap(sizeBytes).order(ByteOrder.BIG_ENDIAN).getInt();

      if (capacity < numOfBitsInMessage) {
         String message = "There is not enough bit capacity [" + capacity + "] in this image to hide data ["
                          + numOfBitsInMessage + "]!";

         JpegImageUtils.throwJpegImageExeption(message);
      }

      bitPosition = 0;

      // writeCoefficientsIntoFile("D:/temp/tmp/Diplomska/Stego_Program/coefficients_encode_before.log");
      for (int row = 0; row < imageHeight; row++) {
         for (int column = 0; column < imageWidth; column++) {
            boolean isBlockFirsRow     = (row % 8) == 0;
            boolean isBlockFirstColumn = (column % 8) == 0;
            boolean isDcElem           = isBlockFirsRow && isBlockFirstColumn;
            int     value              = jpegImageContext.getImageValue(row, column);

            if (!isDcElem && (bitPosition < numOfBitsInMessage)) {
               encodeIntoYLuma(bitWalker, row, column, value);
               encodeIntoCbChroma(bitWalker, row, column, value);
               encodeIntoCrChroma(bitWalker, row, column, value);
            }

            if (bitPosition == numOfBitsInMessage) {
               break;
            }

            if (bitPosition == SIZE_LENGTH_IN_BITS) {
               log = false;
            }
         }

         if (bitPosition == numOfBitsInMessage) {
            break;
         }
      }

      if (bitPosition != numOfBitsInMessage) {
         JpegImageUtils.throwJpegImageExeption("Message is to big for the image medium!");
      }

      // writeCoefficientsIntoFile("D:/temp/tmp/Diplomska/Stego_Program/coefficients_encode_after.log");
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public StegoEncoderStatistics getStatistics() {
      return stegoEncoderStatistics;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aBitWalker
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   private void encodeIntoCbChroma(BitWalker aBitWalker, int aRow, int aColumn, int aValue) {
      byte cbChromaValue = JpegImageEncoderUtils.getQuantizedCbChromaValue(aValue);

      if ((cbChromaValue != 0) && (bitPosition < numOfBitsInMessage)) {
         Byte messageBit       = aBitWalker.getBit(bitPosition);
         byte cbChromaNewValue = lsbEncodeValue(cbChromaValue, messageBit.byteValue());

         stegoEncoderStatistics.addTransition(cbChromaValue, cbChromaNewValue);

         if (cbChromaNewValue != 0) {
            bitPosition++;
         }

         if (cbChromaNewValue != cbChromaValue) {
            jpegImageContext.setQuantizedCbChromaValue(aRow, aColumn, cbChromaNewValue);
         }

         // logging(cbChromaValue, cbChromaNewValue);
      }
   }

   /**
    * Method description
    *
    *
    * @param aBitWalker
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   private void encodeIntoCrChroma(BitWalker aBitWalker, int aRow, int aColumn, int aValue) {
      byte crChromaValue = JpegImageEncoderUtils.getQuantizedCrChromaValue(aValue);

      if ((crChromaValue != 0) && (bitPosition < numOfBitsInMessage)) {
         Byte messageBit       = aBitWalker.getBit(bitPosition);
         byte crChromaNewValue = lsbEncodeValue(crChromaValue, messageBit.byteValue());

         stegoEncoderStatistics.addTransition(crChromaValue, crChromaNewValue);

         if (crChromaNewValue != 0) {
            bitPosition++;
         }

         if (crChromaNewValue != crChromaValue) {
            jpegImageContext.setQuantizedCrChromaValue(aRow, aColumn, crChromaNewValue);
         }

         // logging(crChromaValue, crChromaNewValue);
      }
   }

   /**
    * Method description
    *
    *
    *
    * @param aBitWalker
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   private void encodeIntoYLuma(BitWalker aBitWalker, int aRow, int aColumn, int aValue) {
      byte yLumaValue = JpegImageEncoderUtils.getQuantizedYLumaValue(aValue);

      if ((yLumaValue != 0) && (bitPosition < numOfBitsInMessage)) {
         Byte messageBit    = aBitWalker.getBit(bitPosition);
         byte yLumaNewValue = lsbEncodeValue(yLumaValue, messageBit.byteValue());

         stegoEncoderStatistics.addTransition(yLumaValue, yLumaNewValue);

         if (yLumaNewValue != 0) {
            bitPosition++;
         }

         if (yLumaNewValue != yLumaValue) {
            jpegImageContext.setQuantizedYLumaValue(aRow, aColumn, yLumaNewValue);
         }

         // logging(yLumaValue, yLumaNewValue);
      }
   }

   /**
    * Method description
    *
    *
    * @param aStegoEncoderStatistics
    */
   private void initializeEstimatedCapacity(StegoEncoderStatistics aStegoEncoderStatistics) {
      int estimatedCapacity = 0;

      for (Map.Entry<Byte, Long> entry : aStegoEncoderStatistics.getCoefficientValuesStatisticsOrig().entrySet()) {
         if (entry.getKey() != 0) {
            estimatedCapacity += entry.getValue();
         }
      }

      // Estimated size in bytes
      aStegoEncoderStatistics.setEstimatedCapacityInBytes((estimatedCapacity / Byte.SIZE));
   }

   /**
    * Method description
    *
    *
    * @param aOldValue
    * @param aNewValue
    */
   private void logging(int aOldValue, int aNewValue) {
      if ((bitPosition <= 32) && (aNewValue != 0)) {
         System.out.println(aOldValue + ":" + Integer.toBinaryString(aOldValue) + " : " + aNewValue + ":"
                            + Integer.toBinaryString(aNewValue));
      }
   }

   /**
    * Method description
    *
    *
    * @param aValue
    * @param aBit
    *
    * @return
    */
   private byte lsbEncodeValue(byte aValue, byte aBit) {
      byte resultValue = aValue;

      if (aBit == 1) {
         resultValue |= 0x01;
      } else {    // aBit == 0
         resultValue &= 0xFE;
      }

      return resultValue;
   }

   /**
    * Method description
    *
    *
    * @param aMessage
    *
    * @return
    * @throws IOException
    */
   private byte[] prepareMessageInputStream(InputStream aMessage) throws IOException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      int                   messageSize  = SIZE_LENGTH_IN_BYTES;
      byte[]                resultArray  = null;

      // Write initial header. It is 32bit/8 bytes long.
      for (int headerIndex = 0; headerIndex < SIZE_LENGTH_IN_BYTES; headerIndex++) {
         outputStream.write(0);
      }

      // Read bytes from message
      int     byteRead = aMessage.read();
      boolean isEof    = (byteRead == -1);

      while (!isEof) {
         outputStream.write(byteRead);
         messageSize++;
         byteRead = aMessage.read();
         isEof    = (byteRead == -1);
      }

      resultArray = outputStream.toByteArray();

      // encode size of stream
      ByteBuffer sizeByteBuffer = ByteBuffer.allocate(SIZE_LENGTH_IN_BYTES)
                                            .order(ByteOrder.BIG_ENDIAN)
                                            .putInt(messageSize * Byte.SIZE);

      System.arraycopy(sizeByteBuffer.array(), 0, resultArray, 0, SIZE_LENGTH_IN_BYTES);

      return resultArray;
   }

   /**
    * Method description
    *
    *
    * @param aFilename
    * @throws IOException
    */
   private void writeCoefficientsIntoFile(String aFilename) throws IOException {
      int          imageWidth  = jpegImageContext.getImageWidth();
      int          imageHeight = jpegImageContext.getImageHeight();
      StringBuffer array       = new StringBuffer();
      File         file        = new File(aFilename);

      for (int row = 0; row < imageHeight; row++) {
         for (int column = 0; column < imageWidth; column++) {
            int value         = jpegImageContext.getImageValue(row, column);
            int yLumaValue    = JpegImageEncoderUtils.getQuantizedYLumaValue(value);
            int cbChromaValue = JpegImageEncoderUtils.getQuantizedCbChromaValue(value);
            int crChromaValue = JpegImageEncoderUtils.getQuantizedCrChromaValue(value);

            array.append(String.format("%d %d %d ", yLumaValue, cbChromaValue, crChromaValue));
         }

         array.append("\n");
      }

      FileUtils.writeByteArrayToFile(file, array.toString().getBytes());
   }
}
