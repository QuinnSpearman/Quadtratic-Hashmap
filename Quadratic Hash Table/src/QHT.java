public class QHT<K, V> {
  
  public class KVPair<K, V> {
    
    /*
    Generic key-value pair class
    */
  
    K k;
    V v;

    KVPair(K key, V val) {
      k = key;
      v = val;
    }

    public K key() {
      return k;
    }

    public V value() {
      return v;
    }
  }

  /*
    instance variables.
    DO NOT CHANGE, ADD OR REMOVE INSTANCE VARIABLES
  */

  KVPair[] htable;                                  //The Hash table which is an array of KVPairs
  int size;                                         //Number of elements in the hash table
  int initCap;                                      //Initial capacity of the hash table
  static final int DEFAULT_EXP = 2;                 //Default exponent if it's not specified
  final KVPair TOMBSTONE = new KVPair(null, null);  //The Tombstone to be used when deleting an element

  QHT() {
    /*
      ***TO-DO***
      Default constructor
      should initialize the hash table with default capacity
    */
    
    // Set the initial capacity equal to 2 to the default exponent
    initCap = (int)Math.pow(2, DEFAULT_EXP);
    htable = new KVPair[initCap];
  }

  QHT(int exp) {
    /*
      ***TO-DO***
      Single-parameter constructor. The capacity of the hash table
      should be 2^exp. if exp < 2, use default exponent.
      initialize size and initCap accordingly
    */
    
    // If input exponent is less than 2, set exp to the default exponent
    if(exp < 2){
      exp = DEFAULT_EXP;
    }
    
    // Set the initial capacity equal to 2 to exp
    initCap = (int)Math.pow(2, exp);
    htable = new KVPair[initCap];
    
  }

  public int size() {
    /*
      ***TO-DO***
      return the number of elements currently stored in the 
      hash table. Shouldn't include TOMBSTONES
      Should run in O(1)
    */
    return size;
  }

  public int capacity() {
    /*
      ***TO-DO***
      return the capacity of the hash table
      Should run in O(1)
    */
    
    // If the load factor is greater than 0.5, then the capacity of the table is 
    // getting doubled
    if(loadFactor() > 0.5){
      return htable.length * 2;
    }
    // If the load factor is less than 0.25 and the htable size divided by 2 is greater
    // than or equal to the intial capacity, then the capacity of the table is getting 
    // cut in half
    else if(loadFactor() < 0.25 && htable.length / 2 >= initCap){
      return htable.length / 2;
    }
    // otherwise just return the current length of the htable
    return htable.length;
  }
  


  public boolean isEmpty() {
    /*
      ***TO-DO***
      return true if hash table is empty,
      false otherwise
      Should run in O(1)
    */
    
    // If the size is equal to 0, then the table is empty
    if(size == 0){
      return true;
    }
    // Otherwise the table is not empty
    return false;
  }

  public double loadFactor() {
    /*
      ***TO-DO***
      return the load factor of this hash table.
      load factor is the ratio of size to capacity
      Should run in O(1). Note that the return type is double.
    */
    return (double)size() / (double)htable.length;
  }

  private int h(K k) {
    /*
      The hash function. returns an integer for an arbitrary key
      Should run in O(1)
    */
    
    return (k.hashCode() + capacity()) % capacity() ;
  }

  private int p(K k, int i) {
    /*
      The probe function. returns an integer. i is 
      the number of collisions seen so far for the key
      Should run in O(1)
    */
    
    return i/2 + (i*i)/2 + (i%2);
  }

  public void insert(K k, V v) {
    /*
      ***TO-DO***
      should insert the given key and value as a 
      KVPair in the hash table.
      if load factor > 0.5, increase capacity by a factor of 2
    */
    
    //If the size equals the capacity, throw an IllegalStateException
    if(size == capacity()){
      throw new IllegalStateException("IllegalStateException");
    }
    
    
    
    KVPair kvpair = new KVPair(k, v);
    int i = 0;
    int index;
    boolean emptyIndexNotFound = true;
    int hashIndex = h(k);
    
    
    do{
      // Get the index that the key should hash to
      index = (hashIndex + p(k, i++)) % capacity();
      // If the spot in the htable is null, insert the new kvpair
      if(get(index) == null){
        htable[index] = kvpair;
        emptyIndexNotFound = false;
        ++size;
      }      

      // If the spot found is a tombstone, check to make sure the value being inserted isn't
      // a duplicate, if it is, do nothing, if it isn't, insert into the spot in the table
      else if(get(index) == TOMBSTONE){
        if(checkForDuplicate(k, i, hashIndex)){
          return;
        }
        else{
          htable[index] = kvpair;
          emptyIndexNotFound = false;
          ++size;
        }
      }
      // If the kvpair at the index found is the same as the one being inserted, 
      // then do nothing and exit
      else if(get(index).key() == k){
        return;
      }
    }while(emptyIndexNotFound);
    
    // If the load factor is greater than 0.5, double the size of the array
    if(loadFactor() > 0.5){
      resize(htable.length * 2);
    }
  }
  
  
  private void resize(int newCap){
    KVPair temp[] = new KVPair[newCap];
    int index;
    int hashValue; 
    int probeValue;
    int j = 0;
    boolean emptyIndexNotFound = true;
    if(newCap < initCap){
      return;
    }
    
    // For the length of the htable, insert each value into the resized temp table 
    for(int i = 0; i < htable.length; i++){
      //If the slot in the table isn't null
      if(htable[i] != null){
        
        do{
          //If the value found is a tombstone, continue to the next value of the htable
          if(htable[i] == TOMBSTONE){
            break;
          }
          // Find the hashed index and probe value
          hashValue = h((K)htable[i].key()); 
          probeValue = p((K)htable[i].key(), j++);
          index = (hashValue + probeValue) % newCap;

          // If the slot is null, set this slot to the current kvpair
          if(temp[index] == null){
            temp[index] = htable[i];
            emptyIndexNotFound = false;
          }
        }while(emptyIndexNotFound);
      }
      j = 0;
      emptyIndexNotFound = true;
    }
    
    htable = temp;
  }

  public V remove(K k) {
    
    /*
      ***TO-DO***
      if k is found in the hash table, remove KVPair
      and return the value. Otherwise, return null.
      if load factor < 0.25 then reduce capacity in half.
    */
    boolean valueNotFound = true;
    int hashIndex = h(k);
    int probeValue;
    int index;
    V v = null;
    int i = 0;
    
    do{
      // Find the hashed and probed index
      probeValue = p(k, i++);
      index = (hashIndex + probeValue) % capacity();

      // If a null index is reached, do nothing
      if(get(index) == null){
        break;
      }      
      // If the value is found, turn spot into tombstone
      else if(get(index).key() == k){
        v = (V)get(index).value();
        htable[index] = TOMBSTONE;
        --size;
        valueNotFound = false;
      }

    }while(valueNotFound);
    
    // If load factor drops below 0.25, and half the htable is greater than or equal
    // to the initial capacity, resize the array to half its current size
    if(loadFactor() < 0.25 && htable.length / 2 >= initCap){
      resize(htable.length / 2);
    }
    
    return v;
    
  }


  public V find(K k) {
    /*
      ***TO-DO***
      if k is found in the hash table, return the value. 
      Otherwise, return null.
    */
    
    int hashIndex = h(k);
    int probeValue;
    int i = 0;
    boolean valueNotFound = true;
    V v = null;
    int index;
    
    
    do{
      // Get the index
      probeValue = p(k, i++);
      index = (hashIndex + probeValue) % capacity();
      
      // If a null index is reached, do nothing
      if(get(index) == null){
        break;
      }      
      // If the index with the key is found, return the value
      else if(get(index).key() == k){
        v = (V)get(index).value();
        valueNotFound = false;
      }

    }while(valueNotFound);
    
    return v;
    
  }
  
  
  public boolean checkForDuplicate(K k, int collisions, int hashIndex){
    int index;
    boolean nullValueNotFound = true; 
    do{
      // Find the index
      index = (hashIndex + p(k, collisions++)) % capacity();
      
      // If a duplicate is not found, return false
      if(htable[index] == null){
        nullValueNotFound = false;
      }
      // If a duplicate is found, return true
      else if(htable[index].key() == k){
        return true;
      }

    }while(nullValueNotFound);
    
    return false;
  }

  public KVPair get(int i) {
    /*
      return the KVPair at index i of the hash table
    */
    
    if (i >= capacity())
      return null;
    
    return htable[i];
  }
  
  

  public String toString() {
    /*
      return a string representation of the hash table.
    */
    
    String ret = "\n\n";
  
    for (int i = 0; i < capacity(); i++) {
      if (get(i) != null) {
        if (get(i).key() != null) 
          ret += i + "\t" + get(i).key() + "\t->\t" + get(i).value() + "\n";
        else
          ret += i + "\tTOMBSTONE\n";
      }
      else {
        ret += i + "\tnull\n";
      }
    }

    return ret;
  }
}