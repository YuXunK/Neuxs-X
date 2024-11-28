package org.yuxun.x.nexusx.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.yuxun.x.nexusx.Entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * @author yuxun
     * @Desc 用于查询用户个人信息
     */
    @Select("SELECT users.user_id, users.username, users.password, users.phone, users.email, users.status, users.last_login_time from users where email or phone = #{PhoneorEmail}")
    User findByPhoneOrEmail(String PhoneOrEmail);
}
