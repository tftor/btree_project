package franz.tree;

public class BTree {

	private int ordnung; 	// Knoten hat
							// mindestens ordnung/2 s�hne
							// maximal ordnung s�hne
							// maximal ordnung-1 schl�ssel
							// mit k s�hne speichert k-1 schl�ssel
	private int numberOfTreeEntrys = 0;
	private int minEntrys;
	private int middle;
	private BNode root = null;

	public BTree(int ordnung) {
		this.ordnung = ordnung;
		this.minEntrys = (int) Math.ceil(ordnung/2);	// -1
		this.middle = ordnung / 2;
	}

	public NodeEntry searchKey(int key) {
		return searchKey(root, key);
	}
	
	private NodeEntry searchKey(BNode node, int key) {
		if(node == null) 
			return null;
		
		int entryPosition = node.containsKey(key);
		if(entryPosition < 0) {
			return searchKey(node.getChild(node.getNextChildPositionForKey(key)), key);
		} else {
			return node.getEntry(entryPosition); 
		}
	}

	/**
	 * 
	 * @param key
	 * @return true, if key is inserted successful
	 */
	public boolean insertEntry(NodeEntry entry) {
		if (root == null) {							// Noch kein Baum vorhanden
			root = new BNode();
		}
		if(searchKey(entry.getKey()) != null) {	// Schluessel schon vorhanden
			return false;
		}
		
		insertEntry(root, entry);
		
		if (root.getNumberOfEntrys() > ordnung-1) {
			BNode newRoot = new BNode();
			splitTree(newRoot, 0, root);
			root = newRoot;
		}
		numberOfTreeEntrys++;
		return true;
	}
	
	private void insertEntry(BNode node, NodeEntry entry) {
		if(node.getNumberOfChilds() == 0) {
			// BNode ist ein Blatt		
			for(int i = 0; i < ordnung; i++) {
				if(node.getNumberOfEntrys() > i) {												// wenn der Eintrag i existiert
					if(entry.getKey() < node.getKey(i)) {
						node.addEntry(i, entry);
						break;
					}
				} else {
					node.addEntry(i, entry);
					break;
				}
			}
			// Fertig mit Eingefuegen im Blatt
		} else {
			// BNode ist ein Knoten
			int childPositionToInsert = node.getNextChildPositionForKey(entry.getKey());
			
			// Einfuegen in UnterKnoten
			insertEntry(node.getChild(childPositionToInsert), entry);
		
			// Unterknoten voll?
			if(node.getChild(childPositionToInsert).getNumberOfEntrys() > ordnung-1) {
				splitTree(node, childPositionToInsert, node.getChild(childPositionToInsert));
			}
			
		}	
	}

	private void splitTree(BNode node, int childPositionToSplit, BNode subNodeToSplit) {		
		node.addEntry(childPositionToSplit, subNodeToSplit.removeEntry(middle));
		
		BNode rightSubTree = new BNode();
		while(middle < subNodeToSplit.getNumberOfEntrys()) {
			rightSubTree.addChild(subNodeToSplit.removeChild(middle+1));
			rightSubTree.addEntry(subNodeToSplit.removeEntry(middle));
		}
		rightSubTree.addChild(subNodeToSplit.removeChild(subNodeToSplit.getNumberOfChilds() - 1));
		
		node.setChild(childPositionToSplit, subNodeToSplit);
		node.addChild(childPositionToSplit+1, rightSubTree);
	}
	
