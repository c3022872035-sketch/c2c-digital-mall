package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.IdleItemHistoryDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.IdleItemHistoryModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.utils.FileHashUtil;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.*;

@Service
public class IdleItemServiceImpl implements IdleItemService {

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private UserDao userDao;

    @Resource
    private IdleItemHistoryDao idleItemHistoryDao;

    /**
     * 发布闲置
     * @param idleItemModel
     * @return
     */
    public boolean addIdleItem(IdleItemModel idleItemModel) {
        return idleItemDao.insert(idleItemModel) == 1;
    }

    /**
     * 查询闲置信息，同时查出发布者的信息
     * @param id
     * @return
     */
    public IdleItemModel getIdleItem(Long id) {
        IdleItemModel idleItemModel=idleItemDao.selectByPrimaryKey(id);
        if(idleItemModel!=null){
            idleItemModel.setUser(userDao.selectByPrimaryKey(idleItemModel.getUserId()));
        }
        return idleItemModel;
    }

    /**
     * 查询用户发布的所有闲置
     * user_id建索引
     * @param userId
     * @return
     */
    public List<IdleItemModel> getAllIdleItem(Long userId) {
        return idleItemDao.getAllIdleItem(userId);
    }

    /*  搜索，分页  */
    @Override
    public PageVo<IdleItemModel> findIdleItem(String findValue, Integer sortType, int page, int nums) {
        // 1. 调用 DAO 查询 (传入排序参数)
        List<IdleItemModel> list = idleItemDao.findIdleItem(findValue, sortType, (page - 1) * nums, nums);

        // 2. 填充发布者信息
        if (list.size() > 0) {
            List<Long> idList = new ArrayList<>();
            for (IdleItemModel i : list) {
                idList.add(i.getUserId());
            }
            List<UserModel> userList = userDao.findUserByList(idList);
            Map<Long, UserModel> map = new HashMap<>();
            for (UserModel user : userList) {
                map.put(user.getId(), user);
            }
            for (IdleItemModel i : list) {
                i.setUser(map.get(i.getUserId()));
            }
        }

        // 3. 统计总数
        int count = idleItemDao.countIdleItem(findValue);

        return new PageVo<>(list, count);
    }

    // 后台专用 (Status=1)，支持ID、用户等搜索
    public PageVo<IdleItemModel> findIdleItemForAdmin(String findValue, int page, int nums) {
        List<IdleItemModel> list = idleItemDao.findIdleItemForAdmin(findValue, (page - 1) * nums, nums);

        // 同样需要填充用户信息
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }

