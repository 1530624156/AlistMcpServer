package com.mavis.mavismcpserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * (TAlistConfig)表实体类
 *
 * @author
 * @since 2026-04-10 01:30:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TAlistConfig {

    private String url;

    private String username;

    private String password;

}