	/**
	 * 
	 * @param key
	 * @return true, if key is removed successful
	 */
	public NodeEntry removeEntry(int key) {
		if (searchKey(key) == null) {
			return null;
		}
		NodeEntry result = removeKey(root, key);
		
		numberOfTreeEntrys--;
		return result;
	}
	
//	private NodeEntry removeKey2(BNode node, int key) {
//		// get information needed
//		BNode delBTNode = searchKey(key).getNode();
//		if (delBTNode == null) {
//			return null;
//		}
//		int keyIndex = delBTNode.containsKey(key);
//		BNode returnNode = delBTNode.getKey(keyIndex);
//
//
//		if (delBTNode.getNumberOfChilds() == 0) {				// is a leaf **********************************
//			if (delBTNode.nKey > order - 1)
//				// we can delete KeyNode directly
//			{
//				delBTNode.extractKeyNode(keyIndex);
//			} else
//				// we need to get more keys so that we can delete it
//			{
//				BNode parentBTNode = delBTNode.parent;
//				int parentIndex = 0;
//				while (parentBTNode.getBTNode(parentIndex) != delBTNode)
//					parentIndex++;
//				if (parentIndex == parentBTNode.nKey) {
//					BNode leftBTNode = parentBTNode.getBTNode(parentIndex - 1);
//					if (leftBTNode.nKey > order - 1) {
//						delBTNode.kArray[keyIndex] = parentBTNode.getKeyNode(parentIndex);
//						parentBTNode.kArray[parentIndex] = leftBTNode.getKeyNode(leftBTNode.nKey - 1);
//						deleteNode(leftBTNode, leftBTNode.getKeyNode(leftBTNode.nKey - 1).getKey());
//					} else {
//						delBTNode.mergeWithBTNode();
//					}
//				} else {
//					BNode rightBTNode = parentBTNode.getBTNode(parentIndex + 1);
//					if (rightBTNode.nKey > order - 1) {
//						delBTNode.kArray[keyIndex] = parentBTNode.getKeyNode(parentIndex);
//						parentBTNode.kArray[parentIndex] = rightBTNode.getKeyNode(0);
//						deleteNode(rightBTNode, rightBTNode.getKeyNode(0).getKey());
//					} else {
//						delBTNode.mergeWithBTNode();
//					}
//				}
//			}
//		} else { // is internal node *********************
//			//try predecesor
//			// get the node to exchange and delete it at leaf position
//			BNode preBTNode = delBTNode.getBTNode(keyIndex);
//			while (preBTNode.getNumberOfChilds() > 0) {
//				preBTNode = preBTNode.getBTNode(preBTNode.nKey);
//			}
//
//			// swap nodes
//			BNode tmpKeyNode = preBTNode.getKeyNode(preBTNode.nKey - 1);
//			preBTNode.kArray[preBTNode.nKey - 1] = delBTNode.kArray[keyIndex];
//			delBTNode.kArray[keyIndex] = tmpKeyNode;
//			deleteNode(preBTNode, preBTNode.getKeyNode(preBTNode.nKey - 1).getKey());
//		}
//		return returnNode;
//	}
	/**
	 * Funktion geht davon aus, dass node oder seine Unterbaeume den Schluessel enthalten
	 * @param node
	 * @param key
	 * @return Entfernter Schluessel aus dem Knoten/Blatt
	 */
	private NodeEntry removeKey(BNode node, int key) {
		// Scriptum(KB-K12)
		NodeEntry returnValue = null;

		int keyEntryPosition = node.containsKey(key);

		if(keyEntryPosition < 0) {										// Knoten enthaelt Key NICHT
			
			int nextChildPosition = node.getNextChildPositionForKey(key);
			
			returnValue = removeKey(node.getChild(nextChildPosition), key);
			showTree();
			// Fall 1: nextChild besitzt nach dem Loeschen noch m Schluessel => fertig (167)
			
			if(node.getChild(nextChildPosition).getNumberOfEntrys() == minEntrys-1) {
				System.out.println("TEST");
				// nextChild besitzt nach dem Loeschen nur noch m-1 Schluessel
				int numberOfLeftSilbingNodeEntrys = node.getChild(nextChildPosition-1) != null ? node.getChild(nextChildPosition-1).getNumberOfEntrys() : 0;
				int numberOfRightSilbingNodeEntrys = node.getChild(nextChildPosition+1) != null ? node.getChild(nextChildPosition+1).getNumberOfEntrys() : 0;
				
				if(numberOfLeftSilbingNodeEntrys > minEntrys || numberOfRightSilbingNodeEntrys > minEntrys) {
					// Fall 2: nextChild besitzt nach dem Loeschen nur m-1 Schluessel && ein Geschwisterknoten besitzt mind. m+1 Schluessel => rotate (168 f)
					if(numberOfLeftSilbingNodeEntrys < numberOfRightSilbingNodeEntrys) {
						// Rotation mit dem rechten Geschwisterknoten
						rotateRight(node, nextChildPosition);
					} else {
						// Rotation mit dem linken Geschwisterknoten
						rotateLeft(node, nextChildPosition);
					}
				} else {
					// Fall 3: nextChild besitzt nach dem Loeschen nur m-1 Schluessel && die Geschwisterknoten besitzen ebenfalls m Schluessel => merge (170 f)  !!! Beachte, wenn Wurzel !!!
					if(node != root) {
						System.out.println("not root");
						merge(node, nextChildPosition);
					}
						
				}
			}
			
		} else {														// Knoten enthaelt Key
			
			if(node.getNumberOfChilds() == 0) {							// Knoten ist ein Blatt
				returnValue =  node.removeEntry(node.containsKey(key));
			} else {			// Knoten ist ein Knoten
				if(node.getChild(keyEntryPosition+1).containsKey(key) >= 0) {
					NodeEntry newValue = removeKey(node, getSmallestNextNode(node.getChild(keyEntryPosition+1)).getKey(0));
					showTree();
					insertEntry(newValue);
					returnValue = removeKey(node, key);
					showTree();
				//node.addEntry(keyEntryPosition, newValue);
				} else {
					returnValue = node.setEntry(keyEntryPosition, removeKey(node, getSmallestNextNode(node.getChild(keyEntryPosition+1)).getKey(0)));
				}
//				if(node.getChild(keyEntryPosition+1).getNumberOfEntrys() == 0) {
//					System.out.println("JO");
//					rotateLeft(node, keyEntryPosition+1);
//				} else if(node.getChild(keyEntryPosition).getNumberOfEntrys() == 0) {
//					System.out.println("NO");
//				}
			}
			
		}
		
		return returnValue;
	}
	
