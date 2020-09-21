package org.zhq.rabbit.task.enums;

public enum ElasticJobTypeEnum {

    SIMPLE("SimpleJob", "简单任务"),
    DATAFLOW("DataflowJob", "流式任务"),
    SCRIPT("ScriptJob", "脚本任务");

    String type;
    String name;

    ElasticJobTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
