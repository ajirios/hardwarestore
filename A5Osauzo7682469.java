
/*******************************************************************
*
* Name of program: A5Osauzo7682469
*
* COMP 2140        SECTION A01
* INSTRUCTOR       Helen Cameron
* ASSIGNMENT       Assignment 5
* @author          AJIRI OSAUZO JEFFREY, 7682469
* @version date    April 7, 2016
*
* PURPOSE:         This program implements a table ADT to store and process inventory
*                  using hashing and separate chaining techniques.
*
*********************************************************************/

import java.io.*;
import java.util.Scanner;


public class A5Osauzo7682469
  
{


  /*******************************************************************
  *
  * Method main
  *
  * PURPOSE: To test the Table (implemented as a BST) by 
  *          running a store-inventory application.
  *
  ********************************************************************/
  public static void main( String[] args )
  {
    System.out.println( "\n\nComp 2140 Assignment 5 Winter 2016" 
                        + "\n----------------------------------\n" );

    handleInventorySimulation();

    System.out.println( "\n\n----------------------------------\n"
                        + "\nProgram ended normally.\n" );
  } // end main



  /*******************************************************************
  *
  * Method handleInventorySimulation
  *
  * PURPOSE: To test the BST and TwoThreeTree classes (used as tables) by 
  *   running a store-inventory application:
  *   - read in the name of a file (keyboard input) and open it
  *   - read in all information about the store and its products
  *     (setting up the two tables)
  *   - the read in and execute the inventory commands
  *   - print out the two tables at the end.
  *
  ********************************************************************/
  private static void handleInventorySimulation()
  {
    Scanner keyboard; // to read in the name of the input file
    String fileName; // the name of the input file typed in by the user
    Scanner inFile; // scanner to read from user's selected file. 
    String storeName; // name of the store whose inventory we are handling
    int numProducts;  // number of products in the current inventory
    String inputLine; // to read in each line of the input file (after the first line)
    ProductRecord prod;
    TwoThreeTree products = new TwoThreeTree( );
//    BST products = new BST( );
    BST categories = new BST( );
    String[] tokens;

    // Allow user to choose file with keyboard input

    keyboard = new Scanner( System.in );
    System.out.println( "Enter the input file name (.txt files only): " );
    fileName = keyboard.nextLine();
    System.out.println( "" );
    try
    {
      // inFile is now set to read from the file chosen by the user.
      inFile = new Scanner( new File( fileName ) );

      // First, read in the store's name
      storeName = inFile.nextLine();

      // Second, read number of products to be stored in the two tables
      numProducts = inFile.nextInt();
      inFile.nextLine(); // ignore any remaining characters on this line.

      // Read in the current inventory and create the two tables

      for ( int i = 0; i < numProducts; i++ )
      {
        inputLine = inFile.nextLine().trim();
        prod = parseProductLine( inputLine );
        products.insert( prod.getProductCode(), prod );
        categories.insert( prod.getCategory(), prod );
      }

      // Read in and handle the inventory commands

      while ( inFile.hasNextLine() )
      {
        inputLine = inFile.nextLine().trim();
        if ( !inputLine.equals( "" ) )
        {
          System.out.println( "\nCommand: " + inputLine );
          if ( inputLine.charAt(0) == 'L' )
          {
            if ( inputLine.charAt(1) == 'C' )
            {
              tokens = inputLine.split( "\"" );
              categories.printKeyMatches( tokens[1].trim() );
            }
            else if ( inputLine.charAt(1) == 'P' )
            {
              tokens = inputLine.split( "\\s+" );
              prod = products.find( tokens[1].trim() );
              if ( prod != null )
              {
                System.out.println( "Product found for product code = " + tokens[1].trim() + ":" );
                System.out.println( prod );
              }
            }
            else
              System.out.println( "Command code invalid on input line: " + inputLine );
          }
          else if ( inputLine.charAt(0) == 'S' )
            handleSale( inputLine, products );
          else if ( inputLine.charAt(0) == 'R' )
            handleReceiving( inputLine, products );
          else
            System.out.println( "Command code invalid on input line: " + inputLine );
        }
      }

      // Print out the two tables at the end

      System.out.println( "*****************************\n" + 
     storeName + " --- End-of-Day Inventory Report" );

      System.out.println( "\n\nTable with key = product code" );
      System.out.println(     "-----------------------------" );
      products.printTable();

      System.out.println( "\n\nTable with key = category" );
      System.out.println(     "-------------------------" );
      categories.printTable();
    }
    catch( FileNotFoundException e )
    {
      System.out.println(e.getMessage());
    }

  } // end handleInventorySimulation


