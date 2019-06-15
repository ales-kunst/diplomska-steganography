package org.diplomska.jpeg.jfif.struct;

/**
 * Huffman code.
 *
 *
 * @version        1.0, 25.nov.2015
 * @author         Ales Kunst
 */
public class HuffmanCode {

   /** Huffman code */
   public final short code;

   /** Field description */
   public final int huffmanTreeCode;

   /** Codelength of this code */
   public final int codeLength;

   //~--- constructors --------------------------------------------------------

   /**
    * Default constructor.
    *
    *
    * @param code
    * @param huffmanTreeCode
    * @param codeLength
    */
   public HuffmanCode(short code, int huffmanTreeCode, int codeLength) {
      this.code            = code;
      this.huffmanTreeCode = huffmanTreeCode;
      this.codeLength      = codeLength;
   }
}
