package org.yuxun.x.nexusx.Utils;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class MybatisSqlInterceptor implements InnerInterceptor {

    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, Invocation invocation) {
        System.out.println("SQL ID: " + ms.getId());
        System.out.println("SQL Command: " + ms.getSqlCommandType());
        System.out.println("SQL Parameter: " + parameter);
    }
}