  /*******************************************************************
  *
  * Method parseProductLine
  *
  * PURPOSE: Parse one line containing all information about one
  *          product (this information is part of the store inventory
  *          that is read in at the start of the day).
  *
  * @param inputLine  The line containing info about one product
  *
  * @return A product record containing the info from the input line
  *
  ********************************************************************/
  private static ProductRecord parseProductLine( String inputLine )
  {
    ProductRecord pr = null;
    String[] tokens;
    String prodCode;
    String cat;
    String desc;
    int aisle, ci;

    if ( inputLine != null && !inputLine.equals( "" ) )
    {
      tokens = inputLine.split( "\"" );
      cat = tokens[1].trim();
      desc = tokens[3].trim();
      prodCode = tokens[0].substring( 0, tokens[0].length()-1 );
      tokens = ((tokens[4]).trim()).split( "\\s+" ); 
      aisle = Integer.parseInt( tokens[0] );
      ci = Integer.parseInt( tokens[1] );
      pr = new ProductRecord( prodCode, cat, desc, aisle, ci );
    }
    return pr;
  } // end parseProductLine


  /*******************************************************************
  *
  * Method handleSale
  *
  * PURPOSE: To handle one sale command (parse the input line
  *          containing the sale, figure out if there are enough
  *          units of that product to make the sale, and decrement
  *          the inventory of that product if there is (and print
  *          an error message if there isn't).  
  *          If the inventory becomes 0 as a result of the sale,
  *          print out a message prompting the employee to reorder
  *          the product.
  *
  * @param inputLine  The line containing info about one sale
  *
  * @param products  The table that uses the product code as the key.
  *
  ********************************************************************/
  private static void handleSale( String inputLine, TwoThreeTree products )
  {
    String[] tokens;
    int sold; // how many of the product were sold?
    ProductRecord pr;

    if ( inputLine != null && !inputLine.equals( "" ) )
    {
      tokens = inputLine.split( "\\s+" );
      pr = products.find( tokens[1] );
      if ( pr != null )
      {
        sold = Integer.parseInt( tokens[2].trim() );
        if ( pr.getInventory() < sold )
        {
          System.out.println( "ERROR: Insufficient product "
            + tokens[1] + ". Only " + pr.getInventory() 
            + " on hand, but " + sold + " requested.\n"
            + "  Input line " + inputLine );
        }
        else
        {
          pr.setInventory( pr.getInventory() - sold );
          if ( pr.getInventory() == 0 )
            System.out.println( "Reorder product " + tokens[1] + " (the last one just sold)." );
        }
      }
      else
      {
        System.out.println( "ERROR: No such product " + tokens[1] + " on line " + inputLine );
      }
    }
  } // end handleSale


  /*******************************************************************
  *
  * Method handleReceiving
  *
  * PURPOSE: To handle one receive command (parse the input line
  *          containing the received product, figure out if the
  *          product exists, and add the received number of units to
  *          the inventory of that product if there is (and print
  *          an error message if there isn't).  
  *
  * @param inputLine  The line containing one receive command
  *
  * @param products  The table that uses the product code as the key.
  *
  ********************************************************************/
  private static void handleReceiving( String inputLine, TwoThreeTree products )
  {
    String[] tokens;
    int received; // how many of the product were received?
    ProductRecord pr;

    if ( inputLine != null && !inputLine.equals( "" ) )
    {
      tokens = inputLine.split( "\\s+" );
      pr = products.find( tokens[1].trim() );
      if ( pr != null )
      {
        received = Integer.parseInt( tokens[2].trim() );
        pr.setInventory( pr.getInventory() + received );
      }
      else
      {
        System.out.println( "ERROR: No such product " + tokens[1] + " on line " + inputLine );
      }
    }
  } // end handleReceived


} // A4SolutionWinter2016

