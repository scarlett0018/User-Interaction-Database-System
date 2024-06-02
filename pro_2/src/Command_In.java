import com.google.gson.Gson;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Command_In {
    public static Connection con = null;
    public static PreparedStatement stmt = null;
    public static void openDB(Properties prop) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + prop.getProperty("host") + "/" + prop.getProperty("database");
        try {
            con = DriverManager.getConnection(url, prop);
            if (con != null) {
                System.out.println("Successfully connected to the database "
                        + prop.getProperty("database") + " as " + prop.getProperty("user"));
                con.setAutoCommit(false);
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    public static void closeDB() {
        if (con != null) {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                con.close();
                con = null;
            } catch (Exception ignored) {
            }
        }
    }
    public static Properties loadDBUser() {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream("resource/dbUser.properties")));
            return properties;
        } catch (IOException e) {
            System.err.println("can not find db user file");
            throw new RuntimeException(e);
        }
    }

    public static Post[] posts;
    public static Replies[] replies;


    public static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
        Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
        String time = df.format(now);
        return time;
    }
    public static void seeReply (Integer integer) throws SQLException {
        Scanner in =new Scanner(System.in);
        System.out.println("username");
        String username= in.next();
        while (!check("author","author",username)){
            System.out.println("This author name hasn't been registered! Please input correct author name.");
            System.out.println("username");
            username= in.next();
        }

        String sql="";
        if (integer==3){
            System.out.println("author ID");
            String s=in.next();
            sql= String.format("select * from post where authorid='%s';",s);
        }

        else if (integer == 1){
            sql= String.format("select distinct(replycontent),p.postid ,replystars from reply join post p on p.postid = reply.postid join author a on a.authorid = p.authorid " +
                    "where replyauthor='%s';",username);
        }
        else if(integer==2){
            sql= String.format("select * from reply join post p on p.postid = reply.postid join author a on a.authorid = p.authorid " +
                    "where secondaryreplyauthor='%s';",username);

        }
        Statement st = con. createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = st.executeQuery(sql);
        resultSet.last();

        if(resultSet.getRow()==0){
            System.out.println("No reply!");
            return;
        }
        resultSet.beforeFirst();
        if (integer==3){
            System.out.println( username+"'s posts:");

            while (resultSet.next()){
                String postid = resultSet.getString("postid");
                String title = resultSet.getString("title");
                String content = resultSet.getString("postcontent");
                String postingcity = resultSet.getString("postingcity");
                String postingtime = resultSet.getString("postingtime");
                System.out.println("post " + postid);
                System.out.println(title);
//                System.out.println("Reply star: " +replystar);
                System.out.println("Content:"+ content);
                System.out.println("In:"+ postingcity);
                System.out.println("On:"+ postingtime);
                System.out.println();
            }
        }
        if (integer==1){
            System.out.println( username+"'s first reply:");

            while (resultSet.next()){
                String postid = resultSet.getString("postid");
                String replystar = resultSet.getString("replystars");
                String content = resultSet.getString("replycontent");

                System.out.println("On: post " + postid);
                System.out.println("Reply star: " +replystar);
                System.out.println("Content:"+ content);
                System.out.println();
            }
        }
        else if (integer==2){
            System.out.println( username+"'s secondary reply:");

            while (resultSet.next()){
                String postid = resultSet.getString("postid");
                String postAuthor = resultSet.getString("author");
                String postContent = resultSet.getString("postcontent");

                String firAuthor = resultSet.getString("replyauthor");
                String firContent = resultSet.getString("replycontent");
                String replystar = resultSet.getString("replystars");

                String secondaryreplystarsreplystar = resultSet.getString("secondaryreplystars");
                String content = resultSet.getString("secondaryreplycontent");

                System.out.println("On post: " + postid +" by:" + postAuthor);
                System.out.println(postContent);

                System.out.println("On first reply: " + firContent +" by:" + firAuthor);
                System.out.println("First Reply star:" +replystar);

                System.out.println("Content:"+ content);
                System.out.println("Second reply star:" +secondaryreplystarsreplystar);
                System.out.println();
            }
        }




    }
    public static void seeUserInfo() throws SQLException {
        Scanner in =new Scanner(System.in);
        System.out.println("username");
        String username= in.next();
        while (!check("author","author",username)){
            System.out.println("This author name hasn't been registered! Please input correct author name.");
            System.out.println("username");
            username= in.next();
        }

        String sql= String.format("select * from author where author.author='%s';",username);

        Statement st = con. createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = st.executeQuery(sql);
        if(resultSet.next()){
            String authorid = resultSet.getString("authorid");
            String time = resultSet.getString("authorregistrationtime");
            String authophone = resultSet.getString("authophone");
            String author = resultSet.getString("author");

            System.out.println("author name:"+author);
            System.out.println("register time:"+time);
            System.out.println("author phone:"+authophone);
            System.out.println("author id:"+authorid);
        }



    }

    public static void seeAction () throws SQLException {
        Scanner in =new Scanner(System.in);
        System.out.println("username");
        String username= in.next();
        while (!check("author","author",username)){
            System.out.println("This author name hasn't been registered! Please input correct author name.");
            System.out.println("username");
            username= in.next();
        }


        System.out.println("relation");
        String relation= in.next();

        String sqll= String.format("select * from action join post p on p.postid = action.postid " +
                        "where peoplename ='%s' and relation='%s' ;",username,relation);

        Statement st = con. createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = st.executeQuery(sqll);
        resultSet.last();

        if(resultSet.getRow()==0){
            System.out.println("No posts!");
            return;
        }
        resultSet.beforeFirst();

        while (resultSet.next()){
            String authorid = resultSet.getString("authorid");
            String postid = resultSet.getString("postid");
            String content = resultSet.getString("postcontent");
            String city = resultSet.getString("postingcity");
            String time = resultSet.getString("postingtime");
            String title = resultSet.getString("title");

            // Retrieve other column values as needed

            System.out.println("Post ID:"+ postid);
            System.out.println("Title:"+ title);
            System.out.println("By: " + authorid);
            System.out.println(content);
            System.out.println("In: " + city);
            System.out.println("On: " + time);
            System.out.println();
        }

    }

    public static void follow (String s) throws SQLException {
//        Hashtable<String,String> clientInfo=new Hashtable<>();
        Scanner in =new Scanner(System.in);
        System.out.println("username");
        String username= in.next();
        while (!check("author","author",username)){
            System.out.println("This author name hasn't been registered! Please input correct author name.");
            System.out.println("username");
            username= in.next();
        }
//        clientInfo.put("follower",username);


            if (Objects.equals(s, "C")){

                String sqll= String.format("select * from follow join author a on a.author = follow.following where follwer='%s';",username);

                Statement st = con. createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet resultSet = st.executeQuery(sqll);
                resultSet.last();

                if(resultSet.getRow()==0){
                    System.out.println("No following!");
                    return;
                }
                resultSet.beforeFirst();

                while (resultSet.next()){
                    System.out.println("Following:" + resultSet.getString("following"));
                    System.out.println("author ID:" + resultSet.getString("authorid"));
                    System.out.println("registered time:" + resultSet.getString("authorregistrationtime"));
                    System.out.println();
                }
                System.out.println("Check done!");
                return;
            }

            System.out.println("follow");
            String following= in.next();
            while (!check("author","author",following)){
                    System.out.println("This author name hasn't been registered! Please input correct author name.");
                    System.out.println("follow");
                    following= in.next();
            }

             if (Objects.equals(s, "F")){
                 stmt = con.prepareStatement("insert into follow(follwer,following) VALUES (?,?);");
                 stmt.setString(1,  username);
                 stmt.setString(2, following);
             }

            else if (Objects.equals(s, "D")){
                 stmt= con.prepareStatement("delete from follow where follwer='"+username
                         + "' and following='"+ following + "';");
             }

            stmt.executeUpdate();
            System.out.println("Follow or deleted finished!");
            con.commit();


    }

    public static boolean check(String column,String chartname,String id) throws SQLException {
        String sql = "select "+ column +" from "+ chartname +" where "+ column +" = '"+id +"'";

        PreparedStatement statement= con.prepareStatement(sql);
        ResultSet resultSet=statement.executeQuery();
        return resultSet.next();

    }



    public static void register() throws SQLException {
        Hashtable<String,String> clientInfo=new Hashtable<>();
        Scanner in=new Scanner(System.in);
        System.out.println("Register Started");

        System.out.println("author");
        String s2= in.next();
        while (check("author","author",s2)){
            System.out.println("This author name has been registered! Please input correct author name.");
            System.out.println("author");
            s2= in.next();
        }
        clientInfo.put("author", s2);

        System.out.println("authoPhone (may skip)");
        String s3= in.next();
        while ( !s3.matches("1[34578][0-9]{9}") && (!Objects.equals(s3, "skip") )) {
            System.out.println("Not a valid phone number. Please input correct phone number or keyword 'skip' !");
            System.out.println("authoPhone");
            s3= in.next();
        }
        if (s3.matches("1[34578][0-9]{9}"))
            clientInfo.put("authoPhone", s3);


        String sql="SELECT new_id FROM (" +
                "        SELECT FLOOR(random() * 999999999999) + 1 AS new_id" +
                "        FROM author) temp" +
                "        WHERE NOT EXISTS (SELECT authorID FROM author WHERE cast( temp.new_id AS varchar) = authorID)" +
                "        LIMIT 1;";
        Statement st = con. createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = st.executeQuery(sql);
        resultSet.next();
        String ID = resultSet.getString("new_id");



        try {
            stmt = con.prepareStatement("insert  into Author(authorID, author, authorRegistrationTime, authoPhone)\n" +
                    "VALUES (?,?,?,?) on conflict (author) do nothing ;");
            stmt.setString(1,  ID);
            stmt.setString(2, clientInfo.get("author"));
            stmt.setString(3, getTime());
            stmt.setString(4, clientInfo.get("authoPhone"));
            stmt.executeUpdate();

            con.commit();
            System.out.println("Register successfully! Your authorID is "+ ID);
        } catch (SQLException e) {
            System.out.println("Register Failed");
            e.printStackTrace();
        }

    }
