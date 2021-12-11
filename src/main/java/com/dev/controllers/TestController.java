package com.dev.controllers;
import com.dev.objects.MessageObject;
import com.dev.objects.UserObject;
import com.dev.Persist;
import com.dev.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;


@RestController
public class TestController {
    @Autowired
    private Persist persist;

    @PostConstruct
    private void init () {
        persist.createConnectionToDatabase();
    }

    @RequestMapping("sign-in")
    public String signIn (String username, String password) {
        return persist.getTokenByUsernameAndPassword(username,password);
    }

    @RequestMapping("create-account")
    public boolean createAccount (String username, String password) {
        boolean success = false;
        boolean alreadyExists = persist.getTokenByUsernameAndPassword(username, password) != null;
        if (!alreadyExists) {
            UserObject userObject = new UserObject();
            userObject.setUsername(username);
            userObject.setPassword(password);
            String hash = Utils.createHash(username, password);
            userObject.setToken(hash);
            success = persist.createAccount(userObject);
        }

        return success;
    }

    @RequestMapping("is-username-taken")
    public boolean isUsernameTaken(String username){
        return persist.isUsernameTaken(username);
    }

    @RequestMapping("is-blocked")
    public boolean isBlocked(String username){
        return persist.isBlocked(username);
    }

    @RequestMapping("get-received-messages")
    public List<MessageObject> getReceivedMessages (String token) {
        return persist.getReceivedMessagesByToken(token);
    }

    @RequestMapping("get-users")
    public List<String> getUsers(String token){
        return persist.getUsers(token);
    }

    @RequestMapping("send-message")
    public boolean addPost (String senderToken, String receiverUsername, String title, String content) {
        return persist.sendMessage(senderToken, receiverUsername, title, content);
    }

    @RequestMapping("delete-message")
    public boolean deletePost(int messageId, String token){
        return persist.deleteMessage(messageId, token);
    }

    @RequestMapping("read-message")
    public boolean readMessage(int messageId, String token){
        return persist.readMessage(messageId, token);
    }

}
