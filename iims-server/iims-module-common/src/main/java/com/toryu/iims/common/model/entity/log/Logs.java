package com.toryu.iims.common.model.entity.log;

import com.toryu.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@Builder
public class Logs extends BaseTable {

    private Long id;

    private String level;

    private String threadName;

    private String message;

    private String loggerName;

    private LocalDateTime createTime;

}
