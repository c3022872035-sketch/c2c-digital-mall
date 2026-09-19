package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.MessageDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.MessageModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageDao messageDao;

    @Resource
    private UserDao userDao;

    @Resource
    private IdleItemDao idleItemDao;

    /**
     * 增加一条留言
     * @param messageModel
     * @return
     */
    public boolean addMessage(MessageModel messageModel){
        return messageDao.insert(messageModel)==1;
    }

    /**
     * 删除一条留言，未做用户身份验证
     * @param id
     * @return
     */
    public boolean deleteMessage(Long id){
        return messageDao.deleteByPrimaryKey(id)==1;
    }

    /**
     * 获取一条留言
     * @param id
     * @return
     */
    public MessageModel getMessage(Long id){
        return messageDao.selectByPrimaryKey(id);
    }

    /**
     * 获取一个用户收到的所有留言，未做分页查询
     * 同时查询出用户的信息和闲置的信息
     * userId建索引
     * @param userId
     * @return
     */
    public List<MessageModel> getAllMyMessage(Long userId){
        List<MessageModel> list=messageDao.getMyMessage(userId);
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(MessageModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(MessageModel i:list){
                i.setFromU(map.get(i.getUserId()));
            }

            List<Long> idleIdList=new ArrayList<>();
            for(MessageModel i:list){
                idleIdList.add(i.getIdleId());
            }
            List<IdleItemModel> idleList=idleItemDao.findIdleByList(idleIdList);
            Map<Long,IdleItemModel> idleMap=new HashMap<>();
            for(IdleItemModel idle:idleList){
                idleMap.put(idle.getId(),idle);
            }
            for(MessageModel i:list){
                i.setIdle(idleMap.get(i.getIdleId()));
            }
        }
        return list;
    }

    /**
     * 查询一个闲置下的所有留言，未做分页
     * 同时查出发送者和接收者的信息
     * idleId建索引
     * @param idleId
     * @return
     */
    public List<MessageModel> getAllIdleMessage(Long idleId){
        List<MessageModel> list=messageDao.getIdleMessage(idleId);
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(MessageModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(MessageModel i:list){
                i.setFromU(map.get(i.getUserId()));
            }
            Map<Long,MessageModel> mesMap=new HashMap<>();
            for(MessageModel i:list){
                mesMap.put(i.getId(),i);
            }
            for(MessageModel i:list){
                MessageModel toM=new MessageModel();
                UserModel toU=new UserModel();
                if(i.getToMessage()!=null){
                    toM.setContent(mesMap.get(i.getToMessage()).getContent());
                    toU.setNickname(map.get(i.getToUser()).getNickname());
                }
                i.setToM(toM);
                i.setToU(toU);
            }
        }
        return list;
    }

    // 实现统计未读
    public int getUnreadCount(Long userId) {
        return messageDao.getUnreadCount(userId);
    }

    // 实现获取聊天记录
    public List<MessageModel> getChatHistory(Long userId, Long targetUserId, Long idleId) {
        // 1. 先把对方发给我的未读消息标记为已读
        messageDao.updateMessageStatus(userId, targetUserId, idleId);

        // 2. 再查询记录
        List<MessageModel> list = messageDao.getChatHistory(userId, targetUserId, idleId);
        for (MessageModel m : list) {
            m.setFromU(userDao.selectByPrimaryKey(m.getUserId()));
            m.setToU(userDao.selectByPrimaryKey(m.getToUser()));
        }
        return list;
    }

    // 实现获取会话列表（这是比较复杂的逻辑，需要去重）
    @Override
    public List<MessageModel> getChatList(Long userId) {
        List<MessageModel> allMessages = messageDao.getAllMyRelatedMessages(userId);

        Map<String, MessageModel> chatMap = new LinkedHashMap<>(); // 使用LinkedHashMap保持时间顺序

        for (MessageModel msg : allMessages) {
            Long otherPersonId = msg.getUserId().equals(userId) ? msg.getToUser() : msg.getUserId();
            Long idleId = msg.getIdleId() == null ? 0L : msg.getIdleId();

            String uniqueKey = otherPersonId + "_" + idleId;

            if (!chatMap.containsKey(uniqueKey)) {
                // 填充信息
                msg.setFromU(userDao.selectByPrimaryKey(msg.getUserId()));
                msg.setToU(userDao.selectByPrimaryKey(msg.getToUser()));
                if (idleId != 0L) {
                    msg.setIdle(idleItemDao.selectByPrimaryKey(idleId));
                }

                int count = messageDao.countUnread(userId, otherPersonId, idleId);
                msg.setUnreadCount(count);

                chatMap.put(uniqueKey, msg);
            }
        }
        return new ArrayList<>(chatMap.values());
    }
}
