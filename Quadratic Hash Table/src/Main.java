class Main {
  public static void main(String[] args) {
    
    QHT<Integer, Integer> ht = new QHT<Integer, Integer>();
    
    for (int i = 1; i < 30; i+=4) {
      System.out.println("\nInserting " + i + " -> " + i/2);
      ht.insert(i, i/2);
      System.out.println("Capacity: " + ht.capacity() + " Size: " + ht.size() + " Load Factor: " + ht.loadFactor());
      System.out.println(ht);
    }

    for (int i = 1; i < 30; i+=8) {
      System.out.println("\nRemoving " + i);
      ht.remove(i);
      System.out.println("Capacity: " + ht.capacity() + " Size: " + ht.size() + " Load Factor: " + ht.loadFactor());
      System.out.println(ht);
    }


    for (int i = 13; i < 30; i+=4) {
      if (ht.find(i) == null)
        System.out.println("\nDidn't find " + i);
      else
        System.out.println("\nfound " + i + "->" + ht.find(i));
      System.out.println("Capacity: " + ht.capacity() + " Size: " + ht.size() + " Load Factor: " + ht.loadFactor());
      System.out.println(ht);
    }
    
    System.out.println("\nRemoving 29");
    ht.remove(29);
    System.out.println("Capacity: " + ht.capacity() + " Size: " + ht.size() + " Load Factor: " + ht.loadFactor());
    System.out.println(ht);



  }
}