create table if not exists broker_message
(
    message_id varchar(128) not null comment '消息id' primary key,
    message text null comment '消息内容',
    try_count int(4) default 0 null comment '重试次数',
    status varchar(10) default '' null comment '状态',
    next_retry timestamp  comment '下次重试时间',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
);

