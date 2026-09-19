package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.CommentDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.CommentModel;
import com.second.hand.trading.server.service.CommentService;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentDao commentDao;
    @Resource
    private UserDao userDao;
    @Resource
    private OrderDao orderDao;
    @Resource
    private IdleItemDao idleItemDao;

    @Transactional
    public boolean addComment(CommentModel c) {
        if (commentDao.countByOrder(c.getOrderId()) > 0) {
            return false;
        }
        boolean ok = commentDao.insert(c) == 1;
        if (ok) {
            orderDao.updateCommentFlag(c.getOrderId(), 1);

            // 计算并更新平均分
            // 1. 查出该商品的所有评价
            List<CommentModel> list = commentDao.listByIdle(c.getIdleId());
            if (list.size() > 0) {
                double sum = 0;
                for (CommentModel cm : list) {
                    sum += cm.getRating();
                }
                double avg = Math.round((sum / list.size()) * 10.0) / 10.0;

                // 2. 更新到商品表
                idleItemDao.updateRating(c.getIdleId(), avg);
            }
        }
        return ok;
    }

    public List<CommentModel> listComment(Long idleId) {
        List<CommentModel> list = commentDao.listByIdle(idleId);
        list.forEach(o -> o.setBuyer(userDao.selectByPrimaryKey(o.getBuyerId())));
        return list;
    }

    @Override
    public PageVo<CommentModel> getAllComments(String searchValue, int page, int nums) {
        List<CommentModel> list = commentDao.getAllComments(searchValue, (page - 1) * nums, nums);
        for (CommentModel c : list) {
            c.setBuyer(userDao.selectByPrimaryKey(c.getBuyerId()));
            c.setIdleItem(idleItemDao.selectByPrimaryKey(c.getIdleId()));
        }
        int count = commentDao.countAllComments(searchValue);
        return new PageVo<>(list, count);
    }

    @Override
    public boolean addAdminComment(CommentModel c) {
        return commentDao.insert(c) == 1;
    }

    @Override
    public boolean updateAdminComment(CommentModel c) {
        return commentDao.updateByPrimaryKeySelective(c) == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long id) {
        // 1. 删除前先查询，为了获取 idleId
        CommentModel comment = commentDao.selectByPrimaryKey(id);
        if (comment == null) {
            return false; // 评价不存在
        }

        Long idleId = comment.getIdleId();

        // 2. 执行删除
        if (commentDao.deleteByPrimaryKey(id) == 1) {

            // 3. 重新计算该商品的平均分
            List<CommentModel> list = commentDao.listByIdle(idleId);

            double avg = 0.0;
            if (list != null && list.size() > 0) {
                double sum = 0;
                for (CommentModel cm : list) {
                    sum += cm.getRating();
                }
                avg = Math.round((sum / list.size()) * 10.0) / 10.0;
            } else {
                // 如果没有评论了，重置为 0.0
                avg = 0.0;
            }

            // 4. 更新商品表
            idleItemDao.updateRating(idleId, avg);

            // 5. 同时更新订单表的状态 (可选优化)
            // 如果删除了评价，是否要把订单状态改回"未评价"？
            // 按照业务逻辑，通常管理员删除违规评价后，不应该让用户再评一次，
            // 也不应该显示"待评价"，所以这里我们只更新分数，不动订单状态。

            return true;
        }

        return false;
    }
}