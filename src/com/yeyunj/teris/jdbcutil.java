package com.yeyunj.teris;

import java.sql.*;

public class jdbcutil {
    public static class ErrorCode{
        public static int OK=0;
        public static int SQL_ERROR=1;
        public static int LOGIN_ERROR=2;
        public static int NO_SUCH_USER=3;

        private int code=OK;
        private String message=null;
        public ErrorCode(int code, String message){
            this.code = code;
            this.message = message;
        }
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return message;
        }

        public static ErrorCode generateOk(){
            return new ErrorCode(OK,null);
        }
    }




    private static final String tableName="USER";
    //创建主表
    private static final String createTableStr="CREATE TABLE "+tableName+" (uid INTEGER PRIMARY KEY,name TEXT,account TEXT,passwdhash TEXT)";



    private Connection con=null;

    String dbPath=null;

    public jdbcutil(String dbPath) {
        this.dbPath = dbPath;

        try {
            this.con=createRssDBFile(dbPath);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        createTable(this.con,createTableStr);

//        insertUser("大傻逼","abcdefg");
    }

    /**
     * 生成一个DB文件  （创建连接）
     * @param filePath 生成文件夹的路径  如：F:/fileTest/aabb.db
     * @return 当前DB文件的连接通道
     */
    private Connection createRssDBFile(String filePath) throws ClassNotFoundException,SQLException {
        Connection con=null;

        Class.forName("org.sqlite.JDBC");
        //创建了一个sqlite的  .db文件
        con = DriverManager.getConnection("jdbc:sqlite:"+filePath);

        return con;
    }


    private ErrorCode createTable(Connection con,String createTableSql){
        try {
            Statement stat = con.createStatement();
//            if(tableName != null && !"".equals(tableName)){
//                stat.executeUpdate("drop table if exists "+tableName+";");
//            }
            //创建表
            stat.executeUpdate(createTableSql);
        } catch (SQLException e) {
            return new ErrorCode(ErrorCode.SQL_ERROR,"创建table错误");
        }
        return ErrorCode.generateOk();
    }

    public ErrorCode close(){
        try {
            con.close();
        } catch (SQLException e) {
            return new ErrorCode(ErrorCode.SQL_ERROR,"关闭数据库失败");
        }
        return ErrorCode.generateOk();
    }

    public ErrorCode hasUserByName(String username){
        try {
            PreparedStatement pre=con.prepareStatement(searchUserByNameStr);
            pre.setString(1,username);
            ResultSet res=pre.executeQuery();
            if(res.next()){
                return new ErrorCode(ErrorCode.OK,null);
            }
            else {
                return new ErrorCode(ErrorCode.OK,null);
            }
        } catch (SQLException e) {
//            throw new RuntimeException(e);
            return new ErrorCode(ErrorCode.SQL_ERROR,"关闭数据库失败");
        }
    }
}
