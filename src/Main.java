import java.awt.*;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static Database db1;

    static {
        try {
            db1 = new Database();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        insertDb();
        Main app = new Main();
        app.menu();
    }

    static public void menu() {

        boolean flag = true;
        while (flag) {
            System.out.println("Scegli operazione");
            System.out.println("1 -> creare tabella");
            System.out.println("2 -> inserire valori nella tabella");
            System.out.println("3 -> mostrare dati dalla tabella");
            System.out.println("4 -> cancellare dati dalla tabella per unico id");
            System.out.println("5 -> cancellare tutti dati dalla tabella");
            System.out.println("6 -> cancellare tabella");
            System.out.println("7 -> cambiare database");
            System.out.println("0 -> uscire dal programma");
            String scelta = scanner.next();

            switch (scelta) {
                case "1":
                    db1.creaTab();
                    break;
                case "2":
                    inserireValoriTab();
                    break;
                case "3":
                    stampaTabella();
                    break;
                case "4":
                    deleteRecord();
                    break;
                case "5":
                    deleteAllRecords();
                    break;
                case "6":
                    deleteTable();
                    break;
                case "7":
                    insertDb();
                    break;
                case "0":
                    System.out.println("Programma in chiusura");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Scelta errata");
                    break;
            }
        }
    }

    private static void insertDb() {
        System.out.println("Inserisci il nome di Database da usare/creare");
        String nomeDb = scanner.next();
        db1.connettiDb(nomeDb);
    }

    private static void stampaTabella() {
        String nomiTabelle = db1.mostraTabelle();
        if (nomiTabelle.length() > 1) {
            System.out.println("Inserisci il nome della tabella ");
            String nome_tab = scanner.next();
            if (nomiTabelle.contains(nome_tab)) {
                db1.estrai(nome_tab);
            } else {
                System.out.println("Tabella con il nome '" + nome_tab + "' non trovata");
            }
        }
    }

    private static void inserireValoriTab() {
        String nomiTabelle = db1.mostraTabelle();
        if (nomiTabelle.length() > 1) {
            System.out.println("Inserisci il nome della tabella ");
            String nome_tab = scanner.next();
            if (nomiTabelle.contains(nome_tab)) {
                db1.inserireValori(nome_tab);
            } else {
                System.out.println("Tabella con il nome '" + nome_tab + "' non trovata");
            }
        }
    }

    static public void deleteRecord() {
        String nomiTabelle = db1.mostraTabelle();
        if (nomiTabelle.length() > 1) {
            System.out.println("Inserisci nome della tabella ");
            String nome_tab = scanner.next();
            if (nomiTabelle.contains(nome_tab)) {
                db1.estrai(nome_tab);
                System.out.println("Inserisci id");
                String id = scanner.next();
                try {
                    db1.cancellaRecord(nome_tab, id);
                } catch (SQLException e) {
                    System.out.println("Non posso leggere i dati " + e.getMessage());
                }
            } else {
                System.out.println("Tabella con il nome '" + nome_tab + "' non trovata");
            }
        }
    }

    static public void deleteAllRecords() {
        String nomiTabelle = db1.mostraTabelle();
        if (nomiTabelle.length() > 1) {
            System.out.println("Inserisci nome della tabella ");
            String nome_tab = scanner.next();
            if (nomiTabelle.contains(nome_tab)) {
                db1.svuotareTabella(nome_tab);
            } else {
                System.out.println("Tabella con il nome '" + nome_tab + "' non trovata");
            }
        }
    }

    static public void deleteTable() {
        String nomiTabelle = db1.mostraTabelle();
        if (nomiTabelle.length() > 1) {
            System.out.println("Inserisci nome della tabella da cancellare");
            String nome_tab = scanner.next();
            if (nomiTabelle.contains(nome_tab)) {
                db1.cancellaTabella(nome_tab);
            } else {
                System.out.println("Tabella con il nome '" + nome_tab + "' non trovata");
            }
            db1.mostraTabelle();
        }
    }


}//