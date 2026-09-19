package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.ReportModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportDao {
    int insert(ReportModel record);

    // 统计待处理数量
    int countPending();

    // 获取列表（带分页和状态筛选）
    List<ReportModel> getReportList(@Param("status") Integer status,
                                    @Param("searchValue") String searchValue,
                                    @Param("begin") int begin,
                                    @Param("nums") int nums);
    // 统计总数（用于分页）
    int countReport(@Param("status") Integer status,
                    @Param("searchValue") String searchValue);

    // 更新状态（处理投诉）
    int updateStatus(@Param("id") Long id, @Param("status") Byte status);

    // 通过ID查询（用于关联查询等，可选）
    ReportModel selectByPrimaryKey(Long id);
}