/*******************************************************************
********************************************************************
*   CLASS ProductRecord --- contains all info about one product
********************************************************************
*******************************************************************/

class ProductRecord
{
  private String productCode;
  private String category;
  private String description;
  private int aisle;
  private int currentInventory;

  public ProductRecord( String pc, String cat, String desc, int a, int ci )
  {
    productCode = pc;
    category = cat;
    description = desc;
    aisle = a;
    currentInventory = ci;
  }

  public String getProductCode()
  {
    return productCode;
  }

  public String getCategory()
  {
    return category;
  }

  public String getDescription()
  {
    return description;
  }

  public int getAisle()
  {
    return aisle;
  }

  public int getInventory()
  {
    return currentInventory;
  }

  public void setInventory( int newInventory )
  {
    currentInventory = newInventory;
  }

  public String toString()
  {
    return "Code: " + productCode + " (" + category + ") " + description
            + "; Aisle " + aisle + " (have " + currentInventory + ")";
  }

  public boolean equals( ProductRecord other )
  {
    return this.productCode.equals( other.productCode );
  }

} // end class ProductRecord


/*******************************************************************
********************************************************************
*   CLASS BST
*
*   ADT Table implemented as a BST.
********************************************************************
*******************************************************************/
class BST
{


  /*******************************************************************
  ********************************************************************
  *   CLASS BSTNode
  *   - each node contains a key (a String) and the product record
  *     associated with that key.
  ********************************************************************
  *******************************************************************/
  private class BSTNode
  {
    String key;
    public ProductRecord item;
    public BSTNode left, right;

    public BSTNode( String k, ProductRecord i )
    {
      key = k;
      item = i;
      left = right = null;
    }

    public void insertBelow( String newKey, ProductRecord p )
    { 
      if ( newKey.compareTo( key ) < 0 )
      {
        if ( left == null )
          left = new BSTNode( newKey, p );
        else
          left.insertBelow( newKey, p );
      }
      else
      {
        if ( right == null )
          right = new BSTNode( newKey, p );
        else
          right.insertBelow( newKey, p );
      }
    } // end insertBelow

    public ProductRecord search( String searchKey )
    {
      ProductRecord result = null;

      if ( searchKey.equals( key ) )
        result = this.item;
      else if ( searchKey.compareTo( key ) < 0 )
      {
        if ( left != null )
          result = left.search( searchKey );
      }
      else // key < searchKey
      {
        if ( right != null )
          result = right.search( searchKey );
      }

      return result;
    } // end search

    public void printAllMatches( String searchKey )
    {
  
     if (key == searchKey)
       
     {
       
       System.out.println(item);
       
     }
     
     else
       
     {
       if ((searchKey.compareTo(key)) < 0)
         
       {
         if (left != null)
           
         {
           
           left.printAllMatches(searchKey);
           
         }
         
       }
       
       else
         
       {
         if (right != null)
           
         {
           
           right.printAllMatches(searchKey);
           
         }
         
       }
       
     }
    
    } // end printAllMatches

    public void printInorder( )
      
    {
      
      if (left != null)
   
      {
        left.printInorder();
      }
  
      System.out.println(item);
  
      if (right != null)
   
      {
        right.printInorder();
      }
      
    } // end printInorder
    
  } // end class BSTNode

/*******************************************************************
*   Back to class BST
*******************************************************************/

  BSTNode root;  // A pointer to the root node (or null if the tree is empty)

  public BST(  )
  {
    root = null;
  }


