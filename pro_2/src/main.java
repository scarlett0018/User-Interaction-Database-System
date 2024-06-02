import java.sql.SQLException;
import java.util.*;

public class main {
    public static void main(String[] args) {

        Scanner in= new Scanner(System.in);

        Properties prop = Command_In.loadDBUser();

        Command_In.openDB(prop);
        Command_In.clearDataInTable();
        Command_In.closeDB();


//        long start = System.currentTimeMillis();

        try {
            Initial_Input.openDB(prop);
            System.out.println("Loading...");
            Initial_Input.Initiliaza_post();
            Initial_Input.loadData();
            Initial_Input.loadReply();
            Initial_Input.add_constrain();
            Initial_Input.con.commit();

//            do insert command
            System.out.println("Finish loading");

            Command_In.openDB(prop);
            System.out.println("Please enter your command. 0 means quit.");
            int require= in.nextInt();

            while (require!=0){

                switch (require){
                    case 1:Command_In.register();
                            break;
                    case 2:Command_In.action();
                            break;
                    case 3:Command_In.seeAction();
                            break;
                    case 4:
                        System.out.println("Please input F, D or C");
                        String s= in.next();
                        Command_In.follow(s);
                            break;
                    case 5:Command_In.publish();
                            break;
                    case 6:
                        System.out.println("Please input 1 or 2 ");

                        Integer integer=in.nextInt();
                        Command_In.reply(integer);
                        break;
                    case 7:
                        System.out.println("Please input 1 or 2 or 3");
                        Integer inn=in.nextInt();
                        Command_In.seeReply(inn);
                        break;
                    case 8:
                        Command_In.seeUserInfo();
                        break;
                    default:
                        System.out.println("Invalid command.");
                        break;

                }

                System.out.println("Please enter your command.");
                require= in.nextInt();
            }

            System.out.println("Quit");

            Command_In.con.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Command_In.closeDB();

    }

}
