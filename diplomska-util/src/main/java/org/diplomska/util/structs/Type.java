package org.diplomska.util.structs;

/**
 * Enum for type of value
 *
 */
public enum Type {
   BYTE(8), SHORT(16), INT(32), LONG(64);

   /** Field description */
   private final int typeSize;

   //~--- constructors --------------------------------------------------------

   /**
    * Private constructor.
    *
    *
    * @param typeSize
    */
   Type(int typeSize) {
      this.typeSize = typeSize;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get type size.
    *
    *
    * @return
    */
   public int getTypeSize() {
      return typeSize;
   }
}
