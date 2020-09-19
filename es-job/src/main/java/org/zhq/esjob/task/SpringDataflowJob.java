package org.zhq.esjob.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import org.zhq.esjob.pojo.Foo;

import java.util.List;

public class SpringDataflowJob implements DataflowJob<Foo> {
    @Override
    public List fetchData(ShardingContext shardingContext) {
        System.out.println("===========fetchData===========");
        return null;
    }

    @Override
    public void processData(ShardingContext shardingContext, List list) {
        System.out.println("===========processData===========");
    }
}
