package com.kk.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kk.enums.MethodType;
import com.kk.enums.Verified;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@TableName("user")
public class User {
    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 认证方式
     */
    @TableField("method_type")
    private MethodType methodType;

    /**
     * 唯一标识
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 凭证（如密码、token等）
     */
    @TableField("credential")
    private String credential;

    /**
     * 是否已验证
     */
    @TableField("verified")
    private Verified verified;

    /**
     * 头像
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer isDeleted = 0;

    /**
     * 创建时间
     */
    @TableField("created_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String updatedTime;
}
