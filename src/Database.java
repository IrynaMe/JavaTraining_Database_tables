
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Database {

    private String urlDatabase;

    private String url;
    private Connection conn;
    private Statement statement;

    private String USER = "root";
    private String PASSWORD = "root";
    Scanner scanner = new Scanner(System.in);


    //constructor
    public Database() throws SQLException {
        url = "jdbc:mysql://localhost:8889";
        this.urlDatabase = url + "?user=" + USER + "&password=" + PASSWORD;
    }

    //metodo connessione -> creo DB, apro connection->ritorna oggetto connection
    public Connection connettiDb(String database) {
        //apro connessione
        try {
            conn = DriverManager.getConnection(urlDatabase);
            statement = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS " + database;
            statement.executeUpdate(sql);
            System.out.println("______________________________________");
            System.out.println("Connesso al database " + database + " con successo");
            System.out.println("______________________________________");
            //uso db
            sql = "USE " + database;
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Impossible fare connessione a DB: " + e.getMessage());
        }
        return conn;
    }

    public void creaTab() {
        ArrayList<String> nomiColonne = new ArrayList<>();//per inserimento column_names con scanner
        String colonneTabella = "";//preparo stringa da inserire in sql query per creare tabella
        System.out.println("Inserisci il nome della tabella (senza spazi, es. nome_tabella) ");
        String nome_tabella = scanner.next().toLowerCase();
        boolean flag;
        do {
            System.out.println("Inserisci nome della colonna (senza spazi, es. nome_colonna)");
            String nomeColonna = scanner.next();
            //preparo parametri delle colonne
            String descrColonna = " VARCHAR(255), ";
            String paramColonna = nomeColonna.concat(descrColonna);//->nome_colonna VARCHAR(255),
            nomiColonne.add(paramColonna);// metto righe in ArrayList
            //inserimento multipli colonne con un ciclo
            System.out.println("vuoi creare altra colonna? Inserisci s -> si | altro carattere -> no");
            String scelta = scanner.next().toLowerCase();
            if (scelta.equals("s")) {
                flag = true;
            } else {
                flag = false;
            }
        } while (flag);
        for (int i = 0; i < nomiColonne.size(); i++) {
            if (nomiColonne.get(i) != null) {
                colonneTabella += nomiColonne.get(i).toString();
            }
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + nome_tabella + " " +
                "(id INTEGER not NULL AUTO_INCREMENT, " + colonneTabella +
                " PRIMARY KEY ( id ))";
        try {
            statement.executeUpdate(sql);
            System.out.println("______________________________________");
            System.out.println("Tabella è stata creata");
            System.out.println("______________________________________");
        } catch (SQLException e) {
            System.out.println("Impossibile creare una tabella " + e.getMessage());
        }
    }

    //metodo per prendere nomi delle colonne
    public ArrayList<String> recuperoColonne(String tabellaScelta) {
        ArrayList<String> nomiColonne = new ArrayList<>();
        String colonneRiga = "";//preparo pezzo con colonne per inserire nel sqlInput
        //prendo nomi delle colonne dalla tabella
        String sql = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + tabellaScelta + "'";
        ResultSet colonneSet = null;
        try {
            colonneSet = statement.executeQuery(sql);
            // aggiungo nomi delle colonne nel arrayList con il ciclo
            while (colonneSet.next()) {
                String tmp = colonneSet.getString("column_name");
                nomiColonne.add(tmp);
            }
            for (int i = 0; i < nomiColonne.size(); i++) {
                colonneRiga += nomiColonne.get(i).toString() + ",";//result->colonna1,colonna2,
            }
            colonneRiga.substring(0, colonneRiga.length() - 1);//tolgo ultima virgola, result -> colonna1,colonna2
        } catch (SQLException e) {
            System.out.println("non posso leggere i dati dalla DB " + e.getMessage());
        }
        return nomiColonne;
    }


    //metodo per inserire valori nella tabella
    public void inserireValori(String tabellaScelta) {
        String tuttiValori = "'"; //preparo riga per inserire valori nel sqlInput
        String colonneRiga = "";//preparo pezzo con column_names per inserire nel sqlInput
        ArrayList<String> nomiColonne = new ArrayList<>();//per inserire tutti column_names dalla tabella
        //prendo nome delle colonne della tabella
        String sql = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + tabellaScelta + "'";
        try {
            ResultSet colonneSet = statement.executeQuery(sql);
            //prendo valori per ogni colonna e aggiungo nomi delle colonne con il ciclo
            while (colonneSet.next()) {
                String columnName = colonneSet.getString("column_name");
                nomiColonne.add(columnName);
            }
            String[] valoriTab = new String[nomiColonne.size()];//per valori di colum_names da scanner
            // valoriTab[0]=null;
            if (nomiColonne.size() > 0) {
                //prendo da scanner valori per ogni colonna e metto in array
                for (int i = 1; i < nomiColonne.size(); i++) {//camincio da 1 per non toccare id che è auto increment
                    System.out.println("Inserisci valore (senza spazi es. valore_uno) nella colonna '" + nomiColonne.get(i).toString() + "':");
                    valoriTab[i] = scanner.next();
                    tuttiValori += valoriTab[i].toString().concat("','"); //result->'val1, 'val2' ,
                    colonneRiga += nomiColonne.get(i).toString() + ",";//result->column1, column2,
                }
                tuttiValori = tuttiValori.substring(0, tuttiValori.length() - 2); //result->'val1', 'val2'
                colonneRiga = colonneRiga.substring(0, colonneRiga.length() - 1);//tolgo ultima virgola, result -> result->column1, column2
                String sqlInput = "INSERT INTO " + tabellaScelta + " (" + colonneRiga + ") VALUES (" + tuttiValori + ")";
                statement.executeUpdate(sqlInput);
                System.out.println("______________________________________");
                System.out.println("Inserimento effettuato con successo");
                System.out.println("______________________________________");
            } else {
                System.out.println("______________________________________");
                System.out.println("Tabella con il nome '" + tabellaScelta + "' non esiste");
                System.out.println("______________________________________");
            }

        } catch (SQLException ex) {
            System.out.println("Non posso leggere i dati da DB");
        }
    }

    //metodo per estrazione dati dalla tabella
    public void estrai(String tabella) {
        ArrayList<String> nomiColonne = recuperoColonne(tabella);//uso metodo per prendere column_names


        String estrai = "SELECT * FROM " + tabella;
        ResultSet resultSet;
        try {
            resultSet = (statement.executeQuery(estrai));
            if (!resultSet.next()) {
                System.out.println("______________________________________");
                System.out.println("Tabella è vuota");
                System.out.println("______________________________________");
            } else {
                //estraggo i valori di ogni colonna
                do {
                    for (int i = 0; i < nomiColonne.size(); i++) {
                        System.out.println(nomiColonne.get(i) + ": " + resultSet.getString(nomiColonne.get(i)));
                    }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            System.out.println("non posso estrarre i dati dalla tabella" + e.getMessage());
        }
    }

    public String mostraTabelle() {
        String tutteTabelle = "";
        String sql = "SHOW TABLES";
        // int tableCount = 0;//per contare tabelle nella DB
        boolean dbNonVuota = true;
        try {
            ResultSet result = statement.executeQuery(sql);
            //se ci sono tabelle stampo i nomi e conto quantità
            while (result.next()) {
                if (dbNonVuota) {
                    System.out.println("Tabelle disponibile sono: ");//stampo solo una volta se ci sono tabelle in DB
                    dbNonVuota = false;
                }
                String tableName = result.getString(1);
                System.out.println(tableName);
                //tableCount++;
                tutteTabelle += tableName + ", ";
            }
            // if (tableCount == 0) {
            if (tutteTabelle.length() == 0) {
                System.out.println("Il database non contiene tabelle");
            }
        } catch (SQLException e) {
            System.out.println("non posso mostrare tabelle: " + e.getMessage());
        }
        return tutteTabelle;
    }

    public void cancellaRecord(String tabella, String id) throws SQLException {
        String controlloId = "SELECT id FROM " + tabella;
        ResultSet result = null;
        boolean trovatoId = false;
        result = statement.executeQuery(controlloId);
        //se ci sono tabelle stampo i nomi e conto quantità
        while (result.next()) {
            String tableId = result.getString(1);
            if (id.equals(tableId)) {
                String sql = "DELETE FROM " + tabella + " WHERE id = " + id;
                statement.executeUpdate(sql);
                System.out.println("_______________________________________________");
                System.out.println("Dati per id: " + id + " cancellato con successo");
                System.out.println("_______________________________________________");
                trovatoId = true;
                break;
            }
        }
        if (!trovatoId) {
            System.out.println("_______________________________________________");
            System.out.println("Record con id " + id + " non trovato");
            System.out.println("_______________________________________________");
        }
    }


    public void svuotareTabella(String tabella) {
        String sql = "DELETE FROM " + tabella;
        try {
            statement.executeUpdate(sql);
            System.out.println("_______________________________________________");
            System.out.println("Tabella " + tabella + " è stata svuotata");
            System.out.println("_______________________________________________");
        } catch (SQLException e) {
            System.out.println("Non posso cancellare utenti: " + e.getMessage());
        }
    }

    public void cancellaTabella(String tabella) {

        String sql = "DROP TABLE " + tabella;
        try {
            statement.executeUpdate(sql);
            System.out.println("_______________________________________________");
            System.out.println("Tabella " + tabella + " cancellata con successo");
            System.out.println("_______________________________________________");
        } catch (SQLException e) {
            System.out.println("Non posso cancellare la tabella: " + e.getMessage());
        }
    }


}//









