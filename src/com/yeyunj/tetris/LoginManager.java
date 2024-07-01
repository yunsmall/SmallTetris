package com.yeyunj.tetris;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginManager {
    private Connection connection;

    // SQL queries
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                passwordHash TEXT NOT NULL,
                highScore INTEGER DEFAULT 0
            );
            """;
    private static final String INSERT_USER_SQL = "INSERT INTO users (username, passwordHash) VALUES (?, ?)";
    private static final String SELECT_USER_COUNT_SQL = "SELECT COUNT(*) FROM users WHERE username = ? AND passwordHash = ?";
    private static final String UPDATE_USERNAME_SQL = "UPDATE users SET username = ? WHERE id = ?";
    private static final String UPDATE_PASSWORD_SQL = "UPDATE users SET passwordHash = ? WHERE id = ?";
    private static final String SELECT_HIGH_SCORE_SQL = "SELECT highScore FROM users WHERE id = ?";
    private static final String UPDATE_HIGH_SCORE_SQL = "UPDATE users SET highScore = ? WHERE id = ?";
    private static final String SELECT_TOP_USERS_SQL = "SELECT username FROM users ORDER BY highScore DESC LIMIT ?";
    private static final String SELECT_UID_SQL = "SELECT id FROM users WHERE username = ?";
    private static final String SELECT_USERNAME_BY_UID_SQL = "SELECT username FROM users WHERE id = ?";

    public LoginManager(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        }
    }

    public boolean register(String username, String password, ErrorCode errorCode) {
        try {
            String passwordHash = hashPassword(password);
            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_USER_SQL)) {
                pstmt.setString(1, username);
                pstmt.setString(2, passwordHash);
                pstmt.executeUpdate();
                errorCode.setCode(ErrorCode.OK);
                return true;
            } catch (SQLException e) {
                errorCode.setCode(ErrorCode.DATABASE_ERROR);
                errorCode.setMessage("注册失败：用户名已存在");
                return false;
            }
        } catch (Exception e) {
            errorCode.setCode(ErrorCode.HASH_ERROR);
            errorCode.setMessage("注册失败：密码处理错误");
            return false;
        }
    }

    public boolean login(String username, String password, ErrorCode errorCode) {
        try {
            String passwordHash = hashPassword(password);
            try (PreparedStatement pstmt = connection.prepareStatement(SELECT_USER_COUNT_SQL)) {
                pstmt.setString(1, username);
                pstmt.setString(2, passwordHash);
                ResultSet rs = pstmt.executeQuery();
                if (rs.getInt(1) > 0) {
                    errorCode.setCode(ErrorCode.OK);
                    return true;
                } else {
                    errorCode.setCode(ErrorCode.LOGIN_FAILED);
                    errorCode.setMessage("登录失败：用户名或密码错误");
                    return false;
                }
            } catch (SQLException e) {
                errorCode.setCode(ErrorCode.DATABASE_ERROR);
                errorCode.setMessage("登录失败：数据库错误");
                return false;
            }
        } catch (Exception e) {
            errorCode.setCode(ErrorCode.HASH_ERROR);
            errorCode.setMessage("登录失败：密码处理错误");
            return false;
        }
    }

    public int getUID(String username, ErrorCode errorCode) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_UID_SQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                errorCode.setCode(ErrorCode.OK);
                return rs.getInt("id");
            } else {
                errorCode.setCode(ErrorCode.NOT_FOUND);
                errorCode.setMessage("未找到用户");
                return -1;
            }
        } catch (SQLException e) {
            errorCode.setCode(ErrorCode.DATABASE_ERROR);
            errorCode.setMessage("查询失败：数据库错误");
            return -1;
        }
    }

    public String getUsernameByUID(int uid, ErrorCode errorCode) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_USERNAME_BY_UID_SQL)) {
            pstmt.setInt(1, uid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                errorCode.setCode(ErrorCode.OK);
                return rs.getString("username");
            } else {
                errorCode.setCode(ErrorCode.NOT_FOUND);
                errorCode.setMessage("未找到用户");
                return null;
            }
        } catch (SQLException e) {
            errorCode.setCode(ErrorCode.DATABASE_ERROR);
            errorCode.setMessage("查询失败：数据库错误");
            return null;
        }
    }

    public boolean updateUsername(int uid, String newUsername, ErrorCode errorCode) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_USERNAME_SQL)) {
            pstmt.setString(1, newUsername);
            pstmt.setInt(2, uid);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                errorCode.setCode(ErrorCode.OK);
                return true;
            } else {
                errorCode.setCode(ErrorCode.NOT_FOUND);
                errorCode.setMessage("未找到用户");
                return false;
            }
        } catch (SQLException e) {
            errorCode.setCode(ErrorCode.DATABASE_ERROR);
            errorCode.setMessage("更新失败：数据库错误");
            return false;
        }
    }

    public boolean updatePassword(int uid, String newPassword, ErrorCode errorCode) {
        try {
            String passwordHash = hashPassword(newPassword);
            try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_PASSWORD_SQL)) {
                pstmt.setString(1, passwordHash);
                pstmt.setInt(2, uid);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    errorCode.setCode(ErrorCode.OK);
                    return true;
                } else {
                    errorCode.setCode(ErrorCode.NOT_FOUND);
                    errorCode.setMessage("未找到用户");
                    return false;
                }
            } catch (SQLException e) {
                errorCode.setCode(ErrorCode.DATABASE_ERROR);
                errorCode.setMessage("更新失败：数据库错误");
                return false;
            }
        } catch (Exception e) {
            errorCode.setCode(ErrorCode.HASH_ERROR);
            errorCode.setMessage("更新失败：密码处理错误");
            return false;
        }
    }

    public int getHighScore(int uid, ErrorCode errorCode) {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_HIGH_SCORE_SQL)) {
            pstmt.setInt(1, uid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                errorCode.setCode(ErrorCode.OK);
                return rs.getInt("highScore");
            } else {
                errorCode.setCode(ErrorCode.NOT_FOUND);
                errorCode.setMessage("未找到用户");
                return -1;
            }
        } catch (SQLException e) {
            errorCode.setCode(ErrorCode.DATABASE_ERROR);
            errorCode.setMessage("查询失败：数据库错误");
            return -1;
        }
    }

    public boolean setHighScore(int uid, int score, ErrorCode errorCode) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_HIGH_SCORE_SQL)) {
            pstmt.setInt(1, score);
            pstmt.setInt(2, uid);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                errorCode.setCode(ErrorCode.OK);
                return true;
            } else {
                errorCode.setCode(ErrorCode.NOT_FOUND);
                errorCode.setMessage("未找到用户");
                return false;
            }
        } catch (SQLException e) {
            errorCode.setCode(ErrorCode.DATABASE_ERROR);
            errorCode.setMessage("更新失败：数据库错误");
            return false;
        }
    }

    public List<String> getTopNUsers(int n, ErrorCode errorCode) {
        List<String> topUsers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_TOP_USERS_SQL)) {
            pstmt.setInt(1, n);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                topUsers.add(rs.getString("username"));
            }
            errorCode.setCode(ErrorCode.OK);
        } catch (SQLException e) {
            errorCode.setCode(ErrorCode.DATABASE_ERROR);
            errorCode.setMessage("查询失败：数据库错误");
        }
        return topUsers;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码处理失败");
        }
    }

    public static class ErrorCode {
        public static final int OK = 0;
        public static final int DATABASE_ERROR = 1;
        public static final int LOGIN_FAILED = 2;
        public static final int NOT_FOUND = 3;
        public static final int HASH_ERROR = 4;

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
