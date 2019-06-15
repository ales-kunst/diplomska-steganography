package org.diplomska.util;

import org.diplomska.util.structs.BitNumbering;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

//~--- classes ----------------------------------------------------------------

/**
 * BitWalker class has methods for utilizing
 *
 *
 * @version        1.0, 18.nov.2015
 * @author         Ales Kunst
 */
public class BitWalker {

   /** Start with Msb */
   public static final int MSB_FIRST = 0;

   /** Start with Lsb */
   public static final int LSB_FIRST = 1;

   /** Field description */
   private static final int DEFAULT_ALLOCATE_AHEAD_BUFFER_SIZE = 100;

   //~--- fields --------------------------------------------------------------

   /** Buffered already read content */
   byte[] bufferedContent;

   /** Field description */
   int bufferedContentLength;

   /** Field description */
   int allocateAhedBufferSize;

   /** Input stream of bytes */
   InputStream inputStream;

   /** Which direction should be used MSB_FIRST or LSB_FIRST */
   BitNumbering bitNumbering;

   /** Bit position for next bit method */
   long internalBitPosition;

   /** Field description */
   boolean readFullIntoBuffer;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private BitWalker() {
      bufferedContent       = null;
      bufferedContentLength = -1;
      resetPosition();
   }

   /**
    * Constructor.
    *
    *
    * @param aInputStream
    * @param aBitNumbering
    */
   public BitWalker(InputStream aInputStream, BitNumbering aBitNumbering) {
      this(aInputStream, aBitNumbering, false, DEFAULT_ALLOCATE_AHEAD_BUFFER_SIZE);
   }

   /**
    * Constructs ...
    *
    *
    * @param aInputStream
    * @param aBitNumbering
    * @param aReadFull
    */
   public BitWalker(InputStream aInputStream, BitNumbering aBitNumbering, boolean aReadFull) {
      this(aInputStream, aBitNumbering, aReadFull, DEFAULT_ALLOCATE_AHEAD_BUFFER_SIZE);
   }

