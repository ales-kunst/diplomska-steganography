package org.diplomska.stego;

import java.io.InputStream;

//~--- interfaces -------------------------------------------------------------

/**
 * This is interface for stego encoding algorithms.
 *
 *
 * @version        1.0, 15/11/15
 * @author         Ales Kunst
 */
public interface StegoEncodingAlgorithm {

   /**
    * Interface method for encoding
    *
    *
    *
    * @param aDataContext
    * @param aMessage
    *
    * @throws Exception
    */
   void encode(DataContext aDataContext, InputStream aMessage) throws Exception;

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   StegoEncoderStatistics getStatistics();
}
