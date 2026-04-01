package com.ethereal.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private List<T> records;
    private long total;
    private long current;
    private long size;

    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setCurrent(current);
        pageResult.setSize(size);
        return pageResult;
    }
}