	private void rotateRight(BNode node, int nodeKeyPosition) {
		BNode childNode = node.getChild(nodeKeyPosition);
		BNode rightNode = node.getChild(nodeKeyPosition+1);
		
		childNode.addEntry(node.removeEntry(nodeKeyPosition));
		childNode.addChild(rightNode.removeChild(0));
		
		node.addEntry(nodeKeyPosition, rightNode.removeEntry(0));
	}
	
	private void rotateLeft(BNode node, int nodeKeyPosition) {
		BNode childNode = node.getChild(nodeKeyPosition);
		BNode leftNode = node.getChild(nodeKeyPosition-1);
		
		childNode.addEntry(0, node.removeEntry(nodeKeyPosition-1));
		childNode.addChild(0, leftNode.removeChild(leftNode.getNumberOfChilds()-1));
		
		node.addEntry(nodeKeyPosition-1, leftNode.removeEntry(leftNode.getNumberOfEntrys()-1));
	}
	
	private void merge(BNode node, int nodeKeyPosition) {
		BNode leftNode = null;
		BNode rightNode = null;
		if(nodeKeyPosition == node.getNumberOfChilds()-1) {
			// nextChild ist das rechteste Kind des Elternknotens
			leftNode = node.getChild(node.getNumberOfChilds()-2);
			rightNode = node.getChild(node.getNumberOfChilds()-1);
		} else {
			leftNode = node.getChild(nodeKeyPosition);
			rightNode = node.getChild(nodeKeyPosition+1);
		}
		
		leftNode.addEntry(node.removeEntry(nodeKeyPosition));
		
		while(rightNode.getNumberOfChilds() > 0) {
			leftNode.addChild(rightNode.removeChild(0));
			leftNode.addEntry(rightNode.removeEntry(0));
		}
		
		node.removeChild(nodeKeyPosition+1);
	}
	
	/**
	 * @deprecated
	 * @param leftTree
	 * @param rightTree
	 * @param node
	 * @param childPositionToMerge
	 */
	private void mergeNodes(BNode leftTree, BNode rightTree, BNode node, int childPositionToMerge) {
		if(leftTree.getNumberOfEntrys() + rightTree.getNumberOfEntrys() >= ordnung) {			// Rotation
			
			rightTree.addEntry(0, node.setEntry(childPositionToMerge, leftTree.removeEntry(middle)));
			rightTree.addChild(0, leftTree.removeChild(leftTree.getNumberOfChilds()+1));
			
			while(middle < leftTree.getNumberOfEntrys()) {
				rightTree.addEntry(0, leftTree.removeEntry(leftTree.getNumberOfEntrys()-1));
				rightTree.addChild(0, leftTree.removeChild(leftTree.getNumberOfChilds()+1));
			}
		} else {																				// Zusammenlegen
			leftTree.addEntry(node.removeEntry(childPositionToMerge));
			leftTree.addChild(rightTree.removeChild(0));
			
			while(rightTree.getNumberOfEntrys() > 0) {
				leftTree.addEntry(rightTree.removeEntry(0));
				leftTree.addChild(rightTree.removeChild(0));
			}
			
			node.removeChild(childPositionToMerge);
		}
	}
	
