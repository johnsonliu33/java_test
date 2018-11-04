
CREATE TABLE `t_accusation_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goodsid` bigint(20) NOT NULL COMMENT '商品id',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `identity_card` varchar(20) DEFAULT NULL COMMENT '身份证',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
  `content` varchar(512) DEFAULT NULL COMMENT '举报内容',
  `urlA` varchar(128) DEFAULT NULL COMMENT '图片地址1',
  `urlB` varchar(128) DEFAULT NULL COMMENT '图片地址2',
  `urlC` varchar(128) DEFAULT NULL COMMENT '图片地址3',
  `urlD` varchar(128) DEFAULT NULL COMMENT '图片地址4',
  `urlE` varchar(128) DEFAULT NULL COMMENT '图片地址5',
  `urlF` varchar(128) DEFAULT NULL COMMENT '图片地址6',
  `urlG` varchar(128) DEFAULT NULL COMMENT '图片地址7',
  `urlH` varchar(128) DEFAULT NULL COMMENT '图片地址8',
  `dispose_content` varchar(512) DEFAULT NULL COMMENT '处理内容',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '举报类型：1、匿名举报  2、实名举报',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：1、处理中 2、已处理',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `t_admin` (
  `aid` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '名称',
  `login_name` varchar(128) NOT NULL COMMENT '账户名',
  `login_pwd` varchar(64) NOT NULL COMMENT '登录密码',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户类型：1、监管部门  2、供应商  3、经销商',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：1、待审核 2、正常 3、冻结 4、审核未通过',
  `recall_num` int(11) DEFAULT '0' COMMENT '召回次数',
  `report_num` int(11) DEFAULT '0' COMMENT '举报次数',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `last_login_time` bigint(20) DEFAULT NULL COMMENT '最后登录时间',
  `update_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  `audit_opinion` varchar(128) DEFAULT NULL COMMENT '审核意见',
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8;

CREATE TABLE `t_admin_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aid` bigint(20) NOT NULL COMMENT '经销商id',
  `pay_pwd` varchar(64) NOT NULL COMMENT '流通密码',
  `private_key` varchar(100) NOT NULL COMMENT '私钥',
  `public_key` varchar(100) NOT NULL COMMENT '公钥',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `public_key` (`public_key`),
  KEY `aid` (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `t_announcement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `body` mediumtext,
  `times_view` int(11) DEFAULT '0',
  `status` tinyint(4) DEFAULT '0' COMMENT '1 关闭',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `t_circulate_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goodsid` bigint(20) NOT NULL COMMENT '商品id',
  `from_aid` bigint(20) NOT NULL COMMENT '供应商id',
  `from_address` varchar(100) NOT NULL COMMENT '商品地址',
  `peer_aid` bigint(20) NOT NULL COMMENT '经销商方id',
  `peer_address` varchar(100) NOT NULL COMMENT '经销商地址',
  `amount` int(11) NOT NULL COMMENT '数量',
  `type` tinyint(4) NOT NULL COMMENT '1、转出 2、转入',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `from_aid` (`from_aid`),
  KEY `goodsid` (`goodsid`),
  KEY `from_address` (`from_address`)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8;
ALTER TABLE t_circulate_history ADD hash_id VARCHAR(256) DEFAULT NULL COMMENT 'hash号';

