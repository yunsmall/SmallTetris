package com.yeyunj.tetris;

import javax.swing.*;
import java.sql.SQLException;

public class Tetris {
    private static final String database_path="data.db";

    UserData userData=null;

    LoginManager loginManager;

    private Tetris(){
        JFrame loginFrame=null;
        try{
            loginManager=new LoginManager(database_path);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        loginFrame=LoginFrame.genLoginJFrame(this);
        loginFrame.setVisible(true);
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public void updateMaxScoreAndWriteToDatabase(int new_max_score , LoginManager.ErrorCode ec){
        //常试写入最高分
        loginManager.setHighScore(userData.getUid(), new_max_score, ec);
        if(ec.getCode()== LoginManager.ErrorCode.OK){
            userData.setMax_score(new_max_score);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Tetris();
            }
        });
    }
}
