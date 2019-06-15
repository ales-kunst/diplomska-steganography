package org.diplomska.jpeg.jfif;

import org.diplomska.util.JpegImageUtils;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/18
 * @author         Ales Kunst
 */
public class JfifQuntizationTableSegment extends JfifSegment {

   /** Field description */
   int[][] quantizationMatrix;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param marker
    */
   public JfifQuntizationTableSegment(byte[] marker) {
      super(marker);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get quantization table.
    *
    *
    * @return
    */
   public int[][] getQuantizationMatrix() {
      if (quantizationMatrix == null) {
         int index = 0;

         quantizationMatrix = new int[JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT][JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH];

         int    arraySize        = JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT * JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH;
         byte[] quantizationData = new byte[arraySize];

         System.arraycopy(getPureSegmentContents(), 1, quantizationData, 0, arraySize);

         for (long coordinate : JpegImageUtils.ZIG_ZAG_COORDINATES) {
            int row    = JpegImageUtils.getRow(coordinate);
            int column = JpegImageUtils.getColumn(coordinate);

            quantizationMatrix[row][column] = quantizationData[index];
            index++;
         }
      }

      return quantizationMatrix;
   }

   /**
    * Is quantization for chrominance.
    *
    *
    * @return
    */
   public boolean isForChrominance() {

      // Third byte is information which quntzization table it is.
      return (getPureSegmentContents()[0] == 1);
   }

   /**
    * Is quantization for luminance.
    *
    *
    * @return
    */
   public boolean isForLuminance() {

      // Third byte is information which quntzization table it is.
      return (getPureSegmentContents()[0] == 0);
   }
}