  /*******************************************************************
  *
  * Method insert
  *
  * PURPOSE: Insert ProductRecord p with key newKey into the BST
  *
  * @param newKey  The key (a String) associated with the new product
  *
  * @param p The product record associated with the new product
  *
  ********************************************************************/
  public void insert( String newKey, ProductRecord p )
  { 
    if ( root == null )
      root = new BSTNode( newKey, p );
    else
      root.insertBelow( newKey, p );
  } // insert


  /*******************************************************************
  *
  * Method find
  *
  * PURPOSE: searches for and returns the product record associated with searchKey
  *
  * @param searchKey  The key (a String) associated with the product record we want.
  *
  * @return The product record associated with searchKey
  *
  ********************************************************************/
  public ProductRecord find( String searchKey )
  {
    ProductRecord result = null;

    if ( root != null )
    {
      result = root.search( searchKey );
    }

    return result;
  } // end find


  /*******************************************************************
  *
  * Method printKeyMatches
  *
  * PURPOSE: Print all ProductRecords p with key searchKey in the table:
  *          - Perform a search for searchKey
  *          - If searchKey is found, print out the associated product record
  *            AND continue the search for searchKey in the right child
  *            (items with equal keys are stored only in the right child)
  *          - The searching continues until we "fall off the tree"
  *            --- that is, until we want to move to a child that is null.
  *          - Assumes: (potentially) multiple records with the same key
  *
  * @param searchKey  The key (a String) that we want all records for
  *
  ********************************************************************/
  public void printKeyMatches( String searchKey )
  {
    
    if (root == null)
      
     {
       
      System.out.println("No matches found for search key = " + searchKey);
      
     }
    
     else
      
     {
       
      root.printAllMatches(searchKey);
      
     }
    
  }



  /*******************************************************************
  *
  * Method printTable
  *
  * PURPOSE: Traverses the table, printing out all the keys and their
  *          associated product records
  *
  ********************************************************************/
  public void printTable()
  {
    
    if (root == null)
      
     {
      
      System.out.println("Table is empty.");
      
     }
     
     else
      
     {
       
      root.printInorder();
      
     }
     
    
  }

} // end class BST



/**********************************************************************************
***********************************************************************************
*
*  TwoThreeTree class
*
*  A leaf-based 2-3 tree:
*    - all data is stored in the leaves
*    - interior nodes contain only index values to guide searches
*
***********************************************************************************
***********************************************************************************/

class TwoThreeTree
{

  /**************************************************************
  **************************************************************
  * Nodes for the TwoThreeTree
  **************************************************************
  **************************************************************/

  private class TwoThreeNode
  {

    public String[] key;
    public ProductRecord data;
    public TwoThreeNode[] child;
    public int numKeys;
    public TwoThreeNode parent;

    // Create a new leaf for data d with key k. The leaf should have parent p.
    public TwoThreeNode( String k, ProductRecord d, TwoThreeNode p )
    {
      key = new String[1]; // A leaf holds only ONE key, with its associated data.
      key[0] = k; // The key of a data item!
      data = d; // The data item associated with this key.
      numKeys = 1;
      child = null; // A leaf will _never_ have children.
      parent = p;
    }

    // Create a new interior Node to contain index key k with parent p
    // and two children l and r.
    public TwoThreeNode( String k, TwoThreeNode p, 
                         TwoThreeNode l, TwoThreeNode r )
    {
      key = new String[2]; // May later hold 2 index values.
      key[0] = k; // The index value.
      key[1] = null;
      data = null; // Interior nodes never contain real data (only index keys to guide the search).
      numKeys = 1;
      child = new TwoThreeNode[3]; // May later have 3 children.
      child[0] = l;
      child[1] = r;
      child[2] = null;
      parent = p;
    }

    /************************************************************
    *
    *  printInorder
    *    Do an inorder traversal of the subtree rooted at
    *    the calling TwoThreeNode, printing the data values 
    *    (i.e., only the data stored in the leaves)
    *
    **************************************************************/
    public void printInorder()
      