   /**
    * Constructs ...
    *
    *
    * @param aInputStream
    * @param aBitNumbering
    * @param aReadFull
    * @param aAllocateAhedBufferSize
    */
   public BitWalker(InputStream aInputStream, BitNumbering aBitNumbering, boolean aReadFull,
                    int aAllocateAhedBufferSize) {
      this();
      this.inputStream            = aInputStream;
      this.bitNumbering           = aBitNumbering;
      this.readFullIntoBuffer     = aReadFull;
      this.allocateAhedBufferSize = aAllocateAhedBufferSize;

      if (aReadFull) {
         readFullIntoBuffer();
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aBitPosition bit position. It starts with 0..(n-1)
    *
    * @return
    */
   public Byte getBit(long aBitPosition) {
      Byte returnBit = null;

      try {
         int  byteIndex          = -1;
         long bitPositionFromOne = aBitPosition + 1;

         if (readFullIntoBuffer) {
            if (bitPositionFromOne == 0) {
               byteIndex = 1;
            } else if ((bufferedContentLength * 8) >= bitPositionFromOne) {
               byteIndex = (int) (bitPositionFromOne / 8);

               // Add one byte if modulus is not equal zero. Bits are in the next byte
               byteIndex += ((bitPositionFromOne % 8 == 0)
                             ? 0
                             : 1);
            }
         } else {
            byteIndex = readUntilBitIsAchieved(aBitPosition + 1);
         }

         if (byteIndex != -1) {
            returnBit = new Byte((byte) 0);

            // byte byteValue       = alreadyReadContent.toByteArray()[byteIndex - 1];
            byte byteValue = bufferedContent[byteIndex - 1];

            // int  bitIndexInByte  = (int) Math.abs((byteIndex * 8) - (bitPosition + 1));
            int bitIndexInByte = (int) ((byteIndex * 8) - bitPositionFromOne);

            // int  rightShiftIndex = getRightBitShiftIndex(bitIndexInByte);
            if (bitNumbering != BitNumbering.MSB_FIRST) {
               bitIndexInByte = 7 - bitIndexInByte;
            }

            returnBit = (byte) ((byteValue >>> (bitIndexInByte)) & 1);
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      return returnBit;
   }

   /**
    * Method description
    *
    *
    * @param aStartBitPosition
    * @param aLength
    *
    * @return
    */
   public byte[] getBits(long aStartBitPosition, int aLength) {
      byte[]                returnBits          = null;
      ByteArrayOutputStream returnBitsOutStream = new ByteArrayOutputStream();
      long                  endBitPosition      = aStartBitPosition + aLength;

      for (long bitPosition = aStartBitPosition; bitPosition < endBitPosition; bitPosition++) {
         Byte bit = getBit(bitPosition);

         if (bit == null) {
            break;
         } else {
            returnBitsOutStream.write(bit);
         }
      }

      returnBits = (returnBitsOutStream.size() != 0)
                   ? returnBitsOutStream.toByteArray()
                   : null;

      return returnBits;
   }

   /**
    * Gets next bit in the stream.
    *
    *
    * @return
    */
   public Byte getNextBit() {
      Byte returnBit = getBit(internalBitPosition);

      if (returnBit != null) {
         internalBitPosition++;
      }

      return returnBit;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Reset internal position.
    *
    */
   public void resetPosition() {
      internalBitPosition = 0;
   }

   /**
    * Method description
    *
    *
    * @param aValue
    */
   protected void addByteToAlreadyReadContent(int aValue) {

      // alreadyReadContent.write(value);
      if (bufferedContent == null) {
         bufferedContent       = new byte[allocateAhedBufferSize];
         bufferedContent[0]    = (byte) aValue;
         bufferedContentLength = 1;
      } else if (bufferedContentLength < bufferedContent.length) {
         bufferedContent[bufferedContentLength] = (byte) aValue;
         bufferedContentLength++;
      } else {
         byte[] workingBufferedContent = new byte[bufferedContent.length + allocateAhedBufferSize];

         System.arraycopy(bufferedContent, 0, workingBufferedContent, 0, bufferedContent.length);
         workingBufferedContent[bufferedContent.length] = (byte) aValue;
         bufferedContentLength                          = bufferedContent.length + 1;
         bufferedContent                                = workingBufferedContent;
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aBitIndex This index should be between
    *
    * @return
    */
   protected int getRightBitShiftIndex(int aBitIndex) {
      int returnShiftIndex = 0;

      if ((aBitIndex < 0) || (aBitIndex > 7)) {
         throw new RuntimeException("Index should be between 0 and 7!");
      }

      if (bitNumbering == BitNumbering.MSB_FIRST) {
         returnShiftIndex = aBitIndex;
      } else {
         returnShiftIndex = 7 - aBitIndex;
      }

      return returnShiftIndex;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method reads bytes if needed which contains bitIndex or -1 if the bitindex is beyond the length of input stream.
    *
    *
    * @param aBitIndex
    *
    * @return Returns byte index where bit is located. In case the bitindex is to large -1 is returned.
    *
    * @throws IOException
    */
   protected int readUntilBitIsAchieved(long aBitIndex) throws IOException {
      int returnByteNumber = -1;

      // long currentBytePosition = alreadyReadContent.size();
      long currentBytePosition = (bufferedContent != null)
                                 ? bufferedContentLength
                                 : 0;
      long currentNumberOfBits = currentBytePosition * 8;
      int  byteValue           = -1;

      if (currentNumberOfBits < aBitIndex) {
         byteValue = inputStream.read();
      }

      while ((byteValue != -1) && (currentNumberOfBits <= aBitIndex)) {
         addByteToAlreadyReadContent(byteValue);
         currentBytePosition++;
         currentNumberOfBits = currentBytePosition * 8;

         if (currentNumberOfBits < aBitIndex) {
            byteValue = inputStream.read();
         }
      }

      if (aBitIndex == 0) {
         returnByteNumber = 1;
      } else if (currentNumberOfBits >= aBitIndex) {
         returnByteNumber = (int) (aBitIndex / 8);

         // Add one byte if modulus is not equal zero. Bits are in the next byte
         returnByteNumber += ((aBitIndex % 8 == 0)
                              ? 0
                              : 1);
      }

      return returnByteNumber;
   }

   /**
    * Read fully into buffer.
    *
    */
   private void readFullIntoBuffer() {
      int byteValue;

      try {
         byteValue = inputStream.read();

         ByteArrayOutputStream outStream = new ByteArrayOutputStream();

         while (byteValue != -1) {
            outStream.write(byteValue);
            byteValue = inputStream.read();
         }

         bufferedContent       = outStream.toByteArray();
         bufferedContentLength = bufferedContent.length;
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      }
   }
}