CREATE TABLE `t_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aid` bigint(20) NOT NULL COMMENT '所属供应商',
  `name` varchar(128) NOT NULL COMMENT '商品名称',
  `pay_pwd` varchar(64) DEFAULT NULL COMMENT '流通密码',
  `number` int(11) DEFAULT '0' COMMENT '数量',
  `circulate_num` int(11) DEFAULT '0' COMMENT '已流通数量',
  `recall_num` int(11) DEFAULT '0' COMMENT '已召回数量',
  `private_key` varchar(128) DEFAULT NULL COMMENT '私钥',
  `money_address` varchar(128) DEFAULT NULL COMMENT '钱包地址',
  `status` tinyint(4) DEFAULT '2' COMMENT '状态：1、待完善 2、待审核 3、审核未通过  4、正常  5、召回',
  `cause` varchar(128) DEFAULT NULL COMMENT '原因',
  `sell_aid` bigint(20) DEFAULT NULL COMMENT '流通商家',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `report_num` int(11) DEFAULT '0' COMMENT '已被举报次数',
  `dispose_num` int(11) DEFAULT '0' COMMENT '已处理举报次数',
  `licence` varchar(128) DEFAULT NULL COMMENT '生产许可证',
  `brand` varchar(128) DEFAULT NULL COMMENT '品牌',
  `specification` varchar(128) DEFAULT NULL COMMENT '规格',
  `burden_sheet` varchar(128) DEFAULT NULL COMMENT '配料表',
  `store` varchar(128) DEFAULT NULL COMMENT '存储方式',
  `expiration_date` varchar(128) DEFAULT NULL COMMENT '过期时间',
  `works_name` varchar(128) DEFAULT NULL COMMENT '厂名',
  `works_address` varchar(256) DEFAULT NULL COMMENT '厂址',
  `works_phone` varchar(128) DEFAULT NULL COMMENT '联系地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100001 DEFAULT CHARSET=utf8;
ALTER TABLE t_goods ADD hash_id VARCHAR(256) DEFAULT NULL COMMENT 'hash号';

CREATE TABLE `t_goods_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gid` bigint(20) NOT NULL COMMENT '商品id',
  `pay_pwd` varchar(64) DEFAULT NULL COMMENT '流通密码',
  `private_key` varchar(100) NOT NULL COMMENT '私钥',
  `public_key` varchar(100) NOT NULL COMMENT '公钥',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `public_key` (`public_key`),
  KEY `gid` (`gid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `t_memo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aid` bigint(20) DEFAULT NULL COMMENT '商户id',
  `goodsid` bigint(20) DEFAULT NULL COMMENT '商品id',
  `content` varchar(256) DEFAULT NULL COMMENT '内容',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE t_memo ADD hash_id VARCHAR(256) DEFAULT NULL COMMENT 'hash号';


ALTER TABLE `t_goods` DROP COLUMN pay_pwd;
ALTER TABLE `t_goods` DROP COLUMN private_key;
ALTER TABLE `t_goods` DROP COLUMN money_address;

CREATE TABLE `t_goods_reports` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gid` bigint(20) NOT NULL COMMENT '商品id',
  `facade` varchar(128) DEFAULT NULL COMMENT '外观、色泽',
  `moisture` double(8,2) DEFAULT NULL COMMENT '水分',
  `impurity` double(8,2) DEFAULT NULL COMMENT '杂质',
  `unsound_grain` double(8,2) DEFAULT NULL COMMENT '不完善粒',
  `oleaginousness` double(8,2) DEFAULT NULL COMMENT '含油量（湿态）',
  `aflatoxin` double(8,2) DEFAULT NULL COMMENT '黄曲霉毒素B1',
  `gong_content` double(8,2) DEFAULT NULL COMMENT '贡含量',
  `hch_residual` double(8,2) DEFAULT NULL COMMENT '六六六残留量',
  `ddt_residual` double(8,2) DEFAULT NULL COMMENT 'DDT残留量',
  `defective_particle` double(8,2) DEFAULT NULL COMMENT '有缺点颗粒',
  `annual_output_of` varchar(128) DEFAULT NULL COMMENT '年产度',
  `place_of_origin` varchar(128) DEFAULT NULL COMMENT '产地',
  `reportsid` bigint(20) DEFAULT NULL COMMENT '质检员id',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `gid` (`gid`),
  KEY `reportsid` (`reportsid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8
ALTER TABLE t_goods_reports ADD hash_id VARCHAR(256) DEFAULT NULL COMMENT 'hash号';
