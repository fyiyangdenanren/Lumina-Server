package com.kk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kk.domain.po.Msg;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author kk
 * @since 2025-08-02
 */
@Mapper
public interface MessageMapper extends BaseMapper<Msg> {

}
