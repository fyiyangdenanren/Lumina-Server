package com.kk.domain.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageQuery {
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    /**
     * 每页数量
     */
    private Integer pageSize = 5;
    /**
     * 排序字段
     */
    private String sortBy;
    /**
     * 排序方式: 是否升序
     */
    private Boolean isAsc = true;

    public <T> Page<T> toMpPage(OrderItem... items) {
        Page<T> page = Page.of(pageNum, pageSize);
        if (StrUtil.isNotBlank(sortBy)) {
            // 不为空
            OrderItem o = new OrderItem();
            o.setColumn(sortBy);
            o.setAsc(isAsc);
            page.addOrder(o);
        } else if (items != null) {
            // 不为空，默认排序
            page.addOrder(items);
        }
        return page;
    }

    public <T> Page<T> toMpPage(String defaultSortBy, Boolean defaultAsc) {
        return toMpPage(new OrderItem().setColumn(defaultSortBy).setAsc(defaultAsc));
    }

    public <T> Page<T> toMpPageDefaultSortByCreateTime() {
        return toMpPage(new OrderItem().setColumn("create_time").setAsc(false));
    }

    public <T> Page<T> toMpPageDefaultSortByUpdateTime() {
        return toMpPage(new OrderItem().setColumn("update_time").setAsc(false));
    }
}
