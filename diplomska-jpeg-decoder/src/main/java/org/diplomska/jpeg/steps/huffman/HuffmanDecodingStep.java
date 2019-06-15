package org.diplomska.jpeg.steps.huffman;

import org.diplomska.jpeg.jfif.struct.HuffmanCode;
import org.diplomska.jpeg.jfif.struct.HuffmanTable;
import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.JpegDecoderStep;
import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.util.BitUtils;
import org.diplomska.util.BitWalker;
import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Huffman decoding class.
 *
 *
 * @version        1.0, 15/11/27
 * @author         Ales Kunst
 */
public class HuffmanDecodingStep implements JpegDecoderStep {

   /** Field description */
   List<JpegBlock> jpegBlocks;

   //~--- constructors --------------------------------------------------------

   /**
    * Default constructor.
    *
    */
   public HuffmanDecodingStep() {
      jpegBlocks = new ArrayList<JpegBlock>();
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param jpegDecoderContext
    */
   @Override
   public void execute(JpegDecoderContext jpegDecoderContext) {
      BitWalker              bitWalker              = new BitWalker(jpegDecoderContext.getImageDataAsStream(),
                                                                    BitNumbering.MSB_FIRST, true);
      Byte                   bitValue               = bitWalker.getNextBit();
      ByteArrayOutputStream  bits                   = new ByteArrayOutputStream();
      JpegBlockFillerContext jpegBlockFillerContext = new JpegBlockFillerContext(jpegDecoderContext);
      HuffmanTable           huffmanTable           = jpegBlockFillerContext.getHuffmanTable();

      // long bitIndex = 0;
      // long bitsNumber = jpegDecoderContext.getImageData().length * 8;
      while (bitValue != null) {
         bits.write(bitValue);

         byte[]      reverseBits = BitUtils.reverseBits(bits.toByteArray());
         long        value       = BitUtils.convertBitsToUnsignedNumber(reverseBits, BitNumbering.LSB_FIRST,
                                                                        Type.SHORT);
         HuffmanCode huffmanCode = huffmanTable.getHuffmanCode((int) value, reverseBits.length);

         if (huffmanCode != null) {
            JpegBlock jpegBlock = jpegBlockFillerContext.fill(bitWalker, huffmanCode);

            huffmanTable = jpegBlockFillerContext.getHuffmanTable();

            if ((jpegBlock != null) && (jpegBlock.isEob())) {
               jpegBlocks.add(jpegBlock);
            }

            // Reset bits
            bits = new ByteArrayOutputStream();
         }

         bitValue = bitWalker.getNextBit();
      }

      jpegDecoderContext.addResult(this.getClass(), jpegBlocks);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter.
    *
    *
    * @return
    */
   @Override
   public String getName() {
      return "Huffman decoding";
   }
}
