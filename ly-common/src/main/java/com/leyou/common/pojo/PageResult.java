package com.leyou.common.pojo;


import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long total; //总记录数
    private long totalPage; //总页数
    private List<T> items; //当前页数据

    public PageResult() {
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Long totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }
}
