package org.diplomska.jpeg.steps.huffman;

import org.diplomska.jpeg.jfif.struct.HuffmanCode;
import org.diplomska.jpeg.jfif.struct.HuffmanTable;
import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.jpeg.steps.struct.JpegBlock.BlockType;
import org.diplomska.util.BitWalker;
import org.diplomska.util.JpegImageUtils;

import java.io.ByteArrayOutputStream;

//~--- classes ----------------------------------------------------------------

/**
 * Coefficient filler.
 *
 *
 * @version        1.0, 15/11/28
 * @author         Ales Kunst
 */
public abstract class JpegBlockFiller {

   /** Field description */
   JpegBlock jpegBlock;

   /** Field description */
   JpegDecoderContext jpegDecoderContext;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param jpegDecoderContext
    */
   public JpegBlockFiller(JpegDecoderContext jpegDecoderContext) {
      this.jpegDecoderContext = jpegDecoderContext;
      initializeJpegBlock();
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Fill jpeg block with coefficient.
    *
    *
    *
    * @param context
    * @param bitWalker
    * @param huffmanCode
    *
    * @return
    */
   public JpegBlock fill(JpegBlockFillerContext context, BitWalker bitWalker, HuffmanCode huffmanCode) {
      ByteArrayOutputStream coeffBits       = new ByteArrayOutputStream();
      Byte                  bitValue        = null;
      int                   zeroesPreceding = (huffmanCode.code & 0xF0) >>> 4;
      int                   bitLength       = huffmanCode.code & 0x0F;
      boolean               jpegBlockValid  = true;
      JpegBlock             resultJpegBlock = null;

      for (int index = 0; index < bitLength; index++) {
         bitValue = bitWalker.getNextBit();

         if (bitValue == null) {
            jpegBlockValid = false;
         } else {
            coeffBits.write(bitValue);
         }
      }

      if (jpegBlockValid) {
         Long coefficientValue = JpegImageUtils.getNumberFromHuffmanValueEncoding(coeffBits.toByteArray());

         jpegBlock.put(zeroesPreceding, coefficientValue.intValue());

         if (jpegBlock.isEob()) {
            context.setCurrentCoefficientRetriever(getNextJpegBlockFiller(context));
         }

         resultJpegBlock = jpegBlock;
      }

      return resultJpegBlock;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Gets huffman code.
    *
    *
    * @return
    */
   public abstract HuffmanTable getHuffmanTable();

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    */
   void initializeJpegBlock() {
      jpegBlock = new JpegBlock(getJpegBlockType());
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get block type.
    *
    *
    * @return
    */
   protected abstract BlockType getJpegBlockType();

   /**
    * Method description
    *
    *
    *
    * @param context
    * @return
    */
   protected abstract JpegBlockFiller getNextJpegBlockFiller(JpegBlockFillerContext context);
}
