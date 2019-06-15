package org.diplomska.util.structs;

import org.jblas.DoubleMatrix;

//~--- interfaces -------------------------------------------------------------

/**
 * Interface description
 *
 *
 * @version        Enter version here..., 16/04/12
 * @author         Enter your name here...
 */
public interface JpegImageConstants {

   /** Maximal columns in jpeg image block */
   public static final short JPEG_IMAGE_BLOCK_WIDTH = 8;

   /** Maximal rows in jpeg image block */
   public static final short JPEG_IMAGE_BLOCK_HEIGHT = 8;

   /** Luminance quantization matrix. */
   public static final double[][] LUMINANCE_QUANTIZATION_MATRIX_RAW = new double[][] {
      { 16, 11, 10, 16, 24, 40, 51, 61 }, { 12, 12, 14, 19, 26, 58, 60, 55 }, { 14, 13, 16, 24, 40, 57, 69, 56 },
      { 14, 17, 22, 29, 51, 87, 80, 62 }, { 18, 22, 37, 56, 68, 109, 103, 77 }, { 24, 35, 55, 64, 81, 104, 113, 92 },
      { 49, 64, 78, 87, 103, 121, 120, 101 }, { 72, 92, 95, 98, 112, 100, 103, 99 }
   };

   /** Chrominance quantization matrix. */
   public static final double[][] CHROMINANCE_QUANTIZATION_MATRIX_RAW = new double[][] {
      { 17, 18, 24, 47, 99, 99, 99, 99 }, { 18, 21, 26, 66, 99, 99, 99, 99 }, { 24, 26, 56, 99, 99, 99, 99, 99 },
      { 47, 66, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 },
      { 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 }
   };

   /** Field description */
   public static final DoubleMatrix LUMINANCE_QUANTIZATION_MATRIX = new DoubleMatrix(LUMINANCE_QUANTIZATION_MATRIX_RAW);

   /** Field description */
   public static final DoubleMatrix CHROMINANCE_QUANTIZATION_MATRIX =
      new DoubleMatrix(CHROMINANCE_QUANTIZATION_MATRIX_RAW);

   /** Field description */
   public static final int SIZE_LENGTH_IN_BYTES = Integer.BYTES;

   /** Field description */
   public static final int SIZE_LENGTH_IN_BITS = SIZE_LENGTH_IN_BYTES * Byte.SIZE;

   /** Luma color component number */
   public static final int Y_LUMA = 0;

   /** Chroma color component number */
   public static final int CB_CHROMA = 1;

   /** Chroma color component number */
   public static final int CR_CHROMA = 2;
}