	/**
	 * Methode zum Auffinden des vorherigen groesseren Schluessels im linken Teilbaum
	 * @param node
	 * @return
	 */
	private BNode getGreatestPreviousNode(BNode node) {
		if(node.getNumberOfChilds() > 0)
			return getGreatestPreviousNode(node.getChild(node.getNumberOfEntrys()-1+1));
		else
			return node;
	}
	
	/**
	 * Methode zum Auffinden des naechst kleineren Schluessels im rechten Teilbaum
	 * @param node
	 * @return
	 */
	private BNode getSmallestNextNode(BNode node) {
		if(node.getNumberOfChilds() > 0)
			return getSmallestNextNode(node.getChild(0));
		else
			return node;
	}

	public void showTree() {
		System.out.println("Ausgabe des Baums:");
		
		int maxHeight = getMaxHeight();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getWidth(root) + 24; i++) {
			sb.append('#');
		}
		String border = sb.toString();
		System.out.println(border);
		sb = new StringBuilder();
		for (int i = 1; i <= maxHeight; ++i) {
			sb.append(String.format("# Tiefe %2d |", i));
			printLine(sb, root, i);
			sb.append(String.format("| Tiefe %2d #", i));
			sb.append("\n");
		}
		System.out.print(sb.toString());
		System.out.println(border);
	}

	private void printLine(StringBuilder sb, BNode node, int depth) {
		if (node == null)
			return;
		for (int i = 0; i < node.getNumberOfEntrys(); i++) {
			if (depth == 1) {
				fillSpaces(sb, getWidth(node.getChild(i)));
				if (i == 0 && node.getNumberOfEntrys() == 1) { 									// nur ein Eintrag in Knoten
					sb.append(String.format("(%2s)", node.getKey(i)));
				} else if (i == 0) { 															// ist erster Eintrag in Knoten
					sb.append(String.format("(%2s ", node.getKey(i)));
				} else if (i != 0 && i + 1 == node.getNumberOfEntrys()) { 						// ist letzer Eintrag in Knoten
					sb.append(String.format(" %2s)", node.getKey(i)));
				} else {
					sb.append(String.format(" %2s ", node.getKey(i)));
				}
				if(i+1 == node.getNumberOfEntrys())
					fillSpaces(sb, getWidth(node.getChild(i+1)));
			} else {
				printLine(sb, node.getChild(i), depth - 1);
				fillSpaces(sb, 4);
				if(i+1 == node.getNumberOfEntrys())
					printLine(sb, node.getChild(i+1), depth - 1);
			}
		}
	}

	private void fillSpaces(StringBuilder sb, int count) {
		for (int i = 0; i < count; i++)
			sb.append(' ');
	}

	private int getWidth(BNode node) {
		if (node == null)
			return 0;
		int leftWidth = 0;
		int rightWidth = 0;
		for (int i = 0; i < node.getNumberOfEntrys(); i++) {
			if (node.getChild(i) != null)
				leftWidth += getWidth(node.getChild(i));
			//if (node.getChild(i+1) != null)
			if (i+1 == node.getNumberOfEntrys())
				rightWidth += getWidth(node.getChild(i+1));
		}
		return leftWidth + (4 * node.getNumberOfEntrys()) + rightWidth;
	}

	public void showStat() {
		System.out.println("Ausgabe der Statistik:");
		System.out.println("Ordung:    " + ordnung);
		System.out.println("MinEntrys: " + minEntrys);
		System.out.println("MaxEntrys: " + (ordnung-1));
		System.out.println("MinHeight: " + getMinHeight());
		System.out.println("MaxHeight: " + getMaxHeight());
		
	}

	public int getMinHeight() {
		double minHeight = Math.ceil(Math.log(numberOfTreeEntrys + 1) / Math.log(ordnung) - 1);
		int result = (int) minHeight;
		return result;
	}

	public int getMaxHeight() {
		double maxHeight = Math.floor(Math.log((double) ((numberOfTreeEntrys + 1) / 2)) / Math.log(Math.ceil((double) ordnung / 2))) + 1;
		int result = (int) maxHeight;
		return result;
	}
}