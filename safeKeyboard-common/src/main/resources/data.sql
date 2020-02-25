delete from errors_messages;
insert into errors_messages(code, locale, message, `systemCode`, type) values ('BASEBP0001', 'zh_CN','未知系统异常，请联系系统管理员。', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('BASEBP0001', 'en','Unknown system exception, please contact system administrator.', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('BASEBP0002', 'zh_CN','系统忙，请检查细节并确认结果。', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('BASEBP0002', 'en','The system is busy, please check the details and confirm the result', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('BASEBP0025', 'zh_CN','您输入的密码不正确，今日还剩{0}次机会。', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('BASEBP0026', 'zh_CN','您输入密码的长度不正确，当前密码长度为{0}。', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('org.springframework.dao.DataIntegrityViolationException', 'zh_CN','BASEBP0001', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('org.springframework.dao.DataIntegrityViolationException', 'en','BASEBP0002', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('java.util.concurrent.TimeoutException', 'zh','BASEBP0002', 'SAFE','BIZ');
insert into errors_messages(code, locale, message, `systemCode`, type) values ('org.springframework.dao.DataIntegrityViolationException', 'en','BASEBP0002', 'SAFE','BIZ');
select * from errors_messages;



delete from values_messages;
insert into values_messages(code, locale, message) values ('loginForm.email.input', 'zh_CN','请输入邮箱地址。');
insert into values_messages(code, locale, message) values ('loginForm.email.input', 'en','Please enter your email address.');
insert into values_messages(code, locale, message) values ('loginForm.name.input', 'zh_CN','请输入用户名。');
insert into values_messages(code, locale, message) values ('loginForm.name.input', 'en','Please enter your user name。');
insert into values_messages(code, locale, message) values ('loginForm.password.input', 'zh_CN','请输入密码。');
insert into values_messages(code, locale, message) values ('loginForm.password.input', 'en','Please enter your password.');
select * from values_messages;