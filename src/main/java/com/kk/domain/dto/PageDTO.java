package com.kk.domain.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.domain.po.User;
import com.kk.domain.vo.UserVO;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果
 */
@Data
public class PageDTO<T> {
    /**
     * 总条数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> list;

    public static <PO, VO> PageDTO<VO> of(Page<PO> p, Class<VO> clazz) {
        PageDTO<VO> dto = new PageDTO<>();
        // 1.总条数
        dto.setTotal(p.getTotal());
        // 2.总页数
        dto.setPages(p.getPages());
        // 3.类型转换
        if (CollUtil.isEmpty(p.getRecords())) {
            dto.setList(Collections.emptyList());
            return dto;
        }
        dto.setList(BeanUtil.copyToList(p.getRecords(), clazz));
        // 4.返回
        return dto;
    }

    public static <PO, VO> PageDTO<VO> of(Page<PO> p, Function<PO,VO> convertor) {
        PageDTO<VO> dto = new PageDTO<>();
        // 1.总条数
        dto.setTotal(p.getTotal());
        // 2.总页数
        dto.setPages(p.getPages());
        // 3.类型转换
        if (CollUtil.isEmpty(p.getRecords())) {
            dto.setList(Collections.emptyList());
            return dto;
        }
        dto.setList(p.getRecords().stream().map(convertor).collect(Collectors.toList()));
        // 4.返回
        return dto;
    }
}
