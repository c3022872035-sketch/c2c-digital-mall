package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.CommentModel;
import com.second.hand.trading.server.vo.PageVo;

import java.util.List;

public interface CommentService {
    boolean addComment(CommentModel c);

    List<CommentModel> listComment(Long idleId);

    PageVo<CommentModel> getAllComments(String searchValue, int page, int nums);

    boolean addAdminComment(CommentModel commentModel);

    boolean updateAdminComment(CommentModel commentModel);

    boolean deleteComment(Long id);


}