        int count = idleItemDao.countIdleItemForAdmin(findValue);
        return new PageVo<>(list, count);
    }


    /*分类查询，分页*/
    @Override
    public PageVo<IdleItemModel> findIdleItemByLabel(int idleLabel, Integer sortType, int page, int nums) {
        // 1. 调用 DAO 查询 (传入排序参数)
        List<IdleItemModel> list = idleItemDao.findIdleItemByLabel(idleLabel, sortType, (page - 1) * nums, nums);

        // 2. 填充发布者信息
        if (list.size() > 0) {
            List<Long> idList = new ArrayList<>();
            for (IdleItemModel i : list) {
                idList.add(i.getUserId());
            }
            List<UserModel> userList = userDao.findUserByList(idList);
            Map<Long, UserModel> map = new HashMap<>();
            for (UserModel user : userList) {
                map.put(user.getId(), user);
            }
            for (IdleItemModel i : list) {
                i.setUser(map.get(i.getUserId()));
            }
        }

        // 3. 统计总数
        int count = idleItemDao.countIdleItemByLabel(idleLabel);

        return new PageVo<>(list, count);
    }

    @Override
    public PageVo<IdleItemModel> adminGetIdleList(int status, int page, int nums) {
        // 1. 调用 DAO 查询指定状态的商品列表
        List<IdleItemModel> list = idleItemDao.getIdleItemByStatus(status, (page - 1) * nums, nums);

        // 2. 填充发布者用户信息 (头像、昵称等)
        if (list.size() > 0) {
            List<Long> idList = new ArrayList<>();
            for (IdleItemModel i : list) {
                idList.add(i.getUserId());
            }
            List<UserModel> userList = userDao.findUserByList(idList);
            Map<Long, UserModel> map = new HashMap<>();
            for (UserModel user : userList) {
                map.put(user.getId(), user);
            }
            for (IdleItemModel i : list) {
                i.setUser(map.get(i.getUserId()));
            }
        }

        // 3. 查询总数用于分页
        int count = idleItemDao.countIdleItemByStatus(status);

        return new PageVo<>(list, count);
    }

    /**
     * 更新闲置信息
     * @param idleItemModel
     * @return
     */
    @Override
    @Transactional // 开启事务，保证备份和更新原子性
    public boolean updateIdleItem(IdleItemModel idleItemModel) {
        // 1. 查出数据库中当前的旧数据
        IdleItemModel oldItem = idleItemDao.selectByPrimaryKey(idleItemModel.getId());

        // 2. 如果存在旧数据，且状态不是"已删除"，则进行备份
        if (oldItem != null) {
            IdleItemHistoryModel history = new IdleItemHistoryModel();
            history.setIdleId(oldItem.getId());
            history.setIdleName(oldItem.getIdleName());
            history.setIdleDetails(oldItem.getIdleDetails());
            history.setIdlePrice(oldItem.getIdlePrice());
            history.setPictureList(oldItem.getPictureList());
            history.setIdlePlace(oldItem.getIdlePlace());
            history.setIdleLabel(oldItem.getIdleLabel());
            history.setIdleStatus(oldItem.getIdleStatus());
            history.setProofImage(oldItem.getProofImage());
            history.setResourcePath(oldItem.getResourcePath());
            history.setOriginalFileName(oldItem.getOriginalFileName());
            history.setUnzipPassword(oldItem.getUnzipPassword());
            history.setCreateTime(new Date());

            idleItemHistoryDao.insert(history);
        }

        // 3. 执行更新
        return idleItemDao.updateByPrimaryKeySelective(idleItemModel) == 1;
    }

    //获取某商品的历史记录
    @Override
    public List<IdleItemHistoryModel> getHistoryList(Long idleId) {
        return idleItemHistoryDao.listByIdleId(idleId);
    }

    // 根据不同的状态查找物品
    @Override
    public PageVo<IdleItemModel> findIdleItem1(String findValue, int status, int page, int nums) {
        List<IdleItemModel> list = idleItemDao.findIdleItem1(findValue, status, (page - 1) * nums, nums);
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }
        int count = idleItemDao.countIdleItemByStatus(status);
        return new PageVo<>(list,count);
    }

    /**
     * 获取各个商品分类的统计数据
     * @return 包含分类名称和对应数量的列表
     */
    @Override
    public List<Map<String, Object>> getCategoryStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 从数据库获取原始统计数据（包含 category_id 和 count）
        List<Map<String, Object>> rawData = idleItemDao.countItemsByCategory();

        Map<Integer, String> categoryNames = new HashMap<>();
        categoryNames.put(1, "电子书籍");
        categoryNames.put(2, "软件工具");
        categoryNames.put(3, "课程学习");
        categoryNames.put(4, "设计模版");
        categoryNames.put(5, "其他");
        categoryNames.put(10, "公告展示");

        for (Map<String, Object> item : rawData) {
            Object categoryObj = item.get("category");
            if (categoryObj != null) {
                Integer categoryId = Integer.parseInt(categoryObj.toString());
                item.put("categoryName", categoryNames.getOrDefault(categoryId, "其他"));
                result.add(item);
            }
        }

        return result;
    }
    @Override
    public PageVo<IdleItemModel> getAuditList(Integer status, String searchValue, int page, int nums) {
        // 1. 查询商品列表
        List<IdleItemModel> list = idleItemDao.getAuditList(status, searchValue, (page - 1) * nums, nums);

        // 2. 填充发布者信息 (User)
        if (list.size() > 0) {
            List<Long> idList = new ArrayList<>();
            for (IdleItemModel i : list) {
                idList.add(i.getUserId());
            }
            // 批量查询用户
            List<UserModel> userList = userDao.findUserByList(idList);
            Map<Long, UserModel> map = new HashMap<>();
            for (UserModel user : userList) {
                map.put(user.getId(), user);
            }
            // 匹配填充
            for (IdleItemModel i : list) {
                i.setUser(map.get(i.getUserId()));
            }
        }

        // 3. 查询总数
        int count = idleItemDao.countAuditList(status, searchValue);
        return new PageVo<>(list, count);
    }

    @Override
    public boolean checkFileHashExists(String fileHash) {
        // 调用 Dao 层判断数量是否大于 0
        return idleItemDao.countByFileHash(fileHash) > 0;
    }

    /**
     * @param userFilePath 文件存储根路径（来自 application.properties）
     * @return 修复成功的记录数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int repairFileHash(String userFilePath) {
        // 1. 从数据库中查询出所有需要修复的商品（resource_path不为空且file_hash为空的）
        List<IdleItemModel> missingList = idleItemDao.findItemsMissingHash();
        int count = 0;

        if (missingList == null || missingList.isEmpty()) {
            return 0;
        }

        for (IdleItemModel item : missingList) {
            try {
                // 2. 构造物理文件对象
                File file = new File(userFilePath + File.separator + item.getResourcePath());

                if (file.exists()) {
                    // 3. 判断是否为 ZIP 文件
                    boolean isZip = item.getResourcePath().toLowerCase().endsWith(".zip");

                    // 4. 调用归一化计算工具类
                    // 该方法内部逻辑：若是ZIP则先执行 zipFile.setComment("")，再计算 SHA-256
                    String cleanHash = FileHashUtil.getCleanSHA256(file, isZip);

                    if (cleanHash != null) {
                        // 5. 构造更新参数对象
                        IdleItemModel updateParam = new IdleItemModel();
                        updateParam.setId(item.getId());
                        updateParam.setFileHash(cleanHash);

                        // 6. 执行数据库更新（必须确保 IdleItemDao.xml 中已配置 file_hash 字段）
                        if (idleItemDao.updateByPrimaryKeySelective(updateParam) > 0) {
                            count++;
                            System.out.println(">>> 成功修复 ID " + item.getId() + " 的哈希值: " + cleanHash);
                        }
                    }
                } else {
                    System.err.println(">>> 修复失败：商品 ID " + item.getId() + " 对应的物理文件不存在 [" + file.getAbsolutePath() + "]");
                }
            } catch (Exception e) {
                System.err.println(">>> 修复商品 ID " + item.getId() + " 时发生异常: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return count;
    }

    // 辅助方法：计算本地文件的 SHA-256
    private String calculateFileSHA256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAllItemStats() {
        idleItemDao.syncSalesCount();
        idleItemDao.syncAvgRating();
    }

    @Override
    public List<IdleItemModel> getHotItems(int nums) {
        // 1. 调用 DAO 查询热门数据
        List<IdleItemModel> list = idleItemDao.findHotRecommendations(nums);
        // 2. 调用类中已有的辅助方法，解析图片 JSON 并填充用户信息
        fillUserInfo(list);
        return list;
    }

    @Override
    public List<IdleItemModel> getRelatedItems(int label, long currentId, int nums) {
        // 1. 调用 DAO 查询同类数据
        List<IdleItemModel> list = idleItemDao.findRelatedRecommendations(label, currentId, nums);
        // 2. 同样填充图片和用户信息
        fillUserInfo(list);
        return list;
    }

    /**
     * 私有辅助方法：批量填充商品的用户信息和预览图
     */
    private void fillUserInfo(List<IdleItemModel> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        // 1. 收集所有不重复的用户ID
        List<Long> idList = new ArrayList<>();
        for (IdleItemModel i : list) {
            if (!idList.contains(i.getUserId())) {
                idList.add(i.getUserId());
            }
        }

        // 2. 批量从数据库查询用户信息
        List<UserModel> userList = userDao.findUserByList(idList);

        // 3. 将用户列表转为 Map，方便快速查找
        Map<Long, UserModel> userMap = new HashMap<>();
        for (UserModel user : userList) {
            userMap.put(user.getId(), user);
        }

        // 4. 遍历商品列表，填充用户对象和图片URL
        for (IdleItemModel item : list) {
            // 填充用户
            item.setUser(userMap.get(item.getUserId()));

            // 解析图片列表 JSON
            if (item.getPictureList() != null && !item.getPictureList().isEmpty()) {
                try {
                    // 使用 FastJSON 解析
                    List<String> pics = com.alibaba.fastjson.JSON.parseArray(item.getPictureList(), String.class);
                    if (pics != null && !pics.isEmpty()) {
                        // 设置虚拟字段 imgUrl 为第一张图
                        item.setImgUrl(pics.get(0));
                    }
                } catch (Exception e) {
                    System.err.println(">>> 商品 ID " + item.getId() + " 图片解析失败: " + e.getMessage());
                    item.setImgUrl(""); // 解析失败设为空字符串，防止前端崩溃
                }
            }
        }
    }
}