    {
        
        if (this.isLeaf())
            
        {
            
            System.out.println(data);
            
        }
        
        else
            
        {
            
            //recursively traverse the interior nodes till leaf is found.
            child[0].printInorder();
            
            
            if (numKeys == 2)
                
            {
                
                //the current node has three children
                child[2].printInorder();
                
            }
            
            //recursively traverse the rightmost child
            child[1].printInorder();
            
        }
        
        
        
    } // end printInorder
      
      

    /************************************************************
    *
    * correctChild
    *
    * Figure out which child to move to in the search for searchKey.
    * Return a pointer to that child.
    *
    *  Idea:
    *  - i is the index of the child we think we should move to
    *  - start by assuming we should move to the rightmost child
    *  - loop: if searchKey is less than the index value separating 
    *    the current child from the child immediately to the left of it
    *    move i to the child immediately to the left
    *
    **************************************************************/
    public TwoThreeNode correctChild( String searchKey )
      
    {
        TwoThreeNode correctChild;
        
        correctChild = null;
        
        
        if (this.key[numKeys] == null)
            
        {
            //one index value: pass the lesser
            
            if ((searchKey.compareTo(key[0])) < 0)
                
            {
                //declare the left as correctChild
                correctChild = child[0];
                System.out.println("Correct Child for 2 Node = Left Child " + correctChild);
                
            }
            
            else if ((searchKey.compareTo(key[0])) >= 0)
                
            {
                //declare the right child as correctChild
                correctChild = child[1];
                System.out.println("Correct Child for 2 Node = Right Child " + correctChild);
                
            }
            
            
        }
        
        else if (this.key[numKeys] != null)
            
        {
            //two index values:
            if ((searchKey.compareTo(key[0])) < 0)
                
            {
                
                correctChild = child[0];
                System.out.println("Correct Child for 3 Node = Left Child " + correctChild);
                
            }
            
            else if ((searchKey.compareTo(key[0])) >= 0)
                
            {
                
                if ((searchKey.compareTo(key[1])) < 0)
                    
                {
                    
                    correctChild = child[1];
                    System.out.println("Correct Child for 3 Node = Middle Child " + correctChild);
                    
                }
                
                else if ((searchKey.compareTo(key[1])) >= 0)
                    
                {
                    
                    correctChild = child[2];
                    System.out.println("Correct Child for 3 Node = Right Child " + correctChild);
                    
                }
                
            }
            
            
        }
        
        
        return correctChild;
        
    }

    /************************************************************
    *
    *  isLeaf
    *    Return true if the TwoThreeNode is a leaf; false
    *    otherwise.
    *
    *    Note: A TwoThreeNode is a leaf if it has no children
    *    and if it has no children, then child is null.
    *
    **************************************************************/
    public boolean isLeaf()
      
    {
        
      return (child == null);
        
    }


  } // end class TwoThreeNode
    
    

/***************************************************************
****************************************************************
* Returning to class TwoThreeTree
****************************************************************
***************************************************************/

  private TwoThreeNode root;


  /************************************************************
  *
  * TwoThreeTree constructor
  *
  * Create an empty tree
  *
  **************************************************************/
  public TwoThreeTree()
    
  {
      
    root = null;
      
  }


  /************************************************************
  *
  * findLeaf
  *
  * Return the leaf where searchKey should be 
  * (if it is in the tree at all).
  *
  * (A private helper method for search and insert.
  *
  **************************************************************/
  private TwoThreeNode findLeaf( String searchKey )
    
  {
      TwoThreeNode foundLeaf;
      TwoThreeNode currentNode;
      
      foundLeaf = null;
      currentNode = root;
      
      while (!currentNode.isLeaf())
          
      {
          
          currentNode = currentNode.correctChild(searchKey);
          //System.out.println("Found leaf: " + currentNode.data);
          
      }
      
      foundLeaf = currentNode;
      
      return foundLeaf;
      
  }