public static Long generatePostID() throws SQLException {
    String sql = "SELECT MAX(postid) FROM post";
    PreparedStatement statement = con.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();

    if (resultSet.next()) {
        Long postID = resultSet.getLong(1);
        return postID;
    } else {
        // Handle the case when no result is returned
        return null;
    }
}


    public static void publish() throws SQLException {
        Hashtable<String,String> clientInfo=new Hashtable<>();
        Scanner in =new Scanner(System.in);
        System.out.println("authorID");
        String s51= in.next();
        while (!check("authorid","author",s51)){
            System.out.println("This ID hasn't been registered! Please input correct authorID.");
            System.out.println("authorID");
            s51= in.next();
        }
        clientInfo.put("authorID", s51);
        //检查输入authorID是否合法


        String needType[]=new String[]{"title","postContent", "postingCity(may skip)","types"};
        for (String s:needType
        ) {
            System.out.println(s);
            String s1= in.next();
            if (!Objects.equals(s1, "skip"))
                clientInfo.put(s, s1);
            else continue;
        }

        Long pID=generatePostID()+1;
        try {
            stmt = con.prepareStatement("\n" +
                    "insert into post (authorID, postID, title, postContent, postingCity, postingTime) \n" +
                    "                                VALUES (?,?,?,?,?,?) on conflict (postID) do nothing ;");
            stmt.setString(1,  clientInfo.get("authorID"));
            stmt.setLong(2, pID);
            stmt.setString(3, clientInfo.get("title"));
            stmt.setString(4,  clientInfo.get("postContent"));
            stmt.setString(5,  clientInfo.get("postingCity"));
            stmt.setString(6, getTime());
            stmt.executeUpdate();

            stmt = con.prepareStatement("insert into cate(postID, type) VALUES (?,?);");
            for (String s:clientInfo.get("types").toString().split(",")
                 ) {
                stmt.setLong(1, pID);
                stmt.setString(2, s);
                stmt.executeUpdate();
            }

            con.commit();

            System.out.println("Publish successfully! The postID is "+pID);
            System.out.println("Title: "+ clientInfo.get("title"));
            System.out.println("Content: "+ clientInfo.get("postContent"));
            System.out.println("Types: "+ clientInfo.get("types"));
            System.out.println("City: "+ clientInfo.get("postingCity"));
            System.out.println("Time: "+ getTime());
            System.out.println("By: author ID"+ clientInfo.get("authorID"));

        } catch (SQLException e) {
            System.out.println("Publish Failed!");
            e.printStackTrace();
        }

    }

    public static void reply(Integer level) throws SQLException {
        Scanner in =new Scanner(System.in);
        Hashtable<String,String> hashtable=new Hashtable();

        System.out.println("first author");
        String author_in= in.next();
        while ( !Command_In.check("author.author","author",author_in)) {
            System.out.println("Not a valid author name.");
            System.out.println("first author");
            author_in= in.next();
        }
        hashtable.put("author",author_in);

        System.out.println("postID");
        String pID= in.next();
        while ( !Command_In.check("postid", "post", pID )) {
            System.out.println("Not a valid postID.");
            System.out.println("PostID");
            pID= in.next();
        }
        hashtable.put("postID",pID);

        if (level==1){
            System.out.println("ReplyContent");
            String s4= in.next();
            hashtable.put("replyContent",s4);
            stmt = con.prepareStatement("insert into Reply(postID, replyContent, replyStars, replyAuthor) \n" +
                    "VALUES (?,?,?,?);");
            stmt.setLong(1, Long.parseLong(pID));
            stmt.setString(2, hashtable.get("replyContent"));
            stmt.setInt(3, 0);
            stmt.setString(4, hashtable.get("author"));
            stmt.executeUpdate();

            System.out.println("Reply published successfully!");
            System.out.println("On post: "+ pID);
            System.out.println("Content: "+ hashtable.get("replyContent"));
            System.out.println("Reply stars: 0");
            System.out.println("By: "+ hashtable.get("author"));

        }

        else if (level==2){
            System.out.println("seconderyReplyAuthor");
            String s5= in.next();
            while ( !Command_In.check("author.author","author",s5)) {
                System.out.println("Not a valid author name.");
                System.out.println("seconderyReplyAuthor");
                s5= in.next();
            }
            hashtable.put("seconderyReplyAuthor",s5);

            in.nextLine();
            String fi_re;
            System.out.println("firstReplyContent");
            fi_re=in.nextLine();
            String valid_reply=fi_re.replace("'","''");



            String sql = String.format("select * from reply where replyauthor='%s' and postid='%s' and replycontent='%s';"
                    ,author_in, pID,valid_reply);
            PreparedStatement statement= con.prepareStatement(sql);
            ResultSet resultSet=statement.executeQuery();

            while ( !resultSet.next()) {
                System.out.println("Not a valid reply.");
                System.out.println("firstReplyContent");
                fi_re= in.nextLine();
                valid_reply=fi_re.replace("'","''");
                sql = String.format("select * from reply where replyauthor='%s' and postid='%s' and replycontent='%s';"
                        ,author_in, pID,valid_reply);
                statement= con.prepareStatement(sql);
                resultSet=statement.executeQuery();
            }
            hashtable.put("firstReplyContent",valid_reply);

            String sqll= String.format("select replystars from reply where replycontent='%s';",valid_reply);
            Statement st = con. createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resset = st.executeQuery(sqll);
            resset.next();
            Integer L=resset.getInt("replystars");

            in.nextLine();

            System.out.println("seconderyReplyContent");
            String s4= in.nextLine();
            hashtable.put("SeconderyReplyContent",s4);


            stmt = con.prepareStatement("insert into Reply(postID, replyContent, replyStars, replyAuthor, secondaryReplyContent, secondaryReplyStars, secondaryReplyAuthor) \n" +
                    "VALUES (?,?,?,?,?,?,?);");
            stmt.setLong(1, Long.parseLong(pID));
            stmt.setString(2, hashtable.get("firstReplyContent").replace("''","'"));
            stmt.setInt(3, L);
            stmt.setString(4, hashtable.get("author"));
            stmt.setString(5, hashtable.get("SeconderyReplyContent"));
            stmt.setInt(6, 0);
            stmt.setString(7, hashtable.get("seconderyReplyAuthor"));
            stmt.executeUpdate();

            System.out.println("Secondary reply published successfully!");
            System.out.println("On post: "+ pID);
            System.out.println("First reply: "+ hashtable.get("firstReplyContent").replace("''","'"));
            System.out.println("First reply stars: "+ L);
            System.out.println("Secondary reply: "+ hashtable.get("SeconderyReplyContent"));
            System.out.println("Secondary reply stars: 0");
            System.out.println("By: "+ hashtable.get("seconderyReplyAuthor"));
        }


        con.commit();
    }

    public static void action() throws SQLException {
        Scanner in =new Scanner(System.in);
        Hashtable<String,String> hashtable=new Hashtable();

        System.out.println("author");
        String s2= in.next();
        while ( !Command_In.check("author.author","author",s2)) {
            System.out.println("Not a valid author name.");
            System.out.println("author");
            s2= in.next();
        }
        hashtable.put("author",s2);

        System.out.println("postID");
        String s3= in.next();
        while ( !Command_In.check("postid", "post", s3 )) {
            System.out.println("Not a valid postID.");
            System.out.println("PostID");
            s3= in.next();
        }hashtable.put("postID",s3);

        System.out.println("action (L, S or F)");
        s3= in.next();
        hashtable.put("relation",s3);

        stmt = con.prepareStatement("insert into Action(postID, peopleName, relation) VALUES (?,?,?);");
        stmt.setInt(1,Integer.parseInt( hashtable.get("postID")));
        stmt.setString(2,  hashtable.get("author"));
        stmt.setString(3, hashtable.get("relation"));
        stmt.executeUpdate();
        System.out.println("Action done!");
        con.commit();
    }

    public static void clearDataInTable() {
        Statement stmt0;
        if (con != null) {
            try {
                stmt0 = con.createStatement();
                stmt0.executeUpdate("drop table if exists Post,Author,Reply,cate,follow,Action cascade ;" );
                con.commit();
                stmt0.executeUpdate("create table Author (\n" +
                        "    authorID varchar unique ,\n" +
                        "    author varchar primary key ,\n" +
                        "    authorRegistrationTime varchar,\n" +
                        "    authoPhone varchar\n" +
                        ");\n" +
                        "\n" +
                        "create table Post(\n" +
                        "    authorID varchar not null ,\n" +
                        "    postID int primary key ,\n" +
                        "    title varchar,\n" +
                        "    postContent varchar ,\n" +
                        "    postingCity varchar,\n" +
                        "    postingTime varchar\n" +
                        ");"+
                        "create table cate (\n" +
                        "    postID int not null ,\n" +
                        "    type varchar\n" +
                        "); create table follow(\n" +
                        "    following varchar not null ,\n" +
                        "    follwer varchar not null\n" +
                        ");create table Action(\n" +
                        "    PeopleName varchar not null ,\n" +
                        "    postID int not null,\n" +
                        "    relation varchar\n" +
                        "\n" +
                        ");create table Reply(\n" +
                        "    postID int references post(postID),\n" +
                        "    replyContent varchar,\n" +
                        "    replyStars int,\n" +
                        "    replyAuthor varchar,\n" +
                        "    secondaryReplyContent varchar,\n" +
                        "    secondaryReplyStars int,\n" +
                        "    secondaryReplyAuthor varchar\n" +
                        ");"
                );

                con.commit();
                stmt0.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }





}



