
//批量插入 使用Batch
import com.google.gson.Gson;

import java.io.*;
import java.util.Properties;
import java.sql.*;

public class Initial_Input {
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
    public static int cnt;

    public static Post[] posts;
    public static Replies[] replies;

    public static void Initiliaza_post(){
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader("resource/posts.json"))) {
            posts = gson.fromJson(reader, Post[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("resource/Replies.json"))) {
            replies = gson.fromJson(reader, Replies[].class);
        }catch (  IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final static int Batch=1000;

    //    public static int[] generateID;
    public static void loadReply() throws SQLException {
        cnt=0;
        stmt = con.prepareStatement("insert into Reply(postID, replyContent, replyStars, replyAuthor, secondaryReplyContent, secondaryReplyStars, secondaryReplyAuthor) \n" +
                "VALUES (?,?,?,?,?,?,?);");

        for (Replies r:replies) {
            cnt++;
            stmt.setLong(1, r.getPostID());
            //stmt.setLong(2, r.getRep_id());
            stmt.setString(2, r.getReplyContent());
            stmt.setInt(3, r.getReplyStars());
            stmt.setString(4, r.getReplyAuthor());
            stmt.setString(5, r.getSecondaryReplyContent());
            stmt.setInt(6, r.getSecondaryReplyStars());
            stmt.setString(7, r.getSecondaryReplyAuthor());

            stmt.addBatch();
            if (cnt % Batch==0){
                stmt.executeBatch();
                stmt.clearBatch();
            }

        }stmt.executeBatch();
        stmt.clearBatch();
//        cnt=0;


    }

    public static void loadData() throws SQLException {
        cnt=0;
        stmt = con.prepareStatement("\n" +
                "insert into post (authorID, postID, title, postContent, postingCity, postingTime) \n" +
                "                                VALUES (?,?,?,?,?,?) on conflict (postID) do nothing ;");

        for (Post post : posts) {
            cnt++;

            stmt.setString(1, post.getAuthorID());
            stmt.setLong(2, post.getPostID());
            stmt.setString(3, post.getTitle());
            stmt.setString(4, post.getContent());
            stmt.setString(5, post.getPostingCity());
            stmt.setString(6, post.getPostingTime());
            stmt.addBatch();

            if (cnt % Batch==0){
                stmt.executeBatch();
                stmt.clearBatch();
            }
        }
        stmt.executeBatch();
        stmt.clearBatch();
        cnt=0;


        stmt = con.prepareStatement("insert into cate(postID, type) VALUES (?,?);");
        for (Post post : posts) {
            cnt++;
            for (String category : post.getCategory()) {
                stmt.setLong(1, post.getPostID());
                stmt.setString(2, category);
                stmt.addBatch();
            }
            if (cnt % Batch==0){
                stmt.executeBatch();
            }
        }stmt.executeBatch();
        stmt.clearBatch();
        cnt=0;

        stmt = con.prepareStatement("insert  into Author(authorID, author, authorRegistrationTime, authoPhone)\n" +
                "VALUES (?,?,?,?) on conflict (author) do nothing ;");
        for (Post post : posts) {
            cnt++;

            stmt.setString(1, post.getAuthorID());
            stmt.setString(2, post.getAuthor());
            stmt.setString(3, post.getAuthorRegistrationTime());
            stmt.setString(4, post.getAuthoPhone());
            stmt.addBatch();
            if (cnt % Batch==0){
                stmt.executeBatch();
            }
        }

        stmt.executeBatch();
        stmt.clearBatch();


        //secondry insert
        cnt=0;
        stmt = con.prepareStatement("INSERT INTO Author (author) values (?) on conflict (author) do nothing;");
        for (Post post : posts) {
            cnt++;
//            for (int i = 0; i < Batch; i++) {
            for (String s : post.getAuthorFollowedBy()
            ) {
                if (s != null) {
                    stmt.setString(1, s);
                    stmt.addBatch();
                }
            }
            for (String s : post.getAuthorLiked()
            ) {
                if (s != null) {
                    stmt.setString(1, s);
                    stmt.addBatch();
                }
            }
            for (String s : post.getAuthorFavorite()
            ) {
                if (s != null) {
                    stmt.setString(1, s);
                    stmt.addBatch();
                }
            }
            for (String s : post.getAuthorShared()
            ) {
                stmt.setString(1, s);
                stmt.addBatch();
            }
            if (cnt % Batch==0){
                stmt.executeBatch();
            }

        } stmt.executeBatch();
        stmt.clearBatch();
        con.commit();


        for (Post post : posts) {
            for (String s : post.getAuthorFollowedBy()
            ) {
                stmt= con.prepareStatement(
                        "UPDATE author set authorid= (SELECT new_id FROM (\n" +
                                "  SELECT FLOOR(random() * 999999999999) + 1 AS new_id\n" +
                                "  FROM author\n" +
                                ") temp\n" +
                                "WHERE NOT EXISTS (\n" +
                                "  SELECT authorID FROM author WHERE cast( temp.new_id AS varchar) = authorID\n" +
                                "    )\n" +
                                "LIMIT 1)\n" +
                                "WHERE authorid is null and author.author=?;\n");
                stmt.setString(1, s);
                stmt.executeUpdate();
//                con.commit();

            }
            for (String s : post.getAuthorLiked()
            ) {
                stmt= con.prepareStatement(
                        "UPDATE author set authorid= (SELECT new_id FROM (\n" +
                                "  SELECT FLOOR(random() * 999999999999) + 1 AS new_id\n" +
                                "  FROM author\n" +
                                ") temp\n" +
                                "WHERE NOT EXISTS (\n" +
                                "  SELECT authorID FROM author WHERE cast( temp.new_id AS varchar) = authorID\n" +
                                "    )\n" +
                                "LIMIT 1)\n" +
                                "WHERE authorid is null and author.author=?;\n");
                stmt.setString(1, s);
                stmt.executeUpdate();
//                con.commit();

            }
            for (String s : post.getAuthorFavorite()
            ) {
                stmt= con.prepareStatement(
                        "UPDATE author set authorid= (SELECT new_id FROM (\n" +
                                "  SELECT FLOOR(random() * 999999999999) + 1 AS new_id\n" +
                                "  FROM author\n" +
                                ") temp\n" +
                                "WHERE NOT EXISTS (\n" +
                                "  SELECT authorID FROM author WHERE cast( temp.new_id AS varchar) = authorID\n" +
                                "    )\n" +
                                "LIMIT 1)\n" +
                                "WHERE authorid is null and author.author=?;\n");
                stmt.setString(1, s);
                stmt.executeUpdate();
//                con.commit();
            }
            for (String s : post.getAuthorShared()
            ) {
                stmt= con.prepareStatement(
                        "UPDATE author set authorid= (SELECT new_id FROM (\n" +
                                "  SELECT FLOOR(random() * 999999999999) + 1 AS new_id\n" +
                                "  FROM author\n" +
                                ") temp\n" +
                                "WHERE NOT EXISTS (\n" +
                                "  SELECT authorID FROM author WHERE cast( temp.new_id AS varchar) = authorID\n" +
                                "    )\n" +
                                "LIMIT 1)\n" +
                                "WHERE authorid is null and author.author=?;\n");
                stmt.setString(1, s);
                stmt.executeUpdate();
//                con.commit();

            }

        }con.commit();

        //generate ID for author in follow share like ect.
        cnt=0;
        stmt = con.prepareStatement("INSERT INTO Author (author) values (?) on conflict (author) do nothing;");

        for (Replies r:replies){
//            cnt++;

            stmt.setString(1,r.getReplyAuthor());
            stmt.addBatch();
        }
        for (Replies r:replies){

            stmt.setString(1,r.getSecondaryReplyAuthor());
            stmt.addBatch();
        }

        stmt.executeBatch();
        stmt.clearBatch();
//add reply author into chart

        for (Replies r:replies){
            stmt= con.prepareStatement(
                    "UPDATE author set authorid= (SELECT new_id FROM (\n" +
                            "  SELECT FLOOR(random() * 999999999999) + 1 AS new_id\n" +
                            "  FROM author\n" +
                            ") temp\n" +
                            "WHERE NOT EXISTS (\n" +
                            "  SELECT authorID FROM author WHERE cast( temp.new_id AS varchar) = authorID\n" +
                            "    )\n" +
                            "LIMIT 1)\n" +
                            "WHERE authorid is null and author.author=?;\n");

            stmt.setString(1,r.getReplyAuthor());
            stmt.executeUpdate();
        }
        for (Replies r:replies){
            stmt= con.prepareStatement(
                    "UPDATE author set authorid= (SELECT new_id FROM (\n" +
                            "  SELECT FLOOR(random() * 999999999999) + 1 AS new_id\n" +
                            "  FROM author\n" +
                            ") temp\n" +
                            "WHERE NOT EXISTS (\n" +
                            "  SELECT authorID FROM author WHERE cast( temp.new_id AS varchar) = authorID\n" +
                            "    )\n" +
                            "LIMIT 1)\n" +
                            "WHERE authorid is null and author.author=?;\n");

            stmt.setString(1,r.getSecondaryReplyAuthor());
            stmt.executeUpdate();
        }

        //generate reply author's ID


//            todo:fix the differ digits


        cnt=0;
        stmt = con.prepareStatement("insert into Action(postID, peopleName, relation) VALUES (?,?,?);");
        for (Post post : posts) {
            cnt++;
            for (String s : post.getAuthorFavorite()
            ) {
                stmt.setInt(1, post.getPostID());
                stmt.setString(2, s);
                stmt.setString(3, "F");
                stmt.addBatch();
            }
            for (String s : post.getAuthorShared()
            ) {
                stmt.setInt(1, post.getPostID());
                stmt.setString(2, s);
                stmt.setString(3, "S");
                stmt.addBatch();
            }
            for (String s : post.getAuthorLiked()
            ) {
                stmt.setInt(1, post.getPostID());
                stmt.setString(2, s);
                stmt.setString(3, "L");
                stmt.addBatch();
            }
            if (cnt % Batch==0){
                stmt.executeBatch();
            }

        }
        stmt.executeBatch();
        stmt.clearBatch();

        cnt=0;
        stmt = con.prepareStatement("insert into follow(following, follwer) " +
                "values (?,?);");
        for (Post post : posts) {
            cnt++;
            for (String s : post.getAuthorFollowedBy()
            ) {
                stmt.setString(1, post.getAuthor());
                stmt.setString(2, s);
                stmt.addBatch();
            }
            if (cnt % Batch==0){
                stmt.executeBatch();
            }
        }

        stmt.executeBatch();
        stmt.clearBatch();


        stmt = con.prepareStatement("UPDATE author\n" +
                "SET authorRegistrationTime = (\n" +
                "    SELECT min(postingtime)\n" +
                "    FROM author\n" +
                "    JOIN action ON author.author = action.peoplename\n" +
                "    JOIN post ON action.postid = post.postid\n" +
                ")WHERE authorregistrationtime is null;");
        stmt.executeUpdate();

    }


    public static void add_constrain() throws SQLException {
        stmt=con.prepareStatement(
                "alter table post add constraint publish foreign key (authorID) references Author(authorID);"
                        +"alter table cate add constraint category foreign key (postID)references post(postID);"+
                        "alter table follow add constraint following_relation foreign key (following)references author(author);\n" +
                        "alter table follow add constraint follower_relation foreign key (follwer )references author(author);"
                        +"alter table Action add constraint likePeople foreign key (PeopleName)references author(author);\n" +
                        "alter table Action add constraint likePost foreign key (postID)references post(postID);"+
                        "alter table Action add check ( relation in ('F','S','L'));"+
                        "alter table Reply add constraint rr foreign key (postID)references post(postID);"
        );
        stmt.executeUpdate();

    }


    public static void clearDataInTable() {
        Statement stmt0;
        if (con != null) {
            try {
                stmt0 = con.createStatement();
                stmt0.executeUpdate("drop table if exists Post,Author,Reply,cate,follow,Action cascade ;");
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
                        "    postContent varchar,\n" +
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
                        ");"+
                        "create table Reply(\n" +
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