  /************************************************************
  *
  * find
  *    Find and return the ProductRecord stored with key
  *    searchKey (or return null if searchKey is not in
  *    any leaf in the tree).
  *
  **************************************************************/

  public ProductRecord find( String searchKey )
    
  {
      ProductRecord record;
      TwoThreeNode foundLeaf;
      
      record = null;
      foundLeaf = null;
      
      if (root != null)
          
      {
          
          foundLeaf = findLeaf(searchKey);
          record = foundLeaf.data;
          
      }
      
      return record;
      
  }


  /************************************************************
  *
  * insert
  *    Insert ProductRecord p with key newKey into the tree.
  *     - First, search for newKey all the way to the leaves.
  *     - If the leaf contains newKey, simply return.
  *     - Otherwise, call recursive method addNewLeaf to handle
  *       the insertion (including any splitting and
  *       pushing up required).
  *
  **************************************************************/

  public void insert( String newKey, ProductRecord p  )
  {
    TwoThreeNode curr;
    TwoThreeNode nextCurr;
    boolean found = false;
    int i;

    if ( root == null )
    {
      // Empty tree: Add first node as the root (it has no parent)

      root = new TwoThreeNode( newKey, p, null );
    }
    else
    {
      // Tree is not empty.
      // Find the leaf that would contain newKey if newKey is already in the tree.

      curr = findLeaf( newKey );

      if ( curr != null && !curr.key[0].equals( newKey ) ) 
      {
        // The leaf at which the search ended does not contain searchKey.
        // Insert!

        addNewLeaf( newKey, p, curr );
      }
      else if ( curr == null )
      {
        System.out.println( "Not inserting " + newKey 
          + ": search failed with curr == null in non-empty tree" );
      }
      

    } // end else root != null
      
  } // end insert



