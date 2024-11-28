package org.yuxun.x.nexusx.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.yuxun.x.nexusx.Entity.Operation_logs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OperationMapper extends BaseMapper<Operation_logs> {

    /**
     * 分析日志中的操作状态统计
     * @param start 开始时间
     * @param end 结束时间
     * @return 状态统计数据
     */
    Map<String, Long> countByStatus(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    /**
     * 按操作分组统计日志
     * @param start 开始时间
     * @param end 结束时间
     * @return 按操作名称分组的统计数据
     */
    List<Map<String, Object>> groupByOperation(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    /**
     * 按条件查询日志
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    List<Operation_logs> queryLogs(@Param("userId") Long userId,
                                   @Param("deviceId") Long deviceId,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 删除指定时间之前的日志
     * @param cutoffTime 截止时间
     * @return 删除的行数
     */
    int clearOldLogs(@Param("cutoffTime") LocalDateTime cutoffTime);
}
