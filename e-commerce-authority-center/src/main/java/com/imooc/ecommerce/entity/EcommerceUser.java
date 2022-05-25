package com.imooc.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户表实体类定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_ecommerce_user")
public class EcommerceUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * MD5 密码
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 额外信息
     */
    @Column(name = "extraInfo", nullable = false)
    private String extraInfo;

    /**
     * 创建日期
     *
     * @CreatedDate 插入时自动生成创建日期 需要配合@EntityListeners(AuditingEntityListener.class) 和 @EnableJpaAuditing 使用
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    /**
     * 更新时间
     **/
    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

}