  /************************************************************
  *
  * addNewLeaf
  *    Add a new leaf containing newKey and ProductRecord p into the tree.
  *    Add the new leaf as a child of the parent of leaf lsearch
  *    (where the search for newKey ended) if there's room.
  *    Otherwise, if the parent of lsearch has no room,
  *    split the parent and push the problem up to the grandparent.
  *    All work at the grandparent or above (where all nodes ---
  *    parent or child --- are interior nodes) is handled by
  *    helper method addIndexValueAndChild.
  *
  **************************************************************/  
  private void addNewLeaf( String newKey, ProductRecord p, TwoThreeNode lsearch )
  {
    TwoThreeNode lsParent = lsearch.parent;
    TwoThreeNode newChild = new TwoThreeNode( newKey, p, lsParent );
    int lsIndex = -1; // (will be) index of pointer to lsearch in lsParent.child array
     // in case we have to split lsParent:
    TwoThreeNode newParent; 
    String middleValue, largestValue;
    TwoThreeNode secondLargestChild, largestChild;

    if ( lsParent == null )
    {
      // lsearch is the ONLY node in the tree (it's the root)
      // create a new root to be the parent of lsearch and newChild
      if ( newKey.compareTo( lsearch.key[0] ) < 0 )
      {
        // newChild should be the left child, lsearch the right
        root = new TwoThreeNode( lsearch.key[0], null, newChild, lsearch );
      }
      else
      {
        root = new TwoThreeNode( newKey, null, lsearch, newChild );
      }
      lsearch.parent = root;
      newChild.parent = root;
    }
    else  // lsearch has a parent (and lsearch is not the root)
    {
      if ( lsearch == lsParent.child[0] )
        lsIndex = 0;
      else if ( lsearch == lsParent.child[1] )
        lsIndex = 1;
      else if ( lsParent.numKeys == 2 && lsearch == lsParent.child[2] )
       lsIndex = 2;
      else
        System.out.println( "ERROR in addNewLeaf: Leaf lsearch containing " + lsearch.key[0] 
                            + " is not a child of its parent" );

      if ( lsParent.numKeys == 1 )
      {
        // Parent has room for another leaf child
        if ( newKey.compareTo( lsearch.key[0] ) < 0 )
        {
          if ( lsIndex == 1 )
          {
            lsParent.child[2] = lsearch;
            lsParent.child[1] = newChild;
            lsParent.key[1] = lsearch.key[0];
          }
          else
          {
            lsParent.child[2] = lsParent.child[1];
            lsParent.key[1] = lsParent.key[0];
            lsParent.child[1] = lsearch;
            lsParent.child[0] = newChild;
            lsParent.key[0] = lsearch.key[0];
          }
        }
        else // lsearch's key is < newKey
        {
          if ( lsIndex == 1 )
          {
            lsParent.child[2] = newChild;
            lsParent.key[1] = newKey;
          }
          else
          {
            lsParent.child[2] = lsParent.child[1];
            lsParent.key[1] = lsParent.key[0];
            lsParent.child[1] = newChild;
            lsParent.key[0] = newKey;
          }
        }
        lsParent.numKeys = 2;
        newChild.parent = lsParent;
      }
      else
      {
        // Parent has NO room for another leaf child --- split and push up
        if ( lsIndex == 2 )   // lsearch is rightmost of 3 children
        {
          if ( lsearch.key[0].compareTo( newKey ) < 0 )
          {
            largestChild = newChild;
            secondLargestChild = lsearch;
            largestValue = newKey;
            middleValue = lsParent.key[1];
          }
          else // newKey < lsearch.key[0]
          {
            largestChild = lsearch;
            secondLargestChild = newChild;
            largestValue = lsearch.key[0];
            middleValue = lsParent.key[1];
          }
        }
        else if ( lsIndex == 1 ) // lsearch is middle of 3 children
        {
          largestChild = lsParent.child[2];
          largestValue = lsParent.key[1];
          if ( lsearch.key[0].compareTo( newKey ) < 0 )
          {
            secondLargestChild = newChild;
            middleValue = newKey;
          }
          else // newKey < lsearch.key[0]
          {
            secondLargestChild = lsearch;
            middleValue = lsearch.key[0];
            lsParent.child[1] = newChild;
            newChild.parent = lsParent;
          }
        }
        else // lsIndex == 0   lsearch is leftmost of 3 children
        {
          largestChild = lsParent.child[2];
          secondLargestChild = lsParent.child[1];
          largestValue = lsParent.key[1];
          middleValue = lsParent.key[0];
          if ( lsearch.key[0].compareTo( newKey ) < 0 )
          {
            lsParent.child[1] = newChild;
            lsParent.key[0] = newKey;
          }
          else // newKey < lsearch.key[0]
          {
            lsParent.child[1] = lsearch;
            lsParent.child[0] = newChild;
            lsParent.key[0] = lsearch.key[0];
          }
          newChild.parent = lsParent;
        }
        newParent = new TwoThreeNode( largestValue, lsParent.parent, secondLargestChild, largestChild );
        lsParent.numKeys = 1;
        lsParent.key[1] = null;
        lsParent.child[2] = null;
        largestChild.parent = newParent;
        secondLargestChild.parent = newParent;
        // add new parent to grandparent:
        if ( lsParent.parent == null )
        {
          root = new TwoThreeNode( middleValue, null, lsParent, newParent );
          lsParent.parent = root;
          newParent.parent = root;
        }
        else
          addIndexValueAndChild( lsParent.parent, middleValue, newParent );
      }
    } // end else lsearch has a parent
  }

  
  /************************************************************
  *
  *  addIndexValueAndChild
  *    Insert index value m and the corresponding new child (mChild) 
  *  into TwoThreeNode curr.
  *
  *  (A child of curr was split, and index value m and new child mChild
  *  are the result of the split and must be added to curr, if possible.
  *  If they can't be added to curr (because curr is already full), then
  *  curr must also be split and the problem pushed up to curr's parent.)
  *
  **************************************************************/
  private void addIndexValueAndChild( TwoThreeNode curr, 
                                      String m, TwoThreeNode mChild )
  {
    TwoThreeNode newNode;
    String midKey;

    if ( curr.numKeys == 1 )
    {
      // There's room for m and its child in curr.

      if ( m.compareTo( curr.key[0] ) < 0 )
      {
        // First child of curr was split to create mChild.
        // Order of keys: m < curr.key[0].
        // Order of children: curr.child[0] < mChild < curr.child[1].
        // m becomes the first key and its child becomes
        // the middle child.

        curr.key[1] = curr.key[0];
        curr.child[2] = curr.child[1];
        curr.key[0] = m;
        curr.child[1] = mChild;
      }
      else
      {
        // Second child of curr was split to create mChild.
        // Order of keys: curr.key[0] < m.
        // Order of children: curr.child[0] < curr.child[1] < mChild.
        // m becomes the second key and its child
        // becomes the rightmost child.

        curr.key[1] = m;
        curr.child[2] = mChild;
      }
      curr.numKeys = 2;
      mChild.parent = curr;
    }
    else
    {
      // There's no room for m and its child in curr.
      // Split curr into two (the original 
      // TwoThreeNode curr and a new TwoThreeNode) and 
      // push the middle key and a pointer to the new
      // TwoThreeNode up to the parent.

      if ( m.compareTo( curr.key[0] ) < 0 )
      {
         // First child of curr was split to create mChild.
         // Order of keys: m < curr.key[0] < curr.key[1].
         // Order of children:
         // curr.child[0] < mChild < curr.child[1] < curr.child[2].
         // Original node gets key m and children
         // curr.child[0] and mChild.
         // New node gets key curr.key[1] and children
         // curr.child[1] and curr.child[2].
         // curr.key[0] is the middle key.

         midKey = curr.key[0];
         newNode = new TwoThreeNode( curr.key[1], curr.parent, curr.child[1], curr.child[2] );
         curr.child[1].parent = newNode;
         curr.child[2].parent = newNode;
         mChild.parent = curr;
         curr.key[0] = m;
         curr.child[1] = mChild;
      }
      else if ( m.compareTo( curr.key[1] ) < 0 )
      {
        // Second child of curr was split to create curr.
        // Order of keys: curr.key[0] < m < curr.key[1].
        // Order of children:
        // curr.child[0] < curr.child[1] < mChild < curr.child[2].
        // Original node retains key curr.key[0] and children
        // curr.child[0] and curr.child[1].
        // New node gets key curr.key[1] and children
        // mChild and curr.child[2].
        // m is  the middle key.

        midKey = m;
        newNode = new TwoThreeNode( curr.key[1], curr.parent, mChild, curr.child[2] );
        mChild.parent = newNode;
        curr.child[2].parent = newNode;
      }
      else
      {
        // Order of keys: curr.key[0] < curr.key[1] < m.
        // Order of children:
        // curr.child[0] < curr.child[1] < curr.child[2] < mChild.
        // Original node retains key curr.key[0] and children
        // curr.child[0] and curr.child[1].
        // New node gets key m and children
        // curr.child[2] and mChild.
        // curr.key[1] is the middle key.

        midKey = curr.key[1];
        newNode = new TwoThreeNode( m, curr.parent, curr.child[2], mChild );
        curr.child[2].parent = newNode;
        mChild.parent = newNode;
      }
      curr.numKeys = 1;
      curr.key[1] = null;
      curr.child[2] = null;
      if ( curr != root )
        addIndexValueAndChild( curr.parent, midKey, newNode );
      else
      {
        root = new TwoThreeNode( midKey, null, curr, newNode );
        curr.parent = root;
        newNode.parent = root;
      }
    }
  } 

  
  /************************************************************
  *
  *  printTable
  *    Print an appropriate message if the tree is empty;
  *    otherwise, call a recursive method to print the
  *    data values in an inorder traversal.
  *
  **************************************************************/
  public void printTable()
    
  {
      
      if (root == null)
          
      {
          
          System.out.println("The table is empty.");
          
      }
      
      else
          
      {
          
          root.printInorder();
          
      }
      
  } 

} 