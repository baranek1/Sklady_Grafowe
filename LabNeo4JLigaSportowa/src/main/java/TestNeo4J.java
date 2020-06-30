import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import static org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM;


public class TestNeo4J {
    private static Scanner input = new Scanner(System.in);
    static boolean koniec = false;


    private static Result updateTeam( Transaction transaction,int id_Dr, String druzynaName, String rokZalozenia) {
        String command="MATCH (s:Druzyna) WHERE ID(s)=$id_Dr SET s.druzynaName=$druzynaName, s.rokZalozenia=$rokZalozenia return s";
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("id_Dr", id_Dr);
        parameters.put("druzynaName", druzynaName);
        parameters.put("rokZalozenia", rokZalozenia);
        return transaction.run(command, parameters);
    }

    private static Result deleteTeam(Transaction transaction,int id_Dr) {
        String command="MATCH (s:Druzyna) WHERE ID(s)=$id_Dr DETACH DELETE s";
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("id_Dr", id_Dr);
        return transaction.run(command, parameters);

    }


    static private void showMenu() {
        System.out.println("\n**************  MENU:  ***************");
        System.out.println("1 - Dodaj drużynę ");
        System.out.println("2 - Wyświetl wszystko");
        System.out.println("3 - Usuń drużynę");
        System.out.println("4 - Edytuj drużynę");
        System.out.println("0 - WYJŚCIE\n");
    }

    static private int getWyborMenu() {
        int choice = -1;
        do {
            System.out.println("Podaj wybór:");
            choice = Integer.parseInt(input.nextLine());
            if (choice < 0 || choice > 6) {
                System.out.println("Brak takiej opcji!");
            }
        } while (choice < 0 || choice > 6);
        return choice;
    }

    static private void akcja(int wybor,Session session) {
        String nazwaDR;
        String rokZal;
        int id_Dr;
        switch (wybor) {
            case 1:
                System.out.println("Nazwa");
                nazwaDR = input.nextLine();
                System.out.println("Rok założenia");
                rokZal = input.nextLine();

                session.writeTransaction(tx -> createDruzyna(tx, nazwaDR, rokZal));
                session.writeTransaction(tx -> createRelationship(tx, nazwaDR, "Ekstraklasa"));
                break;
            case 2:

                session.writeTransaction(TestNeo4J::readAllNodes);
                break;
            case 3:
                System.out.println("Podaj id");
                id_Dr = input.nextInt();
                input.nextLine();
                session.writeTransaction( tx-> deleteTeam(tx, id_Dr));
                break;
            case 4:
                System.out.println("Podaj id");
                id_Dr = input.nextInt();
                input.nextLine();
                System.out.println("Nazwa");
                nazwaDR = input.nextLine();
                System.out.println("Rok założenia");
                rokZal = input.nextLine();

                session.writeTransaction(tx -> updateTeam(tx, id_Dr,nazwaDR,rokZal));
                break;
            case 0:
                koniec = true;
                break;
            default:
                System.out.println("BŁĄD");
        }
    }

    static void menu(Session session) {
        while (!koniec) {
            showMenu();
            int wybor = getWyborMenu();
            akcja(wybor, session);
        }
    }

    public static Result createDruzyna(Transaction transaction, String druzynaName, String rokZalozenia) {
        String command = "CREATE (:Druzyna {druzynaName:$druzynaName, rokZalozenia:$rokZalozenia})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("druzynaName", druzynaName);
        parameters.put("rokZalozenia", rokZalozenia);
        return transaction.run(command, parameters);
    }

    public static Result createGroup(Transaction transaction, String groupName) {
        String command = "CREATE (:Grupa {groupName:$groupName})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("groupName", groupName);
        return transaction.run(command, parameters);
    }

    public static Result createRelationship(Transaction transaction, String druzynaName, String groupName) {
        String command =
                "MATCH (s:Druzyna),(g:Grupa) " +
                        "WHERE s.druzynaName = $druzynaName AND g.groupName = $groupName "
                        + "CREATE (s)−[r:JEST_W]−>(g)" +
                        "RETURN type(r)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("druzynaName", druzynaName);
        parameters.put("groupName", groupName);
        System.out.println("Executing: " + command);
        return transaction.run(command, parameters);
    }


    public static Result readAllNodes(Transaction transaction) {
        String command =
                "MATCH (n)" +
                        "RETURN n";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }
    public static void printField(Pair<String, Value> field) {
        System.out.println("field = " + field);
        Value value = field.value();
        if (TYPE_SYSTEM.NODE().isTypeOf(value))
            printNode(field.value().asNode());
        else if (TYPE_SYSTEM.RELATIONSHIP().isTypeOf(value))
            printRelationship(field.value().asRelationship());
        else
            throw new RuntimeException();
    }

    public static void printNode(Node node) {
        System.out.println("id = " + node.id());
        System.out.println("labels = " + " : " + node.labels());
        System.out.println("asMap = " + node.asMap());
    }

    public static void printRelationship(Relationship relationship) {
        System.out.println("id = " + relationship.id());
        System.out.println("type = " + relationship.type());
        System.out.println("startNodeId = " + relationship.startNodeId());
        System.out.println("endNodeId = " + relationship.endNodeId());
        System.out.println("asMap = " + relationship.asMap());
    }

    public static Result deleteEverything(Transaction transaction) {
        String command = "MATCH (n) DETACH DELETE n";
        System.out.println("Executing: " + command);
        return transaction.run(command);
    }


    public static void main(String[] args) throws Exception {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jpassword"));
             Session session = driver.session()) {
            session.writeTransaction(tx -> deleteEverything(tx));
            session.writeTransaction(tx -> createGroup(tx, "Ekstraklasa"));
            menu(session);
        }

    }
}
