package org.yuxun.x.nexusx.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.yuxun.x.nexusx.Entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
//    通过Phone或是Email去查询user
    @Select("SELECT userId,username,password,phone,email,status,lastLoginTime FROM users WHERE phone = #{phoneOrEmail} OR email = #{phoneOrEmail}")
    User findByPhoneOrEmail(String phoneOrEmail);
    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查手机号码是否存在
    boolean existsByPhone(String phone);

    // 检查邮箱地址是否存在
    boolean existsByEmail(String email);

    // 保存用户信息
    int insertUser(User user);
}
