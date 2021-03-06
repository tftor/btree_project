package root;

import java.util.Random;

import root.tree.BTree;
import root.tree.NodeEntry;
import root.utils.ConsoleMenu;


public class BTreeStarter {

	public static final boolean IS_DEBUG = false;
	public static final boolean SHOW_TREE_NEXT_TO_MENU = false;
	
	public static void main(String[] args) {
		System.out.printf("%20s%n", "Initiale Erzeugung des B-Baums");
		BTree tree = new BTree(ConsoleMenu.readInt("Ordnung m des B-Baums (mind. 3) [3]: ", 3, 3));
		
		ConsoleMenu console = new ConsoleMenu(tree, SHOW_TREE_NEXT_TO_MENU);

		console.addMenuItem("Erzeuge einen neuen B-Baum", 1);
		console.addMenuItem("Zeichne Baum", 2);
		console.addMenuItem("Fuelle den Baum mit Zufallszahlen", 3);
		console.addMenuItem("Zahl suchen", 5);
		console.addMenuItem("Zahl hinzufuegen", 6);
		console.addMenuItem("Zahl loeschen", 7);
		console.addMenuItem("Zeige B-Baum-Statistik", 9);
		console.addMenuItem("Beenden", 0);
		
		if(IS_DEBUG) {
			//schneller f�r Tests
			fillWithRandomData(tree);
		}
		
		int choice = -1;
		
		while(choice != 0) {
			choice = console.showMenu();
			System.out.print("-------------------------------------------------\n");
			//System.out.println("Auswahl: " + choice);
			switch (choice) {
				case 1:
					tree = new BTree(ConsoleMenu.readInt("Ordnung m des B-Baums (mind. 3) [3]: ", 3, 3));
					console.setTree(tree);
					break;
				case 2:
					tree.printTree();
					break;
				case 3:
					fillWithRandomData(tree);
					break;
				case 5:
					searchKey(tree);
					break;
				case 6:
					addOneNumber(tree);
					break;
				case 7:
					deleteKey(tree);
					break;
				case 9:
					tree.printStats();
					break;
				default:
					break;
			}
		}
		
		System.out.println("Anwendung wird beendet...");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Beendet...");
	}

	/**
	 * Menuepunkt mit Hilfe dessen mehrere Schluessel eingefuegt werden
	 * @param tree BTree-Objekt in den mehrere Schluessel eingefuegt werden
	 */
	public static void fillWithRandomData(BTree tree) {
		int minNumberOfValues = ConsoleMenu.readInt("Anzahl der Schluessel [20]: ", 20);
		int maxValue = ConsoleMenu.readInt("Maximaler Wert [" + minNumberOfValues*2 + "]: ", minNumberOfValues*2, minNumberOfValues);
		int seed = ConsoleMenu.readInt("Seed [4711]: ", 4711);
		
		Random rand = new Random((long) seed);
		int randNumber = 0;
		boolean successfulInsert = false;
		int failedInserts = 0;
		for(int i = 0; i < minNumberOfValues; i++) {
			do {
				randNumber = rand.nextInt(maxValue) + 1;
				successfulInsert = tree.insertEntry(randNumber, (1000-randNumber) + "");
				failedInserts++;
			} while(!successfulInsert && failedInserts < 100);
			if(IS_DEBUG) System.out.println(randNumber);
			if(failedInserts == 100) {
				System.out.println("Fehler: Zu viele (100) Einfuegeversuche fehlgeschlagen!\n"+
								   "        Es wurden " + (i == 0 ? "keine": "nur " + i) + " Zahlen eingefuegt.");
				break;
			}
			failedInserts = 0;
		}
		tree.printTree();
	}
	
	/**
	 * Menuepunkt mit Hilfe dessen ein Schluessel eingefuegt wird
	 * @param tree BTree-Objekt in den ein Schluessel eingefuegt wird
	 */
	public static void addOneNumber(BTree tree) {
		if(tree.insertEntry(new NodeEntry(ConsoleMenu.readInt("Einzufuegender Schluessel: ")))) {
			System.out.printf("%40s%n", "Schluessel erfolgreich eingefuegt");
		} else {
			System.out.printf("%40s%n", "Schluessel nicht eingefuegt, da dieser schon im Baum vorhanden ist");
		}
		tree.printTree();			
	}
	
	/**
	 * Menuepunkt mit Hilfe dessen ein Schluessel gesucht wird
	 * @param tree BTree-Objekt in dem ein Schluessel gesucht wird
	 */
	public static void searchKey(BTree tree) {
		NodeEntry searchResult = tree.searchKey(ConsoleMenu.readInt("Zu suchender Schluessel: "));
		if(searchResult != null) {
			System.out.printf("Schluessel mit dem Key %d gefunden\n und er enhaelt folgende Daten:\n         %s\n", searchResult.getKey(), searchResult.getData());
		} else {
			System.out.printf("%40s%n", "Schluessel nicht gefunden");
		}
	}
	
	/**
	 * Menuepunkt mit Hilfe dessen ein Schluessel geloescht wird
	 * @param tree BTree-Objekt aus dem ein Schluessel geloescht wird
	 */
	private static void deleteKey(BTree tree) {
		int keyToDelete = ConsoleMenu.readInt("Zu loeschender Schluessel: ", -1, 0);
		System.out.println("Baum vorher");
		tree.printTree();
		NodeEntry removedEntry = tree.removeEntry(keyToDelete);
		if(removedEntry != null) {
			System.out.printf("%40s%n", "Schluessel erfolgreich geloescht");
			System.out.println("Baum nachher");
			tree.printTree();
			System.out.println("Geloeschter Schluessel:");
			System.out.println("      key: " + removedEntry.getKey());
		} else {
			System.out.printf("%40s%n", "Schluessel nicht geloescht");
		}
	}
}
