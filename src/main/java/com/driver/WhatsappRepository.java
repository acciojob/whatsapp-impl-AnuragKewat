package com.driver;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public boolean createUser(String mobile) {
        if(userMobile.contains(mobile)) return false;
        userMobile.add(mobile);
        return true;
    }
    public Group createGroup(List<User> users) {
        if(users.size()>2) {
            customGroupCount++;
            Group group = new Group("Group " + customGroupCount, users.size());
            groupUserMap.put(group, users);
            adminMap.put(group, users.get(0));
            return group;
        }
        else {
            Group group = new Group(users.get(1).getName(), users.size());
            groupUserMap.put(group, users);
            adminMap.put(group, users.get(0));
            return group;
        }
    }
    public int createMessage(String content) {
        Date instant = Timestamp.from(Instant.now());
        messageId++;
        Message message = new Message(messageId, content, instant);
        return messageId;
    }
    public int sendMessage(Message message, User sender, Group group){
        if(!groupUserMap.containsKey(group)) throw new RuntimeException("Group does not exist");
        for(User user: groupUserMap.get(group)) {
            if(user.equals(sender)) {
                senderMap.put(message, sender);
                List<Message> currentMessage = groupMessageMap.get(group);
                currentMessage.add(message);
                groupMessageMap.put(group, currentMessage);
                return currentMessage.size();
            }
        }
        throw new RuntimeException("You are not allowed to send message");
    }
    public String changeAdmin(User approver, User user, Group group) {
        if(!groupUserMap.containsKey(group)) throw new RuntimeException("Group does not exist");
        if(!adminMap.get(group).equals(approver)) throw new RuntimeException("Approver does not have rights");
        for(User valid: groupUserMap.get(group)) {
            if(user.equals(valid)) {
                adminMap.put(group, user);
                return "SUCCESS";
            }
        }
        throw new RuntimeException("User is not a participant");
    }
    public int removeUser(User user) {
        int overAllMessage = 0;
        for(Group group: groupMessageMap.keySet()) {
            overAllMessage += groupMessageMap.get(group).size();
        }
        for(Group group: groupUserMap.keySet()) {
            for(User validUser: groupUserMap.get(group)) {
                if(validUser.equals(user)) {
                    if(adminMap.containsKey(validUser)) {
                        throw new RuntimeException("Cannot remove admin");
                    }
                    groupUserMap.get(group).remove(validUser);
                    int previous = groupMessageMap.get(group).size();
                    for(Message message: senderMap.keySet()) {
                        if(senderMap.get(message).equals(validUser)) {
                            groupMessageMap.get(group).remove(message);
                            senderMap.remove(message);
                        }
                    }
                    int current = groupMessageMap.get(group).size();
                    return groupUserMap.get(group).size() + current + overAllMessage-(previous-current);
                }
            }
        }
        throw new RuntimeException("User not found");
    }
    public String findMessage(Date start, Date end, int K) {
        PriorityQueue<Message> pq = new PriorityQueue<>();
        for(Message message: senderMap.keySet()) {
            if((message.getTimestamp().compareTo(start))>1 && message.getTimestamp().compareTo(end)<1) {
                pq.add(message);
            }
        }
        if(pq.size()<K) throw new RuntimeException("K is greater than the number of messages");
        for(int i=0;i<pq.size()-K;i++) {
            pq.remove();
        }
        return pq.peek().getContent();
    }
}
