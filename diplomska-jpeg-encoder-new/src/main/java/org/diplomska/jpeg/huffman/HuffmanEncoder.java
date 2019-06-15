package org.diplomska.jpeg.huffman;

import org.diplomska.util.BitWalker;

//~--- interfaces -------------------------------------------------------------

/**
 * Interface description
 *
 *
 * @version        1.0, 15/06/11
 * @author         Ales Kunst
 */
public interface HuffmanEncoder {

   /**
    * Encode byte array.
    *
    *
    * @param aByteArray
    *
    * @return
    */
   HuffmanOutputStructure encode(byte[] aByteArray);

   /**
    * Method description
    *
    *
    * @param aBitWalker
    *
    * @return
    */
   HuffmanOutputStructure encode(BitWalker aBitWalker);
}
