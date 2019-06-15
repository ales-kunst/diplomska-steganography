package org.diplomska.stego;

import java.io.OutputStream;

//~--- interfaces -------------------------------------------------------------

/**
 * This is interface for stego decoding algorithms.
 *
 *
 * @version        1.0, 15/11/15
 * @author         Ales Kunst
 */
public interface StegoDecodingAlgorithm {

   /**
    * Interface method for decoding
    *
    *
    *
    * @param aDataContext
    * @param aOutputStream
    *
    * @throws Exception
    */
   void decode(DataContext aDataContext, OutputStream aOutputStream) throws Exception;
}
