package com.dev;
import com.dev.objects.MessageObject;
import com.dev.objects.UserObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Persist {
    private Connection connection;

    @PostConstruct
    public void createConnectionToDatabase () {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/messagesProject", "root", "?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTokenByUsernameAndPassword(String username, String password) {
         String token = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (4 <= resultSet.getInt("tries"))
                    resultSet.updateBoolean("isBlocked", true);

                if (Objects.equals(resultSet.getString("password"), password)) {
                    if( !resultSet.getBoolean("isBlocked") )
                        token = resultSet.getString("token");
                } else {
                   resultSet.updateInt("tries", resultSet.getInt("tries") + 1);
                   resultSet.updateRow();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    public boolean createAccount(UserObject user) {
        boolean success = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "INSERT INTO users (username, password, token) VALUES (?,?,?)"
            );
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getToken());
            if (!this.isUsernameTaken(user.getUsername())) {
                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean isUsernameTaken(String username) {
        boolean success = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ? ");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            success = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }


    private Integer getUserIdByToken(String token) {
        Integer userID = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE token = ?");
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userID = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userID;
    }


    private Integer getUserIdByUsername(String username) {
        Integer userID = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userID = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userID;
    }

    public List<MessageObject> getReceivedMessagesByToken(String token) {
        List<MessageObject> messages = new ArrayList<>();
        try {
            Integer userID = getUserIdByToken(token);
            if(userID != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "SELECT * FROM " +
                                "messages m INNER JOIN users u ON m.senderId = u.id " +
                                "WHERE receiverId = ? ORDER BY messageId DESC");
                preparedStatement.setInt(1, userID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    messages.add(new MessageObject(
                            resultSet.getInt("messageId"),
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getString("u.username"),
                            resultSet.getString("readDate") != null,
                            resultSet.getString("sendDate")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public boolean isBlocked(String username){
        if(isUsernameTaken(username)) {
            try {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "SELECT * FROM users WHERE username =?");
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next())
                    return resultSet.getBoolean("isBlocked");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteMessage(int messageId, String token) {
        boolean success = false;
        try{
            Integer userID = getUserIdByToken(token);
            if(userID != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "DELETE FROM messages WHERE messageId = ? and receiverId = ?"
                );
                preparedStatement.setInt(1, messageId);
                preparedStatement.setInt(2, userID);
                preparedStatement.executeUpdate();
                success = true;
            }
        }catch (SQLException e) { e.printStackTrace(); }

        return success;
    }

    public boolean readMessage(int messageId, String token) {
        boolean success = false;
        try{
            Integer userId = getUserIdByToken(token);
            if(userId != null){
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE messages SET readDate = CURRENT_TIMESTAMP WHERE messageId = ? AND receiverId = ? "
                );
                preparedStatement.setInt(1, messageId);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return success;
    }

    public List<String> getUsers(String token) {
        List<String> users = new ArrayList<>();

        try{
           Integer userId = getUserIdByToken(token);
           if(userId != null){
               PreparedStatement preparedStatement = connection.prepareStatement(
                       "SELECT * FROM users WHERE id <> ? "
               );
               preparedStatement.setInt(1, userId);
               ResultSet resultSet = preparedStatement.executeQuery();
               while(resultSet.next()){
                   users.add(resultSet.getString("username"));
               }
           }
        } catch ( SQLException e) { e.printStackTrace(); }
        return users;
    }


        public boolean sendMessage(String senderToken, String receiverUsername, String title, String content) {
        boolean success = false;
        try {
            Integer senderID = this.getUserIdByToken(senderToken);
            Integer receiverID = this.getUserIdByUsername(receiverUsername);
            if (senderID != null && receiverID != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "INSERT INTO messages (senderId, receiverId, title, content) VALUES (?, ?, ?, ?)");
                preparedStatement.setInt(1, senderID);
                preparedStatement.setInt(2, receiverID);
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, content);
                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }
}
