insert into error_messages(code, locale, message, `systemCode`, type) values ('BASEBP0001', 'zh_CN','未知系统异常，请联系系统管理员。', 'SAFE','BIZ');
insert into error_messages(code, locale, message, `systemCode`, type) values ('BASEBP0025', 'zh_CN','您输入的密码不正确，今日还剩{0}次机会。', 'SAFE','BIZ');
insert into error_messages(code, locale, message, `systemCode`, type) values ('BASEBP0026', 'zh_CN','您输入密码的长度不正确，当前密码长度为{0}。', 'SAFE','BIZ');
select * from error_messages;