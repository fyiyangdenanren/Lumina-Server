package com.kk.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartSpec {
    // 图表类型，比如 bar, pie, line
    private String type;
    // 图表标题
    private String title;
    // X 轴或标签
    private List<String> x;
    // Y 轴数据
    private List<Double> y;
    // 某些图表（如 radar）需要多组数据
    private List<List<Double>> series;
}
