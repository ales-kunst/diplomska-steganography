package org.diplomska.jpeg.processor.image.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.huffman.HuffmanEncoder;
import org.diplomska.jpeg.huffman.HuffmanOutputStructure;
import org.diplomska.jpeg.processor.image.JpegImageProcessorStep;
import org.diplomska.jpeg.util.JpegImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 02.jul. 2015
 * @author         Ales Kunst
 */
public class HuffmanCodingStep implements JpegImageProcessorStep {

   /** Field description */
   private HuffmanEncoder fHuffmanEncoder;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private HuffmanCodingStep() {}

   /**
    * Constructs ...
    *
    *
    * @param aHuffmanEncoder
    */
   public HuffmanCodingStep(HuffmanEncoder aHuffmanEncoder) {
      this();
      this.fHuffmanEncoder = aHuffmanEncoder;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Execution method.
    *
    *
    * @param aJpegImageContext
    */
   @Override
   public void execute(JpegImageContext aJpegImageContext) {
      byte[]                 bitsArray              = getBitsArray(aJpegImageContext);
      HuffmanOutputStructure huffmanOutputStructure = getHuffmanEncoder().encode(bitsArray);

      aJpegImageContext.setDcLumaHuffmanTable(huffmanOutputStructure.getLumaDcHuffmanElements());
      aJpegImageContext.setAcLumaHuffmanTable(huffmanOutputStructure.getLumaAcHuffmanElements());
      aJpegImageContext.setDcChromaHuffmanTable(huffmanOutputStructure.getChromaDcHuffmanElements());
      aJpegImageContext.setAcChromaHuffmanTable(huffmanOutputStructure.getChromaAcHuffmanElements());
      aJpegImageContext.setHuffmanEncodedImageBitsArray(huffmanOutputStructure.getHuffmanEncodedBitsArray());
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter method.
    *
    *
    * @return
    */
   public HuffmanEncoder getHuffmanEncoder() {
      return fHuffmanEncoder;
   }

   /**
    * Get bits array of all jpeg image blocks zig zag arrays.
    *
    *
    * @param aJpegImageContext
    * @return
    */
   protected byte[] getBitsArray(JpegImageContext aJpegImageContext) {
      int                   rows       = aJpegImageContext.getJpegImageBlocks().length;
      int                   columns    = aJpegImageContext.getJpegImageBlocks()[0].length;
      ByteArrayOutputStream bitsStream = new ByteArrayOutputStream();

      for (int row = 0; row < rows; row++) {
         for (int column = 0; column < columns; column++) {
            JpegImageBlock currentJpegImageBlock = aJpegImageContext.getJpegImageBlocks()[row][column];

            try {
               bitsStream.write(currentJpegImageBlock.getCombinedZigZagArrayAsBits());

               // currentJpegImageBlock.clearZigZagArrays();
            } catch (IOException e) {
               JpegImageUtils.throwJpegImageEncoderExeption(e.getMessage());
            }
         }
      }

      return bitsStream.toByteArray();
   }
}
